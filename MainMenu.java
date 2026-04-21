package project;

import java.util.List;

public class MainMenu extends Menu {

    // FIX: store the dependencies that Main.java passes in
    private final TaskManager taskManager;
    private final SessionManager sessionManager;

    public MainMenu(TaskManager taskManager, SessionManager sessionManager) {
        this.taskManager = taskManager;
        this.sessionManager = sessionManager;
    }

    @Override
    void setMenuSelections() {
        this.menuSelections = List.of(
            "Start a Task",
            "Add a Task",
            "View Pending Tasks",
            "View Completed Tasks",
            "Exit"
        );
    }

    @Override
    void handleSelection(int index) {
        switch (index) {

            case 0:
                // pass sessionManager so the menu can start sessions
                System.out.println("Starting a task...");
                break;

            case 1:
                System.out.println("Adding a task...");
                break;

            case 2:
                // pass taskManager — PendingTasksViewMenu needs it
                new PendingTasksViewMenu(taskManager).display();
                break;

            case 3:
                // pass taskManager — CompletedTasksViewMenu needs it
                new CompletedTasksViewMenu(taskManager).display();
                break;

            case 4:
                System.out.println("Exiting...");
                System.exit(0);
                break;
        }
    }
}


// Menu.display() handles input arrow keys calls handleSelection renders UI  so displayMenuItems displayMenuSelections removed 
//Main.java
/*MainMenu.display()
User presses ↑ ↓
Menu updates currentIndex
User presses ENTER
handleSelection(index) is called */
