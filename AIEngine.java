import java.io.*;
import java.util.List;

// Facade that ties HeuristicEngine and LearningEngine together.
// The rest of the app only calls AIEngine — it never touches the
// two sub-engines or the weights directly.
//
// Data flow:
//   suggestTasks()          → HeuristicEngine scores pending tasks with current weights
//   observeTaskSelection()  → stores a Delta (what the user picked vs. what we suggested)
//   learn()                 → LearningEngine averages past deltas → apply to weights → save
public class AIEngine {

    private static int     numberOfTasksToSuggest;
    private static Weights weights;

    private static LearningEngine       learningEngine;
    private static HeuristicEngine      heuristicEngine;
    private static TaskSelectionHistory taskSelectionHistory;

    private static TaskManager taskManager;

    private static double learningFactor;
    private static int    extentOfBacklogToLearnFrom;

    public static void init(TaskManager tm) {

        loadState(); // load weights from disk (or use defaults if file is missing)

        taskManager = tm;

        numberOfTasksToSuggest     = Settings.UserSettings.getSuggestionCount();
        learningFactor             = Settings.AI_Settings.getLearningFactor();
        extentOfBacklogToLearnFrom = Settings.AI_Settings.getBacklogSize();

        taskSelectionHistory = new TaskSelectionHistory();

        learningEngine = new LearningEngine(
            learningFactor,
            extentOfBacklogToLearnFrom,
            taskSelectionHistory
        );

        heuristicEngine = new HeuristicEngine(
            numberOfTasksToSuggest,
            weights
        );
    }

    // called by MainMenu.reset() and SessionMenu every time the list is displayed
    public static List<Task> suggestTasks() {
        return heuristicEngine.suggestTasks(taskManager.getPendingTasks());
    }

    // records what the user picked vs. what we suggested so we can learn from it
    public static void observeTaskSelection(Task task, Task suggested) {
        Delta delta = Delta.fromTaskComparison(task, suggested);
        taskSelectionHistory.addDelta(delta);
    }

    // runs after every task selection — keeps weights drifting toward the user's habits
    public static void learn() {
        Delta cumulativeDelta = learningEngine.calculateCumulativeDelta();
        weights.applyDelta(cumulativeDelta);
        saveState();
    }

    // reads weights from weights.sys; falls back to hand-tuned defaults if the
    // file is missing, empty, or in an unexpected format
    public static void loadState() {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        File file = new File(baseDir, "weights.sys");

        double d1 = 0.4, d2 = 0.5, d3 = -0.2, d4 = -0.1; // sensible starting point

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

                weights = new Weights(
                    Double.parseDouble(parts[0]),
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3])
                );
            }

        } catch (Exception e) {
            weights = new Weights(d1, d2, d3, d4);
        }
    }

    public static void saveState() {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        File file = new File(baseDir, "weights.sys");

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println(
                weights.getImportance() + " " +
                weights.getUrgency()    + " " +
                weights.getEffort()     + " " +
                weights.getLength()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
