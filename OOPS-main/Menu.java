import java.util.ArrayList;
import java.util.List;

public abstract class Menu {

    protected List<Task> menuItems        = new ArrayList<>();
    protected List<String> menuSelections = new ArrayList<>();
    protected int currentIndex = 0;
    protected boolean exitSignal = false;

    // Subclasses override these to control what the render shows
    // polymorphism used here bec render() method calls getItemsHeader() in the appropriate menu accordingly
    // ex PendingTasksViewMenu CompletedTasksViewMenu 

    protected String getItemsHeader() { return "SUGGESTED TASKS"; }

    abstract Menu handleSelection();

    protected void setMenuSelections(String... options) { // varargs 
        for (String option : options) {
            menuSelections.add(option); // adds it to menuSelection
        }
    }

    protected void setMenuItems(List<Task> tasks) {
        this.menuItems = tasks;
    }

    // called by Navigator every time a menu is visited . Resets the option back to the first option
    // mainMenu overrides this is to also refresh the suggested 
    public void reset() {
        currentIndex = 0;
        exitSignal   = false;
    }

    public Menu display() {

        render();

        while (!exitSignal) {
            
            String key = KeyboardListener.listen();

            if      ("UP".equals(key))        moveUp();
            else if ("DOWN".equals(key))      moveDown();
            else if ("BACKSPACE".equals(key)) {
                if (Navigator.canGoBack()) return null;
                continue; 
            }
            else if ("ENTER".equals(key))     { return handleSelection(); }

            render();
        }

        return null;
    }

    // protected so subclasses that override display() can still call them
    protected void moveUp() {
        currentIndex--;
        if (currentIndex < 0) currentIndex = menuSelections.size() - 1;
    }

    protected void moveDown() {
        currentIndex++;
        if (currentIndex >= menuSelections.size()) currentIndex = 0;
    }

    protected void render() {

        String status="Status Couldn't be determined";

        UI.cls();

        if ("ONLINE".equals(Status.get())){
            status = Status.get() + " " ;
        }else{
            status= Status.get() + " - Restart to sync";
        }

        System.out.println();
        UI.printFullWidth("*");
        UI.printCenter("████████   █████    █████   ██   ██    ██   ██");
        UI.printCenter("   ██     ██   ██   ██      ██  ██      ██ ██ ");
        UI.printCenter("   ██     ███████   █████   █████        ███   ");
        UI.printCenter("    ██     ██   ██      ██   ██  ██      ██ ██  ");
        UI.printCenter("   ██     ██   ██   █████   ██   ██    ██   ██");
        UI.printFullWidth("-");
        UI.printAtMargins( Utils.getWeekDayAndDate()+" ", status );
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