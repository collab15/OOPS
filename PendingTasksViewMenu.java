import java.util.List;

public class PendingTasksViewMenu extends Menu {

    private final TaskManager taskManager;

    public PendingTasksViewMenu(TaskManager taskManager) {

        this.taskManager = taskManager;

        setMenuSelections("Edit Task", "Remove Task");

        List<Task> tasks = taskManager.getPendingTasks();
        setMenuItems(tasks);
    }

    @Override
    Menu handleSelection() {

        switch (currentIndex) {
            case 0:
                return this;
            case 1:
                return this;
        }

        return null;
    }
}