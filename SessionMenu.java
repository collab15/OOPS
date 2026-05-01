import java.util.List;
import java.util.Scanner;

// Three-phase session screen.
// SELECTING      — pick a task from the list
// CHOOSING_TIMER — pick Pomodoro (25 min) or a custom duration
// ACTIVE         — session is running; a background thread redraws the
//                  countdown every second without needing a keypress
public class SessionMenu extends Menu {

    private enum Mode { SELECTING, CHOOSING_TIMER, ACTIVE }

    private final SessionManager sessionManager;
    private List<Task> tasks;
    private Task selectedTask = null;
    private Mode mode = Mode.SELECTING;

    private final Scanner scanner = new Scanner(System.in);

    // redraws the timer every second while a session is active
    private Thread refreshThread;
    private volatile boolean refreshing = false;

    public SessionMenu(List<Task> tasks, SessionManager sessionManager) {
        this.tasks          = tasks;
        this.sessionManager = sessionManager;
        enterSelectingMode();
    }

    // starts the background redraw loop
    private void startRefreshThread() {
        refreshing    = true;
        refreshThread = new Thread(() -> {
            while (refreshing) {
                try {
                    Thread.sleep(1000);
                    if (refreshing && mode == Mode.ACTIVE) {
                        render();

                        // timer finished naturally — go back to selecting
                        if (!sessionManager.isSessionActive()) {
                            refreshing = false;
                            mode       = Mode.SELECTING;
                            enterSelectingMode();
                            render();
                        }
                    }
                } catch (InterruptedException e) {
                    // stopped intentionally by stopRefreshThread()
                }
            }
        }, "screen-refresh-thread");
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    private void stopRefreshThread() {
        refreshing = false;
        if (refreshThread != null) refreshThread.interrupt();
    }

    private void enterSelectingMode() {
        stopRefreshThread();
        mode         = Mode.SELECTING;
        selectedTask = null;
        currentIndex = 0;
        menuItems.clear();
        menuSelections.clear();

        if (tasks.isEmpty()) {
            setMenuSelections("Back");
        } else {
            for (Task t : tasks) menuSelections.add(t.getName());
        }
    }

    private void enterChoosingTimerMode(Task task) {
        mode         = Mode.CHOOSING_TIMER;
        selectedTask = task;
        currentIndex = 0;
        menuSelections.clear();
        setMenuSelections("Pomodoro  (25 min)", "Custom Duration", "Back");
    }

    private void enterActiveMode() {
        mode         = Mode.ACTIVE;
        currentIndex = 0;
        menuSelections.clear();
        setMenuSelections("Pause", "Stop Session");
        startRefreshThread();
    }

    private void enterPausedMode() {
        currentIndex = 0;
        menuSelections.clear();
        setMenuSelections("Resume", "Stop Session");
    }

    @Override
    protected String getItemsHeader() {
        switch (mode) {
            case SELECTING:      return "SELECT A TASK";
            case CHOOSING_TIMER: return "CHOOSE TIMER  -  " + selectedTask.getName();
            case ACTIVE:         return sessionManager.isSessionPaused()
                                        ? "SESSION PAUSED"
                                        : "SESSION IN PROGRESS";
            default:             return "";
        }
    }

    @Override
    protected void render() {

        UI.cls();

        String status = Status.get() + " ";

        System.out.println();
        UI.printFullWidth("*");
        UI.printCenter("████████   █████    █████   ██   ██    ██   ██");
        UI.printCenter("   ██     ██   ██   ██      ██  ██      ██ ██ ");
        UI.printCenter("   ██     ███████   █████   █████        ███   ");
        UI.printCenter("   ██     ██   ██      ██   ██  ██      ██ ██  ");
        UI.printCenter("   ██     ██   ██   █████   ██   ██    ██   ██");
        UI.printFullWidth("-");
        UI.printAtMargins(Utils.getWeekDayAndDate() + " ", status);
        UI.printFullWidth("=");
        UI.printEmpty();
        UI.printCenter("--- " + getItemsHeader() + " ---");
        UI.printEmpty();

        if (mode == Mode.ACTIVE) {

            Task current = sessionManager.getCurrentTask();
            if (current != null) {
                UI.printCenter("TASK : " + current.getName());
            }

            UI.printEmpty();
            UI.printCenter(formatTime(sessionManager.getTimeRemaining()));
            UI.printEmpty();

            if (sessionManager.isSessionPaused()) {
                UI.printCenter("[ PAUSED ]");
            }

        } else if (mode == Mode.SELECTING) {

            if (tasks.isEmpty()) {
                UI.printCenter("No tasks available.");
            } else {
                UI.printCenter("UP / DOWN to scroll   ENTER to select   BACKSPACE to go back");
            }

        } else if (mode == Mode.CHOOSING_TIMER) {

            UI.printCenter("Pomodoro  :  25 minutes, classic focus session");
            UI.printEmpty();
            UI.printCenter("Custom    :  you enter the duration in minutes");
        }

        UI.printEmpty();
        UI.printFullWidth("-");

        for (int i = 0; i < menuSelections.size(); i++) {
            if (i == currentIndex) {
                UI.printCenter("<[ " + menuSelections.get(i) + " ]>");
            } else {
                UI.printCenter(menuSelections.get(i));
            }
            UI.printEmpty();
        }

        UI.printFullWidth("=");
    }

    @Override
    public Menu display() {

        render();

        while (true) {

            String key = KeyboardListener.listen();

            if ("UP".equals(key))   { moveUp();   render(); continue; }
            if ("DOWN".equals(key)) { moveDown(); render(); continue; }

            if ("BACKSPACE".equals(key)) {
                if (mode == Mode.ACTIVE) {
                    stopRefreshThread();
                    sessionManager.stopSession();
                    enterSelectingMode();
                    render();
                    continue;
                } else if (mode == Mode.CHOOSING_TIMER) {
                    enterSelectingMode();
                    render();
                    continue;
                } else {
                    return null;
                }
            }

            if ("ENTER".equals(key)) {
                Menu next = handleSelection();
                if (next != this) return next;
                render();
            }
        }
    }

    @Override
    Menu handleSelection() {

        // SELECTING: record the user's choice, let the AI observe it,
        // then move to the timer picker
        if (mode == Mode.SELECTING) {

            if (tasks.isEmpty()) return null;

            List<Task> suggestions = AIEngine.suggestTasks();
            Task selected          = tasks.get(currentIndex);
            selectedTask           = selected;

            Task topSuggested = suggestions.isEmpty() ? selected : suggestions.get(0);

            // learning runs on a background thread so it doesn't block the UI
            new Thread(() -> {
                AIEngine.observeTaskSelection(selected, topSuggested);
                AIEngine.learn();
            }).start();

            enterChoosingTimerMode(selected);
            return this;
        }

        // CHOOSING_TIMER: start the session with the chosen timer
        if (mode == Mode.CHOOSING_TIMER) {

            switch (currentIndex) {
                case 0: // Pomodoro
                    sessionManager.startSession(selectedTask, new PomodoroTimer(sessionManager));
                    enterActiveMode();
                    return this;

                case 1: // Custom duration
                    int minutes = askForDuration();
                    if (minutes > 0) {
                        sessionManager.startSession(
                            selectedTask,
                            new SimpleTimer(sessionManager, minutes * 60)
                        );
                        enterActiveMode();
                    }
                    return this;

                case 2: // Back
                    enterSelectingMode();
                    return this;
            }
        }

        // ACTIVE: pause/resume/stop controls
        String selected = menuSelections.get(currentIndex);

        if ("Pause".equals(selected)) {
            sessionManager.pauseSession();
            enterPausedMode();
        }

        if ("Resume".equals(selected)) {
            sessionManager.resumeSession();
            enterActiveMode();
        }

        if ("Stop Session".equals(selected)) {
            stopRefreshThread();
            sessionManager.stopSession();
            enterSelectingMode();
        }

        return this;
    }

    // temporarily hands control to Scanner for a number input,
    // then gives control back to KeyboardListener when done
    private int askForDuration() {

        KeyboardListener.pause();

        int minutes = 0;

        while (true) {

            UI.cls();

            String status = "ONLINE".equals(Status.get())
                    ? Status.get() + " "
                    : Status.get() + " - Restart to sync";

            System.out.println();
            UI.printFullWidth("*");
            UI.printCenter("████████   █████    █████   ██   ██    ██   ██");
            UI.printCenter("   ██     ██   ██   ██      ██  ██      ██ ██ ");
            UI.printCenter("   ██     ███████   █████   █████        ███   ");
            UI.printCenter("    ██     ██   ██      ██   ██  ██      ██ ██  ");
            UI.printCenter("   ██     ██   ██   █████   ██   ██    ██   ██");
            UI.printFullWidth("-");
            UI.printAtMargins(Utils.getWeekDayAndDate() + " ", status);
            UI.printFullWidth("=");
            UI.printEmpty();
            UI.printCenter("--- " + getItemsHeader() + " ---");
            UI.printEmpty();
            UI.printCenter("Enter duration (1 - 120 minutes)");
            UI.inputCenter("Minutes: ");

            String input = scanner.nextLine().trim();

            try {
                minutes = Integer.parseInt(input);
                if (minutes >= 1 && minutes <= 120) break;
            } catch (Exception e) {
                UI.printCenter("Invalid input");
            }
        }

        KeyboardListener.resume();
        return minutes;
    }

    // formats seconds as MM : SS for the countdown display
    private String formatTime(int totalSeconds) {
        return String.format("%02d : %02d",
                totalSeconds / 60,
                totalSeconds % 60);
    }
}
