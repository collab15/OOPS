
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// responsible for selecting the best tasks to suggest to the user based on the current weights and pending tasks
public class HeuristicEngine {

    private int numberOfTasksToSuggest; // how many tasks to return 
    private Weights weights;// used to calc priority score 

    public HeuristicEngine(int numberOfTasksToSuggest, Weights weights) {
        this.numberOfTasksToSuggest = numberOfTasksToSuggest;
        this.weights = weights;
    }
    
    public List<Task> suggestTasks(List<Task> pendingTasks) {

        List<PrioritizedTask> priorityList = prioritizeTasks(pendingTasks);
        return chooseHighPriorityTasks(numberOfTasksToSuggest, priorityList);
    }

    // first you get pendiing tasks from TaskManager next you calculate a priority score
//  for each task using the weights , then you sort the tasks by priority and return N tasks based on numberOfTasksToSuggest
    private List<PrioritizedTask> prioritizeTasks(List<Task> pendingTasks) {

        List<PrioritizedTask> priorityList = new ArrayList<>();
        for (Task pendingTask : pendingTasks) {
            double priority = weights.calculateScore(pendingTask);
            priorityList.add(new PrioritizedTask(priority, pendingTask));
        }

        return priorityList;
    }

    private List<Task> chooseHighPriorityTasks(int numberOfTasksToSuggest,
                                               List<PrioritizedTask> priorityList) {
        List<Task> highPriorityTasks = new ArrayList<>();
        List<PrioritizedTask> sorted = sortByPriority(priorityList);
        int limit = Math.min(numberOfTasksToSuggest, sorted.size());
        for (int i = 0; i < limit; i++) {
            highPriorityTasks.add(sorted.get(i).getTask());
        }

        return highPriorityTasks;
    }

    private List<PrioritizedTask> sortByPriority(List<PrioritizedTask> priorityList) {
        List<PrioritizedTask> sorted = new ArrayList<>(priorityList);
        sorted.sort(Comparator
                .comparingDouble(PrioritizedTask::getPriority)
                .reversed());
        return sorted;
    }
}
