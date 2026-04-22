import java.util.ArrayList;
import java.util.List;

abstract class Menu {

    protected List<Task> menuItems = new ArrayList<>();
    protected List<String> menuSelections = new ArrayList<>();
    protected int currentIndex = 0;

    protected boolean exitSignal = false;

    abstract Menu handleSelection();

    protected void setMenuSelections(String... options) {
        for (String option : options) {
            menuSelections.add(option);
        }
    }

    protected void setMenuItems(List<Task> tasks) {
        this.menuItems = tasks;
    }

    public void reset() {
        currentIndex = 0;
        exitSignal = false;
    }

    public Menu display() {

        render();

        while (!exitSignal) {

            String key = KeyboardListener.listen();

            if ("UP".equals(key)) moveUp();
            else if ("DOWN".equals(key)) moveDown();

            else if ("BACKSPACE".equals(key)) {
                if (Navigator.canGoBack()) {
                    return null;
                }
            }

            else if ("ENTER".equals(key)) {
                return handleSelection();
            }

            render();
        }

        return null;
    }

    private void moveUp() {
        currentIndex--;
        if (currentIndex < 0) currentIndex = menuSelections.size() - 1;
    }

    private void moveDown() {
        currentIndex++;
        if (currentIndex >= menuSelections.size()) currentIndex = 0;
    }

    protected void render() {

        UI.cls();

        System.out.println();
        UI.printFullWidth("=");
        UI.printEmpty();
        UI.printEmpty();
        UI.printCenter("--- SUGGESTED TASKS ---");
        UI.printEmpty();

        if (!menuItems.isEmpty()) {
            for (int i=0; i < menuItems.size() ;i++) {
                UI.printCenter( (i+1) + ". " + menuItems.get(i).getName());
            }
        }else{
            UI.printCenter("No Tasks Suggested");
            UI.printEmpty();
        }

        UI.printEmpty();
        UI.printEmpty();
        UI.printFullWidth("-");
        UI.printEmpty();

        // Show menu options
        for (int i = 0; i < menuSelections.size(); i++) {
            if (i == currentIndex) {
                UI.printCenter("<[ " + menuSelections.get(i) + " ]>");
                UI.printEmpty();
            } else {
                UI.printCenter(menuSelections.get(i));
                UI.printEmpty();
            }
        }
        
        UI.printFullWidth("=");
    }
}

// before we had two methods handleSelection and select two step process move selection then call select  
// replaced it w handleSelection(int index) which combines both steps into one method, when user presses enter 
// it calls handleSelection with current index to determine which action to take based on selection made

    // handle selection change removed and replaced with moveUp and moveDown methods to handle navigation through menu options, these methods update currentIndex based on user input and wrap around when reaching the top or bottom of the menu. The render method is responsible for clearing the screen and displaying the current menu options along with any items if applicable.
        // replace display menuselections and displaymenuitems with render clears screen prints menu prints items  



// render means to display the items on screen 
// current state ko screen par dikhana 
// cause to become visible or to be made visible by drawing or by processing code and displaying
// the result on a screen or other output device. 
// In our case it means to clear the screen and print the menu options and any relevant items 
//based on the current state of the menu,
//  such as which option is currently selected.
