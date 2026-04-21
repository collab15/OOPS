
public class Main {
    public static void main(String[] args) {

        Settings.loadSettings();

        // Load tasks from storage first
        TaskManager.loadTasks();

        TaskSelectionHistory history = new TaskSelectionHistory();

        LearningEngine learning = new LearningEngine(
                Settings.AI_Settings.getLearningFactor(),
                Settings.AI_Settings.getBacklogSize(),
                history
        );

        Weights weights = new Weights(1.0, 1.0, 1.0, 1.0);

        TaskManager taskManager = new TaskManager();
        // AIEngine now stored so it can be used
        AIEngine ai = new AIEngine(weights, learning, taskManager);

        // Session system
        SessionManager sessionManager = new SessionManager(taskManager);

        // Start UI
        new MainMenu(taskManager, sessionManager).display();
    }
}
