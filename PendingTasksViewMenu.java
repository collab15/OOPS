package project;

import java.util.List;

public class PendingTasksViewMenu extends Menu {

    public PendingTasksViewMenu() {
        setMenuSelections();
        setMenuItems(TaskManager.getPendingTasks());
    }

    @Override
    void setMenuSelections() {
        this.menuSelections = List.of(
            "Start Task",
            "Back"
        );
    }

    @Override
    void handleSelection() {
        // arrow keys will be implemented later 
    }

    @Override
    void select() { // runs when enter is pressed
        switch (currentIndex) {

            case 0:
                // Start selected task
                if (!menuItems.isEmpty()) { // checks if there are tasks 
                    Task selectedTask = menuItems.get(0); // need to change it to dynamic index only picks first task 
                    System.out.println("Starting task: " + selectedTask.getName());
                } else {
                    System.out.println("No pending tasks.");
                }
                break;

            case 1:
               // go back 
                break;
        }
    }
}
