import java.util.ArrayList;
import java.util.List;

// Abstract class
abstract class Menu {

    protected List<Task> menuItems = new ArrayList<>();
    protected List<String> menuSelections = new ArrayList<>();
    protected int currentIndex = 0;

    abstract void setMenuSelections();
    abstract void handleSelection();
    abstract void select();

    protected void setMenuItems(List<Task> tasks){
        this.menuItems = tasks;
    }

    protected void handleSelectionChange(int indexChange){
        
        this.currentIndex += indexChange;

        if(currentIndex < 0){
            currentIndex = menuSelections.size() - 1;
        } else if(currentIndex >= menuSelections.size()){
            currentIndex = 0;
        }

        for(String MenuSelection : menuSelections){
            // printf with MenuSelections[index] highlighted
        }
    }

    protected void displayMenuItems(){
        for(Task menuItem : menuItems){
            //printf MenuItem.xyz
        }
    }

    protected void displayMenuSelections(){
        for(String menuSelection : menuSelections){
            //printf MenuSelection
        }
    }

}