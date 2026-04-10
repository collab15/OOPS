import java.util.List;

public class Main {
    public static void main(String[] args) {

        TaskManager.loadTasks();

        List<Task> suggestedTasks[];
        // run AI engine to get top 3 suggestions

        MainMenu.setMenuItems(suggestedTasks);

    }
}