import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        Settings.loadSettings();
        TaskManager.loadTasks();

        AI_Engine ai = new AI_Engine();
        ai.loadState();

        List<Task> suggestedTasks = new ArrayList<>();

        suggestedTasks = ai.suggestTasks();
        
        MainMenu mainMenu = new MainMenu();
        mainMenu.setMenuItems(suggestedTasks);
        mainMenu.handleSelection();

    }
}