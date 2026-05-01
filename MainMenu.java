import java.util.List;

// The home screen. Shows the AI-suggested task list and routes to every
// major section of the app. reset() is called by Navigator every time
// the user navigates back here, so the suggested list is always fresh.
public class MainMenu extends Menu {

    private final TaskManager    taskManager;
    private final SessionManager sessionManager;

    public MainMenu(TaskManager taskManager, SessionManager sessionManager) {

        this.taskManager    = taskManager;
        this.sessionManager = sessionManager;

        setMenuSelections(
            "Start a Task",
            "Add a Task",
            "View Pending Tasks",
            "View Completed Tasks",
            "Exit"
        );
    }

    // re-runs the AI suggestion every visit so the list reflects any
    // tasks added, completed, or removed since we were last here
    @Override
    public void reset() {
        super.reset();
        menuItems = AIEngine.suggestTasks();
    }

    @Override
    protected String getItemsHeader() {
        return "SUGGESTED TASKS";
    }

    @Override
    Menu handleSelection() {

        switch (currentIndex) {

            case 0: // Start a Task — opens SessionMenu with the suggested list
                if (menuItems.isEmpty()) return this;
                return new SessionMenu(menuItems, sessionManager);

            case 1: // Add a Task — switches to Scanner input mode temporarily
                KeyboardListener.pause();
                ActionHandler.addTask(taskManager);
                KeyboardListener.resume();
                return this;

            case 2:
                return new PendingTasksViewMenu(taskManager, sessionManager);

            case 3:
                return new CompletedTasksViewMenu(taskManager);

            case 4:
                System.exit(0);
                return null;
        }

        return null;
    }
}
