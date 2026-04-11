import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HeuristicEngine {

    private int numberOfTasksToSuggest;
    private Weights weights;

    public HeuristicEngine(int numberOfTasksToSuggest, Weights weights) {
        this.numberOfTasksToSuggest = numberOfTasksToSuggest;
        this.weights = weights;
    }

    public List<Task> suggestTasks() {
        List<Task> pendingTasks = TaskManager.getPendingTasks();
        List<PrioritizedTask> priorityList = prioritizeTasks(pendingTasks);
        return chooseHighPriorityTasks(numberOfTasksToSuggest, priorityList);
    }

    private List<PrioritizedTask> prioritizeTasks(List<Task> pendingTasks) {
        List<PrioritizedTask> priorityList = new ArrayList<>();
        for (Task pendingTask: pendingTasks) {
            double priority = weights.calculateScore(pendingTask);
            PrioritizedTask prioritizedTask = new PrioritizedTask(priority, pendingTask);
            priorityList.add(prioritizedTask);
        }
        return priorityList;
    }

    private List<Task> chooseHighPriorityTasks(int numberOfTasksToSuggest, List<PrioritizedTask> priorityList) {
        List<Task> highPriorityTasks = new ArrayList<>();
        List<PrioritizedTask> tasksSortedByPriority = sortByPriority(priorityList);
        int limit = Math.min(numberOfTasksToSuggest, tasksSortedByPriority.size());
        for (int i = 0; i < limit; i++) {
            highPriorityTasks.add(tasksSortedByPriority.get(i).getTask());
        }
        return highPriorityTasks;
    }

    private List<PrioritizedTask> sortByPriority(List<PrioritizedTask> priorityList) {
        List<PrioritizedTask> sortedPriorityList = new ArrayList<>(priorityList);
        sortedPriorityList.sort(Comparator.comparingDouble(PrioritizedTask::getPriority).reversed());
        return sortedPriorityList;
    }
}
