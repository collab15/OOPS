import java.util.List;

// Shows everything the user has finished. The only action available is
// clearing the whole list — there's no per-task action here by design,
// completed tasks are just for review.
public class CompletedTasksViewMenu extends Menu {

    private final TaskManager taskManager;

    public CompletedTasksViewMenu(TaskManager taskManager) {
        this.taskManager = taskManager;
        refresh();
    }

    // pulls the latest completed list every time we enter or after a clear
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
            return null; // only option is "Back"
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
