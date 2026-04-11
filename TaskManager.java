public class TaskManager {

    private List<Task> pendingTasks = new ArrayList<>();
    private List<Task> completedTasks = new ArrayList<>();

    public List<Task> getPendingTasks() {
        return pendingTasks;
    }

    public List<Task> getCompletedTasks() {
        return completedTasks;
    }

    public void addTask(Task task) {
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

    public void completeTask(Task task) {
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




