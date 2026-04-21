import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract class Menu {

    protected List<Task> menuItems = new ArrayList<>(); // holds actual objects like task used when displaying data
    protected List<String> menuSelections = new ArrayList<>(); // tracks which option is currently selected
    protected int currentIndex = 0;

    // flag that lets handleSelection signal the display loop to exit
    protected boolean shouldExit = false;
    // subclasses will implement this 
    abstract void setMenuSelections();
    abstract void handleSelection(int index);

    protected void setMenuItems(List<Task> tasks) {
        this.menuItems = tasks;
    }

    public void display() {
        try {
            Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .jna(true)
                    .build(); // this enables us to read arrow keys and other special keys from the terminal

            setMenuSelections(); // loads menu options 

            // loop checks shouldExit so sub-menus can break back to the caller
            while (!shouldExit) {
                render(); // clears screen prints menu highlights selected option

                int ch = System.in.read(); // reads raw keyboard input

                if (ch == 27) {
                    System.in.read(); // skip '['
                    int direction = System.in.read();

                    if (direction == 'A') {
                        moveUp();
                    } else if (direction == 'B') {
                        moveDown();
                    }
                } else if (ch == 10 || ch == 13) { // this happens when the user presses enter 
                    handleSelection(currentIndex);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveUp() {
        currentIndex--; // selection moves up index decreases
        if (currentIndex < 0) {
            currentIndex = menuSelections.size() - 1;
        }
    }

    private void moveDown() {
        currentIndex++;
        if (currentIndex >= menuSelections.size()) {
            currentIndex = 0;
        }  // if at last reaches at top 
    }

    private void render() {
        System.out.print("\033[H\033[2J");  // clear screen
        System.out.flush();

         // this is a common ANSI escape code to clear the terminal screen and move the cursor to the top-left corner,
        //  it ensures that each time the menu is rendered it starts with a clean slate without any previous output cluttering the display

        // Show menu options
        for (int i = 0; i < menuSelections.size(); i++) {
            if (i == currentIndex) {
                System.out.println("> " + menuSelections.get(i));
            } else {
                System.out.println("  " + menuSelections.get(i));
            }
        }

        if (!menuItems.isEmpty()) {
            System.out.println("\n--- Items ---");
            for (Task item : menuItems) {
                System.out.println("- " + item);
            }
        }
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
