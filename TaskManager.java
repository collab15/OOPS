import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Owns the in-memory task lists and keeps them in sync with local .tsk files.
// Every mutation (add, remove, complete, clear) writes to disk immediately
// and then calls PartitionManager.sync() to push changes to Supabase.
public class TaskManager {

    private List<Task> pendingTasks   = new ArrayList<>();
    private List<Task> completedTasks = new ArrayList<>();

    // defensive copies so callers can't accidentally mutate the internal lists
    public List<Task> getPendingTasks()   { return new ArrayList<>(pendingTasks); }
    public List<Task> getCompletedTasks() { return new ArrayList<>(completedTasks); }

    public void addTask(Task task) {

        if (task == null) return;

        // assign a unique ID before anything else touches the task
        task.setID(UUID.randomUUID().toString());

        pendingTasks.add(task);
        saveTask(task, "pendingTasks");
        PartitionManager.sync(this);
    }

    public void removeTask(Task task) {

        if (task == null) return;

        pendingTasks.remove(task);
        completedTasks.remove(task);
        deleteTaskFile(task);
        PartitionManager.sync(this);
    }

    public void completeTask(Task task) {

        if (task == null) return;

        if (pendingTasks.remove(task)) {
            completedTasks.add(task);
            moveFile(task); // moves .tsk file from pendingTasks/ to completedTasks/
            PartitionManager.sync(this);
        }
    }

    public void clearCompletedTasks() {

        completedTasks.clear();

        String baseDir     = Settings.AppSettings.getLocalStorageDirectory();
        File completedDir  = new File(baseDir + File.separator + "completedTasks");

        if (completedDir.exists()) {
            File[] files = completedDir.listFiles((dir, name) -> name.endsWith(".tsk"));
            if (files != null) {
                for (File file : files) file.delete();
            }
        }

        PartitionManager.sync(this);
    }

    // reads all .tsk files from both folders back into memory — called once at startup
    public void loadTasks() {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();

        pendingTasks.clear();
        completedTasks.clear();

        loadFromFolder(baseDir + File.separator + "pendingTasks",   pendingTasks);
        loadFromFolder(baseDir + File.separator + "completedTasks", completedTasks);
    }

    // serialises a task object into the given subfolder
    private void saveTask(Task task, String folder) {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        File directory = new File(baseDir + File.separator + folder);
        if (!directory.exists()) directory.mkdirs();

        File file = new File(directory, task.getID() + ".tsk");

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(task);
        } catch (IOException ignored) {}
    }

    // removes the .tsk file from whichever folder it's currently in
    private void deleteTaskFile(Task task) {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();

        File pending   = new File(baseDir + "/pendingTasks",   task.getID() + ".tsk");
        File completed = new File(baseDir + "/completedTasks", task.getID() + ".tsk");

        if (pending.exists())   pending.delete();
        if (completed.exists()) completed.delete();
    }

    // moves the .tsk file from pendingTasks/ → completedTasks/ without re-serialising
    private void moveFile(Task task) {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        File oldFile   = new File(baseDir + "/pendingTasks",   task.getID() + ".tsk");
        File newDir    = new File(baseDir + "/completedTasks");

        if (!newDir.exists()) newDir.mkdirs();

        oldFile.renameTo(new File(newDir, task.getID() + ".tsk"));
    }

    // deserialises every .tsk in the folder and appends to the given list;
    // corrupt or unreadable files are silently skipped
    private void loadFromFolder(String path, List<Task> list) {

        File directory = new File(path);
        if (!directory.exists()) return;

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".tsk"));
        if (files == null) return;

        for (File file : files) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {

                list.add((Task) ois.readObject());

            } catch (Exception ignored) {}
        }
    }
}
