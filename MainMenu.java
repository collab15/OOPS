import java.util.List;

public class MainMenu extends Menu {
    
    void setMenuSelections(){
        this.menuSelections = List.of(
            "Start a Task",
            "Add a Task",
            "View Pending Tasks",
            "View Completed Tasks",
            "Exit"
        );
    }

    void handleSelection(){
        while(true){
            displayMenuItems();
            displayMenuSelections();
            // handleSelectionChange() on keypress up and down
            // select() on keypress enter
        }
    }

    void select(){
        switch(currentIndex){
            case 0 :

                break;

            case 1 :

                break;

            case 2 :

                break;

            case 3 :

                break;

            case 4 :

                break;
        }
    }
}
