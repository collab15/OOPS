

import java.util.List;
// AIEngine is the main branch it suggests tasks using heuristic engine and learns from the task selection history using learning engine
// AIEngine is a composition of HeuristicEngine LearningEngine and Weights
public class AIEngine {

    private int numberOfTasksToSuggest;
    private double learningFactor;
    private int extentOfBacklogToLearnFrom;

    private Weights weights;

    private final LearningEngine learningEngine;
    private final HeuristicEngine heuristicEngine;

    public AIEngine(Weights weights, LearningEngine learning) {
        // how many tasks to suggest is determined by user settings
        this.numberOfTasksToSuggest = Settings.UserSettings.getSuggestionCount();
        //  learning factor and backlog size are determined by AI settings
        this.learningFactor = Settings.AI_Settings.getLearningFactor();
        this.extentOfBacklogToLearnFrom = Settings.AI_Settings.getBacklogSize();

        //  weights are initialized with default values but will be updated through learning
        this.weights = new Weights(5, 8, -3, -2);

        // responsible for learning from task selection history and updating weights accordingly
        this.learningEngine = new LearningEngine(learningFactor, extentOfBacklogToLearnFrom);
         // responsible for suggesting tasks based on current weights and pending tasks
        this.heuristicEngine = new HeuristicEngine(numberOfTasksToSuggest, weights);
    }
    // calls HeuristicEngine to return a list of recommended tasks based on the current weights and pending tasks
    public List<Task> suggestTasks() {
        return heuristicEngine.suggestTasks();
    }

    // calls LearningEngine to analyse past data and produce a delta the change in weight 
    // apply those changes to weights
    public void learn() {
        Delta cumulativeDelta = learningEngine.calculateCumulativeDelta();
        weights.applyDelta(cumulativeDelta);
    }

    public void loadState() {
        // load weights from storage
    }
}

// when suggesting tasks : AIEngine → HeuristicEngine → uses Weights → returns tasks
// when learning : AIEngine → LearningEngine → uses TaskSelectionHistory → returns Delta → AIEngine applies Delta to Weights
// dependency means one element relies on another to function, for example HeuristicEngine 
// relies on Weights to calculate task priorities, and LearningEngine relies on TaskSelectionHistory 
// to learn from past selections. represented using dashed arrows in the diagram.
// Settings.UserSettings, Settings.AI_Settings used to fetch values ( dependency )
