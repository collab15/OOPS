import java.util.List;

// Displays completed tasks and lets the user clear them all.
// Tasks are loaded fresh from TaskManager each time the menu is entered
// so the list is always up to date.
public class CompletedTasksViewMenu extends Menu {

    private final TaskManager taskManager;

    public CompletedTasksViewMenu(TaskManager taskManager) {
        this.taskManager = taskManager;
        refresh();
    }

    private void refresh() {
        List<Task> completed = taskManager.getCompletedTasks();
        setMenuItems(completed);

        menuSelections.clear();
        if (completed.isEmpty()) {
            setMenuSelections("Back");
        } else {
            setMenuSelections("Clear All Completed Tasks", "Back");
        }

        currentIndex = 0;
    }

    @Override
    protected String getItemsHeader() {
        return "COMPLETED TASKS";
    }

    @Override
    Menu handleSelection() {

        List<Task> completed = taskManager.getCompletedTasks();

        if (completed.isEmpty()) {
            // only option is "Back"
            return null;
        }

        switch (currentIndex) {

            case 0: // Clear All
                taskManager.clearCompletedTasks();
                refresh();
                return this;

            case 1: // Back
                return null;
        }

        return this;
    }
}