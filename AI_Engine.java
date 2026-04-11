import java.util.List;

public class AIEngine {

    private int numberOfTasksToSuggest =
        Settings.UserSettings.getSuggestionCount();

    private double learningFactor =
        Settings.AI_Settings.getLearningFactor();

    private int extentOfBacklogToLearnFrom =
        Settings.AI_Settings.getBacklogSize();

    private Weights weights = new Weights(5, 8, -3, -2);

    private final LearningEngine learningEngine =
        new LearningEngine(learningFactor, extentOfBacklogToLearnFrom);

    private final HeuristicEngine heuristicEngine =
        new HeuristicEngine(numberOfTasksToSuggest, weights);

    public List<Task> suggestTasks() {
        return heuristicEngine.suggestTasks();
    }

    public void learn() {
        Delta cumulativeDelta = learningEngine.calculateCumulativeDelta();
        weights.applyDelta(cumulativeDelta);
    }

    public void loadState() {
        // load weights
    }
}
