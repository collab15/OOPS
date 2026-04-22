import java.util.List;;

public class Main {
    public static void main(String[] args) {

        KeyboardListener.start();
        
        TaskManager taskManager = new TaskManager();
        taskManager.loadTasks();

        SessionManager sessionManager = new SessionManager(taskManager);

        AIEngine ai = new AIEngine(taskManager);
        List<Task> suggestedTasks = ai.suggestTasks();

        Navigator.goTo(new MainMenu(suggestedTasks, taskManager, sessionManager));

        Navigator.run();

        KeyboardListener.stop();
    }
}