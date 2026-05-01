import java.util.List;
import java.util.Scanner;

// Three-phase menu:
//   Phase 1 (SELECTING)      — pick a task from the list
//   Phase 2 (CHOOSING_TIMER) — pick Pomodoro (25 min) or Custom duration
//   Phase 3 (ACTIVE)         — session running, can pause/resume or stop
//
// In ACTIVE mode a refresh thread redraws the screen every second so the
// countdown is visible without needing a keypress.
public class SessionMenu extends Menu {

    private enum Mode { SELECTING, CHOOSING_TIMER, ACTIVE }
    private final SessionManager sessionManager;
    private List<Task> tasks;
    private Task selectedTask = null;
    private Mode mode = Mode.SELECTING;

    private final Scanner scanner = new Scanner(System.in);

    // ---- Refresh thread fields ----
    private Thread refreshThread;
    private volatile boolean refreshing = false;

    public SessionMenu(List<Task> tasks, SessionManager sessionManager) {
        this.tasks          = tasks;
        this.sessionManager = sessionManager;
        enterSelectingMode();
    }

    // ---- Refresh thread ----

    // Starts a background thread that redraws the screen every second.
    // Only active during ACTIVE mode so we can see the countdown tick.
    private void startRefreshThread() {
        refreshing    = true;
        refreshThread = new Thread(() -> {
            while (refreshing) {
                try {
                    Thread.sleep(1000);
                    if (refreshing && mode == Mode.ACTIVE) {
                        render();

                        // Timer finished naturally — session ended on its own
                        // onTimerComplete() already ran in SessionManager,
                        // so we just exit active mode and go back to selecting
                        if (!sessionManager.isSessionActive()) {

                            refreshing = false;
                            mode = Mode.SELECTING;
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

    // Stops the refresh thread cleanly.
    private void stopRefreshThread() {
        refreshing = false;
        if (refreshThread != null) {
            refreshThread.interrupt();
        }
    }

    // ---- Mode transitions ----

    private void enterSelectingMode() {
        stopRefreshThread(); // always stop refreshing when leaving ACTIVE
        mode         = Mode.SELECTING;
        selectedTask = null;
        currentIndex = 0;
        menuItems.clear();
        menuSelections.clear();

        if (tasks.isEmpty()) {
            setMenuSelections("Back");
        } else {
            for (Task t : tasks) {
                menuSelections.add(t.getName());
            }
        }
    }

    private void enterChoosingTimerMode(Task task) {
        mode         = Mode.CHOOSING_TIMER;
        selectedTask = task;
        currentIndex = 0;
        menuSelections.clear();
        setMenuSelections(
            "Pomodoro  (25 min)",
            "Custom Duration",
            "Back"
        );
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

    // ---- Header ----

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

    // ---- Custom render ----

    @Override
    protected void render() {

        String status="Status Couldn't be determined";

        UI.cls();

        status = Status.get() + " ";

        System.out.println();
        UI.printFullWidth("*");
        UI.printCenter("████████   █████    █████   ██   ██    ██   ██");
        UI.printCenter("   ██     ██   ██   ██      ██  ██      ██ ██ ");
        UI.printCenter("   ██     ███████   █████   █████        ███   ");
        UI.printCenter("   ██     ██   ██      ██   ██  ██      ██ ██  ");
        UI.printCenter("   ██     ██   ██   █████   ██   ██    ██   ██");
        UI.printFullWidth("-");
        UI.printAtMargins( Utils.getWeekDayAndDate()+" ", status);
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
        }

        else if (mode == Mode.SELECTING) {

            if (tasks.isEmpty()) {
                UI.printCenter("No tasks available.");
            } else {
                UI.printCenter("UP / DOWN to scroll   ENTER to select   BACKSPACE to go back");
            }
        }

        else if (mode == Mode.CHOOSING_TIMER) {

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

    // ---- Display loop ----

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

    // ---- Handle selection ----

    @Override
    Menu handleSelection() {

        // ---- SELECTING ----
        if (mode == Mode.SELECTING) {

            if (tasks.isEmpty()) return null;

            List<Task> suggestions = AIEngine.suggestTasks();

            Task selected = tasks.get(currentIndex);
            selectedTask = selected;

            Task topSuggested = suggestions.isEmpty() ? selected : suggestions.get(0);

            new Thread(() -> {
                AIEngine.observeTaskSelection(selected, topSuggested);
                AIEngine.learn(); // learn from selection
            }).start();

            enterChoosingTimerMode(selected);
            return this;
        }

        // ---- CHOOSING TIMER ----
        if (mode == Mode.CHOOSING_TIMER) {

            switch (currentIndex) {

                case 0:
                    sessionManager.startSession(selectedTask, new PomodoroTimer(sessionManager));
                    enterActiveMode();
                    return this;

                case 1:
                    int minutes = askForDuration();
                    if (minutes > 0) {
                        sessionManager.startSession(
                            selectedTask,
                            new SimpleTimer(sessionManager, minutes * 60)
                        );
                        enterActiveMode();
                    }
                    return this;

                case 2:
                    enterSelectingMode();
                    return this;
            }
        }

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

    // ---- Ask for custom duration ----

    private int askForDuration() {

        KeyboardListener.pause();

        int minutes = 0;

        String status="Status Couldn't be determined";

        while (true) {
            
            UI.cls();

            if ("ONLINE".equals(Status.get())){
                status = Status.get() + " " ;
            }else{
                status= Status.get() + " - Restart to sync";
            }
            System.out.println();
            UI.printFullWidth("*");
            UI.printCenter("████████   █████    █████   ██   ██    ██   ██");
            UI.printCenter("   ██     ██   ██   ██      ██  ██      ██ ██ ");
            UI.printCenter("   ██     ███████   █████   █████        ███   ");
            UI.printCenter("    ██     ██   ██      ██   ██  ██      ██ ██  ");
            UI.printCenter("   ██     ██   ██   █████   ██   ██    ██   ██");
            UI.printFullWidth("-");
            UI.printAtMargins( Utils.getWeekDayAndDate()+" ", status );
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

    // ---- Utilities ----

    private String formatTime(int totalSeconds) {
        return String.format("%02d : %02d",
                totalSeconds / 60,
                totalSeconds % 60);
    }
}
