// import java.io.FileWriter;
// import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class TaskManager {

    private static List<Task> pendingTasks = new ArrayList<>();
    private static List<Task> completedTasks = new ArrayList<>();


    public static List<Task> getPendingTasks() {
        return pendingTasks;
    }

    public static List<Task> getCompletedTasks() {
        return completedTasks;
    }

    public static void loadTasks() {
        // Load from storage (file/database)
        // Example:
        // pendingTasks.add(new Task(...));
    }

    public static void addTask(Task task) {
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
        if (pendingTasks.remove(task)) {
            completedTasks.add(task);
        }
    }

    public static void removeTask(Task task) {
        pendingTasks.remove(task);
    }

}




