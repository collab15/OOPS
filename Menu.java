import java.util.List;

// Abstract class
abstract class Menu {

    protected List<Task> menuItems;
    protected List<String> menuSelections;
    protected int currentIndex = 1;

    abstract void setMenuSelections();
    abstract void handleSelection();
    abstract void select();

    protected void setMenuItems(List<Task> suggestedTasks){
        this.menuItems = suggestedTasks;
    }

    protected void handleSelectionChange(int indexChange){
        this.currentIndex += indexChange;

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