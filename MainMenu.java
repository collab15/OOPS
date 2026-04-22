import java.util.List;

public class MainMenu extends Menu {

    private final TaskManager taskManager;
    private final SessionManager sessionManager;

    public MainMenu(List<Task> suggestedTasks,TaskManager taskManager, SessionManager sessionManager) {

        this.menuItems = suggestedTasks;
        this.taskManager = taskManager;
        this.sessionManager = sessionManager;

        setMenuSelections(
            "Start a Task",
            "Add a Task",
            "View Pending Tasks",
            "View Completed Tasks",
            "Exit"
        );
    }

    Menu handleSelection() {

        switch (currentIndex) {

            case 0:
                return this;
            case 1:
                KeyboardListener.pause();
                ActionHandler.addTask();
                KeyboardListener.resume(); 
                return this;

            case 2:
                return new PendingTasksViewMenu(taskManager);

            case 3:
                return new CompletedTasksViewMenu(taskManager);

            case 4:
                System.exit(0);
                return null;
        }

        return null;
    }
}