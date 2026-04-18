package project;

import java.util.List;

public class CompletedTasksViewMenu extends Menu {

    public CompletedTasksViewMenu() {
        setMenuSelections();
        setMenuItems(TaskManager.getCompletedTasks()); // loads completed tasks from TaskManager into  menuItems
    }

    @Override
    void setMenuSelections() {
        this.menuSelections = List.of(
            "Back" // only back bec u r only viewing completed tasks 
        );
    }

    @Override
    void handleSelection() {
     // arrows
    }

    @Override
    void select() {
        switch (currentIndex) {
            case 0:
               // go back 
                break;
        }
    }
}
