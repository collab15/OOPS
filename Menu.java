import java.util.ArrayList;
import java.util.List;

// Base class every screen in the app inherits from.
// Handles the shared render loop, cursor movement, and keyboard dispatch.
// Subclasses override render() details via getItemsHeader() and
// handleSelection() to provide their own behaviour — that's the
// polymorphism that lets Navigator treat all menus the same way.
public abstract class Menu {

    protected List<Task>   menuItems       = new ArrayList<>();
    protected List<String> menuSelections  = new ArrayList<>();
    protected int  currentIndex = 0;
    protected boolean exitSignal = false;

    // subclasses return the heading shown above the task list
    protected String getItemsHeader() { return "SUGGESTED TASKS"; }

    // subclasses handle what happens when the user presses Enter
    abstract Menu handleSelection();

    // varargs so callers can just write setMenuSelections("A", "B", "C")
    protected void setMenuSelections(String... options) {
        for (String option : options) {
            menuSelections.add(option);
        }
    }

    protected void setMenuItems(List<Task> tasks) {
        this.menuItems = tasks;
    }

    // called by Navigator every time this menu comes to the top of the stack;
    // resets the cursor so the first option is highlighted on entry
    public void reset() {
        currentIndex = 0;
        exitSignal   = false;
    }

    // default display loop — subclasses with special needs (e.g. SessionMenu)
    // override this entirely
    public Menu display() {

        render();

        while (!exitSignal) {

            String key = KeyboardListener.listen();

            if      ("UP".equals(key))        moveUp();
            else if ("DOWN".equals(key))      moveDown();
            else if ("BACKSPACE".equals(key)) {
                if (Navigator.canGoBack()) return null;
                continue; // already at the root — ignore silently
            }
            else if ("ENTER".equals(key))     { return handleSelection(); }

            render();
        }

        return null;
    }

    // protected so subclasses that override display() can still scroll the cursor
    protected void moveUp() {
        currentIndex--;
        if (currentIndex < 0) currentIndex = menuSelections.size() - 1;
    }

    protected void moveDown() {
        currentIndex++;
        if (currentIndex >= menuSelections.size()) currentIndex = 0;
    }

    // default render — draws the standard header, task list, and selection menu;
    // subclasses that need extra sections (like a countdown timer) override this
    protected void render() {

        UI.cls();

        String status = Status.get() + " ";

        System.out.println();
        UI.printFullWidth("*");
        UI.printCenter("████████   █████    █████   ██   ██    ██   ██");
        UI.printCenter("   ██     ██   ██   ██      ██  ██      ██ ██ ");
        UI.printCenter("   ██     ███████   █████   █████        ███   ");
        UI.printCenter("   ██     ██   ██      ██   ██  ██      ██ ██  ");
        UI.printCenter("   ██     ██   ██   █████   ██   ██    ██   ██");
        UI.printFullWidth("-");
        UI.printAtMargins(Utils.getWeekDayAndDate() + " ", status);
        UI.printFullWidth("=");
        UI.printEmpty();
        UI.printCenter("--- " + getItemsHeader() + " ---");
        UI.printEmpty();

        if (!menuItems.isEmpty()) {
            for (int i = 0; i < menuItems.size(); i++) {
                UI.printCenter((i + 1) + ". " + menuItems.get(i).getName());
            }
        } else {
            UI.printCenter("No Tasks");
            UI.printEmpty();
        }

        UI.printEmpty();
        UI.printFullWidth("-");
        UI.printEmpty();

        for (int i = 0; i < menuSelections.size(); i++) {
            if (i == currentIndex) {
                UI.printCenter("<[ " + menuSelections.get(i) + " ]>");
            } else {
                UI.printCenter(menuSelections.get(i));
            }
            UI.printEmpty();
        }

        UI.printFullWidth("=");
    }
}
