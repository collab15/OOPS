public class CompletedTasksViewMenu extends Menu {

    private final TaskManager taskManager;

    public CompletedTasksViewMenu(TaskManager taskManager) {

        this.taskManager = taskManager;

        setMenuSelections("Clear Completed Tasks");
    }

    Menu handleSelection() {

        taskManager.clearCompletedTasks();

        return this;
    }
}