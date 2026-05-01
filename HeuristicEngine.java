import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Scores every pending task using the current weights and returns the
// top N as suggestions. No learning happens here — this class only
// reads weights, it never writes them.
public class HeuristicEngine {

    private int numberOfTasksToSuggest;
    private Weights weights;

    public HeuristicEngine(int numberOfTasksToSuggest, Weights weights) {
        this.numberOfTasksToSuggest = numberOfTasksToSuggest;
        this.weights = weights;
    }

    // each attribute is normalised to 0–1 before multiplying by its weight
    // so a task scored 10 on importance doesn't dwarf one scored 8 on urgency
    public double calculatePriority(Weights weights, Task task) {

        double importance = task.getImportance() / 10.0;
        double urgency    = task.getUrgency()    / 10.0;
        double effort     = task.getEffort()     / 10.0;
        double length     = task.getLength()     / 10.0;

        return weights.getImportance() * importance
             + weights.getUrgency()    * urgency
             + weights.getEffort()     * effort
             + weights.getLength()     * length;
    }

    // entry point: score all tasks, sort, return the top N
    public List<Task> suggestTasks(List<Task> pendingTasks) {
        List<PrioritizedTask> priorityList = prioritizeTasks(pendingTasks);
        return chooseHighPriorityTasks(numberOfTasksToSuggest, priorityList);
    }

    private List<PrioritizedTask> prioritizeTasks(List<Task> pendingTasks) {

        List<PrioritizedTask> priorityList = new ArrayList<>();

        for (Task pendingTask : pendingTasks) {
            double priority = calculatePriority(weights, pendingTask);
            priorityList.add(new PrioritizedTask(priority, pendingTask));
        }

        return priorityList;
    }

    private List<Task> chooseHighPriorityTasks(int limit,
                                               List<PrioritizedTask> priorityList) {
        List<Task> highPriorityTasks = new ArrayList<>();
        List<PrioritizedTask> sorted = sortByPriority(priorityList);

        int cap = Math.min(limit, sorted.size());

        for (int i = 0; i < cap; i++) {
            highPriorityTasks.add(sorted.get(i).getTask());
        }

        return highPriorityTasks;
    }

    // highest priority first
    private List<PrioritizedTask> sortByPriority(List<PrioritizedTask> priorityList) {

        List<PrioritizedTask> sorted = new ArrayList<>(priorityList);
        sorted.sort(Comparator.comparingDouble(PrioritizedTask::getPriority).reversed());
        return sorted;
    }
}
