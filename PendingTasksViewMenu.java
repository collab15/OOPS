
import java.util.ArrayList;
import java.util.List;

public class PendingTasksViewMenu extends Menu {

    private final TaskManager taskManager;

    public PendingTasksViewMenu(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    void setMenuSelections() {

        // use the injected instance method
        List<Task> tasks = taskManager.getPendingTasks();
        setMenuItems(tasks);
        // Build menu dynamically
        List<String> selections = new ArrayList<>();

        for (Task task : tasks) {
            selections.add(task.getName());
        }

        selections.add("Back");

        this.menuSelections = selections;
    }

    @Override
    void handleSelection(int index) {
        // If "Back" is selected
        //set shouldExit so the display loop actually exits on Back
        if (index == menuSelections.size() - 1) {
            shouldExit = true;
            return;
        }
        // Otherwise user selected a task
        Task selectedTask = menuItems.get(index);
        System.out.println("Starting task: " + selectedTask.getName());

        //  connect to SessionManager
    }
}

// implement proper go back system
// constructor calling setMenuSelections already called inside display method in Menu class so no need to call it again in constructor of PendingTasksViewMenu, also setMenuItems is called in constructor to load pending tasks from TaskManager into menuItems list, this allows us to display the pending tasks when the menu is rendered. The handleSelection method will be implemented later to handle navigation through the menu options using arrow keys, and the select method will execute the appropriate action based on the current selection when the user presses enter.
