import java.io.*;
import java.util.List;

// AIEngine is the main branch it suggests tasks using heuristic engine and learns from the task selection history using learning engine
// AIEngine is a composition of HeuristicEngine LearningEngine and Weights
public class AIEngine {

    private static int numberOfTasksToSuggest;
    private static Weights weights;

    private static LearningEngine learningEngine;
    private static HeuristicEngine heuristicEngine;
    private static TaskSelectionHistory taskSelectionHistory;

    private static TaskManager taskManager;

    private static double learningFactor;
    private static int extentOfBacklogToLearnFrom;

    public static void init(TaskManager tm) {

        loadState();// AI internal state (AUTO initialized)

        taskManager = tm;

        // how many tasks to suggest is determined by user settings
        numberOfTasksToSuggest = Settings.UserSettings.getSuggestionCount();

        // learning factor and backlog size are determined by AI settings
        learningFactor = Settings.AI_Settings.getLearningFactor();
        extentOfBacklogToLearnFrom = Settings.AI_Settings.getBacklogSize();

        taskSelectionHistory = new TaskSelectionHistory();

        // responsible for learning from past task selection history
        learningEngine = new LearningEngine(
                learningFactor,
                extentOfBacklogToLearnFrom,
                taskSelectionHistory
        );

        // responsible for suggesting tasks based on current weights and pending tasks
        heuristicEngine = new HeuristicEngine(
                numberOfTasksToSuggest,
                weights
        );
    }

    public static List<Task> suggestTasks() {

        // using the internal taskManager instance
        List<Task> pendingTasks = taskManager.getPendingTasks();

        return heuristicEngine.suggestTasks(pendingTasks);
    }

    // user feedback entry point (stores learning signal internally)
    public static void observeTaskSelection(Task task, Task suggested) {

        Delta delta = Delta.fromTaskComparison(task, suggested);

        taskSelectionHistory.addDelta(delta);
    }

    // calls LearningEngine to analyse past data and produce a delta the change in weight
    // apply those changes to weights
    public static void learn() {

        Delta cumulativeDelta = learningEngine.calculateCumulativeDelta();
        weights.applyDelta(cumulativeDelta);

        saveState();
    }

    public static void loadState() {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        File file = new File(baseDir, "weights.sys");

        // default fallback values
        double d1 = 0.4, d2 = 0.5, d3 = -0.2, d4 = -0.1;

        try {

            if (!file.exists()) {
                weights = new Weights(d1, d2, d3, d4);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

                String line = reader.readLine();

                if (line == null || line.isEmpty()) {
                    weights = new Weights(d1, d2, d3, d4);
                    return;
                }

                String[] parts = line.trim().split("\\s+");

                if (parts.length < 4) {
                    weights = new Weights(d1, d2, d3, d4);
                    return;
                }

                double w1 = Double.parseDouble(parts[0]);
                double w2 = Double.parseDouble(parts[1]);
                double w3 = Double.parseDouble(parts[2]);
                double w4 = Double.parseDouble(parts[3]);

                weights = new Weights(w1, w2, w3, w4);
            }

        } catch (Exception e) {
            //fallback
            weights = new Weights(d1, d2, d3, d4);
        }
    }

    public static void saveState() {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        File file = new File(baseDir, "weights.sys");

        try (PrintWriter writer = new PrintWriter(file)) {

            writer.println(
                    weights.getImportance() + " " +
                    weights.getUrgency() + " " +
                    weights.getEffort() + " " +
                    weights.getLength()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}