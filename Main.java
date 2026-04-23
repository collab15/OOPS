import java.util.List;

public class Main {
    public static void main(String[] args) {

        KeyboardListener.start();

        TaskManager taskManager = new TaskManager();
        taskManager.loadTasks();

        SessionManager sessionManager = new SessionManager(taskManager);
        AIEngine ai = new AIEngine(taskManager);

        // Pass AIEngine into MainMenu so it can re-suggest tasks on every visit
        Navigator.goTo(new MainMenu(taskManager, sessionManager, ai));

        Navigator.run();

        KeyboardListener.stop();
    }
}