import java.util.List;

public class MainMenu extends Menu {

    private final TaskManager taskManager;
    private final SessionManager sessionManager;
    private final AIEngine ai;

    public MainMenu(TaskManager taskManager, SessionManager sessionManager, AIEngine ai) {

        this.taskManager    = taskManager;
        this.sessionManager = sessionManager;
        this.ai             = ai;

        setMenuSelections(
            "Start a Task",
            "Add a Task",
            "View Pending Tasks",
            "View Completed Tasks",
            "Exit"
        );
    }

    // Re-run AI suggestion every time the menu is displayed so the list
    // stays fresh after tasks are added, removed, or completed.
    @Override
    public void reset() {
        super.reset();
        menuItems = ai.suggestTasks(); // refresh suggested tasks
    }

    @Override
    protected String getItemsHeader() {
        return "SUGGESTED TASKS";
    }

    @Override
    Menu handleSelection() {

        switch (currentIndex) {

            case 0:
                if (menuItems.isEmpty()) return this; // no tasks to start
                return new SessionMenu(menuItems, sessionManager);

            case 1:
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