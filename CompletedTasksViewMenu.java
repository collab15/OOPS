

import java.util.ArrayList;
import java.util.List;

public class CompletedTasksViewMenu extends Menu {

    private final TaskManager taskManager;

    public CompletedTasksViewMenu(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    void setMenuSelections() {

        List<Task> tasks = taskManager.getCompletedTasks();
        setMenuItems(tasks);

        List<String> selections = new ArrayList<>();

        for (Task task : tasks) {
            selections.add(task.getName());
        }

        selections.add("Back");

        this.menuSelections = selections;
    }

    @Override
    void handleSelection(int index) {

        //  set shouldExit so the display loop actually exits on Back
        if (index == menuSelections.size() - 1) {
            shouldExit = true;
            return;
        }

        Task selectedTask = menuItems.get(index);
        System.out.println("Viewing completed task: " + selectedTask.getName());
    }
}
