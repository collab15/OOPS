import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HeuristicEngine {
    
    private int numberOfTasksToSuggest;
    private Weights weights;

    HeuristicEngine(int numberOfTasksToSuggest, Weights weights){
        this.numberOfTasksToSuggest = numberOfTasksToSuggest;
        this.weights = weights;

    }

    public List<Task> suggestTasks(){

        List<Task> pendingTasks = TaskManager.getPendingTasks();

        List<PrioritizedTask> priorityList = prioritizeTasks(pendingTasks);

        List<Task> suggestedTasks = chooseHighPriorityTasks (numberOfTasksToSuggest, priorityList);

        return suggestedTasks;
    }

    private List<PrioritizedTask> prioritizeTasks(List<Task> pendingTasks){

        List<PrioritizedTask> priorityList = new ArrayList<>();

        for(Task pendingTask : pendingTasks){
            double priority = calculatePriority(pendingTask);
            PrioritizedTask prioritizedTask = new PrioritizedTask(priority, pendingTask);
            priorityList.add(prioritizedTask);
        }

        return priorityList;
    }

    private List<Task> chooseHighPriorityTasks(int numberOfTasksToSuggest, List<PrioritizedTask> priorityList){
        
        List<Task> highPriorityTasks = new ArrayList<>();

        List<PrioritizedTask> tasksSortedByPriority = sortByPriority(priorityList);

        int limit = Math.min(numberOfTasksToSuggest, tasksSortedByPriority.size());

        for(int i = 0; i < limit; i++){
            highPriorityTasks.add(tasksSortedByPriority.get(i).task);
        }

        return highPriorityTasks;
    }

    private double calculatePriority(Task task){

        double priority = weights.w_importance * task.importance
                    + weights.w_urgency * task.urgency
                    + weights.w_effort * task.effort
                    + weights.w_length * task.length;

        return priority;
    }

    private List<PrioritizedTask> sortByPriority(List<PrioritizedTask> priorityList){
        
        List<PrioritizedTask> sortedPriorityList = new ArrayList<>(priorityList);

        sortedPriorityList.sort(Comparator.comparingDouble((PrioritizedTask t) -> t.priority).reversed());

        return sortedPriorityList;
    }

}
