import java.util.List;

// Two-phase menu:
//   Phase 1 (BROWSING) — scroll through all pending tasks and pick one
//   Phase 2 (ACTING)   — choose what to do: Start Task, Remove Task, or Back
public class PendingTasksViewMenu extends Menu {

    private enum Mode { BROWSING, ACTING }

    private final TaskManager taskManager;
    private final SessionManager sessionManager;
    private Mode mode = Mode.BROWSING;
    private Task selectedTask = null;

    public PendingTasksViewMenu(TaskManager taskManager, SessionManager sessionManager) {
        this.taskManager    = taskManager;
        this.sessionManager = sessionManager;
        enterBrowsingMode();
    }

    // ---- Mode transitions ----

    private void enterBrowsingMode() {
        mode         = Mode.BROWSING;
        selectedTask = null;
        currentIndex = 0;
        menuItems.clear();
        menuSelections.clear();

        List<Task> tasks = taskManager.getPendingTasks();
        if (tasks.isEmpty()) {
            setMenuSelections("No Tasks — Back");
        } else {
            for (Task t : tasks) {
                menuSelections.add(t.getName());
            }
        }
    }

    private void enterActingMode(Task task) {
        mode         = Mode.ACTING;
        selectedTask = task;
        currentIndex = 0;
        menuSelections.clear();
        setMenuSelections("Start Task", "Remove Task", "Back");
    }

    // ---- Header ----

    @Override
    protected String getItemsHeader() {
        return mode == Mode.ACTING
                ? "SELECTED: " + selectedTask.getName()
                : "PENDING TASKS";
    }

    // ---- Custom render ----

    @Override
    protected void render() {

        String status="Status Couldn't be determined";

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

        if (mode == Mode.BROWSING) {
            List<Task> tasks = taskManager.getPendingTasks();
            if (tasks.isEmpty()) {
                UI.printCenter("You have no pending tasks.");
            } else {
                UI.printCenter(tasks.size() + " task(s)   UP / DOWN to scroll   ENTER to select");
            }
        } else {
            UI.printCenter("What would you like to do with this task?");
        }

        UI.printEmpty();
        UI.printFullWidth("-");
        UI.printEmpty();

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
                if (mode == Mode.ACTING) {
                    enterBrowsingMode();
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

        if (mode == Mode.BROWSING) {
            List<Task> tasks = taskManager.getPendingTasks();
            if (tasks.isEmpty()) return null;
            if (currentIndex < tasks.size()) {
                enterActingMode(tasks.get(currentIndex));
            }
            return this;
        }

        // Mode.ACTING
        switch (currentIndex) {

            case 0: // Start Task — open SessionMenu with just this one task
                List<Task> single = new java.util.ArrayList<>();
                single.add(selectedTask);
                return new SessionMenu(single, sessionManager);

            case 1: // Remove Task
                taskManager.removeTask(selectedTask);
                enterBrowsingMode();
                return this;

            case 2: // Back
                enterBrowsingMode();
                return this;
        }

        return this;
    }
}