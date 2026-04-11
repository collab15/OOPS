public class Main {

    public static void main(String[] args) {

        Settings.loadSettings();

        TaskManager taskManager = new TaskManager();

        Weights weights = new Weights(5, 8, -3, -2);

        LearningEngine learning = new LearningEngine( Settings.AI_Settings.getLearningFactor(), Settings.AI_Settings.getBacklogSize() );

        AIEngine ai = new AIEngine(weights, learning);

        List<Task> suggestedTasks = ai.suggestTasks(taskManager.getPendingTasks(), Settings.UserSettings.getSuggestionCount());

        MainMenu menu = new MainMenu();
        menu.setMenuItems(suggestedTasks);
        menu.handleSelection();
    }
}
