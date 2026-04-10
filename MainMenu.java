import java.util.List;

public class MainMenu extends Menu {
    
    void setMenuSelections(){
        this.menuSelections = menuSelections;
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
            // case 1 : call this;
        }
    }
}
