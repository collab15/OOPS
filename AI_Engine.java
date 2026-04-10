import java.util.List;

public class AI_Engine {


    private int numberOfTasksToSuggest = Settings.UserSettings.numberOfTasksToSuggest;
    private double learningFactor = Settings.AI_Settings.learningFactor;
    private int extentOfBacklogToLearnFrom = Settings.AI_Settings.extentOfBacklogToLearnFrom;

    private Weights weights = new Weights(5, 8, -3, -2);
    
    private final LearningEngine learningEngine = new LearningEngine(learningFactor, extentOfBacklogToLearnFrom);
    private final HeuristicEngine heuristicEngine = new HeuristicEngine(numberOfTasksToSuggest, weights);

    List<Task> suggestTasks(){
        return heuristicEngine.suggestTasks();
    }

    void learn(){
        Delta CumulativeDelta = learningEngine.calculateCumulativeDelta();
        weights.updateByDelta(CumulativeDelta);
    }

    void loadState(){
        // load weights from memory
    }


}
