

import java.util.List;
import java.util.ArrayList;

public class TaskManager {

    private static List<Task> pendingTasks = new ArrayList<>();
    private static List<Task> completedTasks = new ArrayList<>();

    public static List<Task> getPendingTasks() {
        return new ArrayList<>(pendingTasks); //  instead of return pendingTasks;
    }

    public static List<Task> getCompletedTasks() {
        return new ArrayList<>(completedTasks); 
        //  instead of return completedTasks; to avoid not anyone modifying the list directly 
        // from outside the class by returning a new ArrayList copy of List provided allowing external code to read the tasks 
        // without risking unintended modifications to the original lists within TaskManager.
    } 

    public static void addTask(Task task) {
        if (task == null) return;
        pendingTasks.add(task);
                // try {
        //     FileWriter writer = new FileWriter("tasks/pending/" + task.name + ".dat");

        //     writer.write(task.name + "\n");
        //     writer.write(task.importance + "\n");
        //     writer.write(task.urgency + "\n");
        //     writer.write(task.length + "\n");

        //     writer.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    public static void completeTask(Task task) {
        if (task == null) return;
        if (pendingTasks.remove(task)) {
            completedTasks.add(task);
        }
    }

    public static void loadTasks() {
        // Load from storage (file/database)
        // Example:
        // pendingTasks.add(new Task(...));
    }
}



