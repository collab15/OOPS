import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskManager {

    private List<Task> pendingTasks   = new ArrayList<>();
    private List<Task> completedTasks = new ArrayList<>();

    public List<Task> getPendingTasks() {
        return new ArrayList<>(pendingTasks);
    }

    public List<Task> getCompletedTasks() {
        return new ArrayList<>(completedTasks);
    }

    // =========================
    // ADD TASK
    // =========================
    public void addTask(Task task) {

        if (task == null) return;

        String id = UUID.randomUUID().toString();
        task.setID(id);

        pendingTasks.add(task);

        saveTask(task, "pendingTasks");

        PartitionManager.sync(this);
    }

    // =========================
    // REMOVE TASK
    // =========================
    public void removeTask(Task task) {

        if (task == null) return;

        pendingTasks.remove(task);
        completedTasks.remove(task);

        deleteTaskFile(task);

        PartitionManager.sync(this);
    }

    // =========================
    // COMPLETE TASK
    // =========================
    public void completeTask(Task task) {

        if (task == null) return;

        if (pendingTasks.remove(task)) {

            completedTasks.add(task);

            moveFile(task);

            PartitionManager.sync(this);
        }
    }

    // =========================
    // CLEAR COMPLETED
    // =========================
    public void clearCompletedTasks() {

        completedTasks.clear();

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        File completedDir = new File(baseDir + File.separator + "completedTasks");

        if (completedDir.exists()) {

            File[] files = completedDir.listFiles((dir, name) -> name.endsWith(".tsk"));

            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }

        PartitionManager.sync(this);
    }

    // =========================
    // LOAD TASKS
    // =========================
    public void loadTasks() {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();

        pendingTasks.clear();
        completedTasks.clear();

        loadFromFolder(baseDir + File.separator + "pendingTasks", pendingTasks);
        loadFromFolder(baseDir + File.separator + "completedTasks", completedTasks);
    }

    // =========================
    // FILE SAVE
    // =========================
    private void saveTask(Task task, String folder) {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        String path = baseDir + File.separator + folder;

        File directory = new File(path);
        if (!directory.exists()) directory.mkdirs();

        File file = new File(directory, task.getID() + ".tsk");

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(task);

        } catch (IOException ignored) {}
    }

    // =========================
    // FILE DELETE
    // =========================
    private void deleteTaskFile(Task task) {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();

        File pending   = new File(baseDir + "/pendingTasks", task.getID() + ".tsk");
        File completed = new File(baseDir + "/completedTasks", task.getID() + ".tsk");

        if (pending.exists()) pending.delete();
        if (completed.exists()) completed.delete();
    }

    // =========================
    // MOVE FILE
    // =========================
    private void moveFile(Task task) {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();

        File oldFile = new File(baseDir + "/pendingTasks", task.getID() + ".tsk");

        File newDir = new File(baseDir + "/completedTasks");
        if (!newDir.exists()) newDir.mkdirs();

        File newFile = new File(newDir, task.getID() + ".tsk");

        oldFile.renameTo(newFile);
    }

    // =========================
    // LOAD FROM FILE
    // =========================
    private void loadFromFolder(String path, List<Task> list) {

        File directory = new File(path);
        if (!directory.exists()) return;

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".tsk"));

        if (files == null) return;

        for (File file : files) {

            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {

                Task task = (Task) ois.readObject();
                list.add(task);

            } catch (Exception ignored) {}
        }
    }
}
