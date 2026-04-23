import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    // Instance fields — NOT static.
    // Static lists meant all TaskManager instances 
    // shared the same list, which caused unpredictable behaviour.
    private List<Task> pendingTasks   = new ArrayList<>();
    private List<Task> completedTasks = new ArrayList<>();

    public List<Task> getPendingTasks() {
        return new ArrayList<>(pendingTasks);
    }

    public List<Task> getCompletedTasks() {
        return new ArrayList<>(completedTasks); // returns the copy only 
    }

    public void addTask(Task task) {

        if (task == null) return;

        pendingTasks.add(task);

        String baseDir = Settings.AppSettings.getLocalStorageDirectory(); // gets the base folder which is local
        String dirPath = baseDir + File.separator + "pendingTasks";

        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs(); // creates the folder  
        }

        File file = new File(directory, Utils.sanitizeFileName(task.getName()) + ".tsk"); // converts the task name into a safe filename
// automatically closes the filestreams when done even if an error occurs 
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // ObjectOutputStream converts a java object into bytes that can be written to a file -> serialisation
//oos.writeObject(task) — writes the entire Task object to the file.
            oos.writeObject(task);

        } catch (IOException e) {
            System.out.println("Failed to save task: " + e.getMessage());
        }
    }

    public void removeTask(Task task) { // removes from in memory lists then delete file from disk 

        if (task == null) return;

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        String fileName = Utils.sanitizeFileName(task.getName()) + ".tsk";

        File pendingFile   = new File(baseDir + File.separator + "pendingTasks",   fileName);
        File completedFile = new File(baseDir + File.separator + "completedTasks", fileName);

        pendingTasks.remove(task);
        completedTasks.remove(task);

        if (pendingFile.exists()) { // checking b4 deleting to avoid errors
            if (!pendingFile.delete()) {
                System.out.println("Failed to delete pending task file");
            }
        }

        if (completedFile.exists()) {
            if (!completedFile.delete()) {
                System.out.println("Failed to delete completed task file");
            }
        }
    }

    public void completeTask(Task task) {  // moves the file from local pending tasks to local completing tasks than deleting and rewriting just moves existing file 

        if (task == null) return;

        if (pendingTasks.remove(task)) {
            completedTasks.add(task);

            String baseDir = Settings.AppSettings.getLocalStorageDirectory();

            File oldFile = new File(
                    baseDir + File.separator + "pendingTasks",
                    Utils.sanitizeFileName(task.getName()) + ".tsk"
            );

            File newDir = new File(baseDir + File.separator + "completedTasks");
            if (!newDir.exists()) newDir.mkdirs();

            File newFile = new File(
                    newDir,
                    Utils.sanitizeFileName(task.getName()) + ".tsk"
            );

            oldFile.renameTo(newFile);
        }
    }

    public void clearCompletedTasks() { 

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        File completedDir = new File(baseDir + File.separator + "completedTasks");

        completedTasks.clear(); // wipes the in memory list 

        if (completedDir.exists() && completedDir.isDirectory()) {
         // filters files gets files whose nmes end w .tsk to avoid accidently deleting unrelated files 
            File[] files = completedDir.listFiles((dir, name) -> name.endsWith(".tsk"));

            if (files != null) {
                for (File file : files) {
                    if (!file.delete()) {
                        System.out.println("Failed to delete: " + file.getName());
                    }
                }
            }
        }
    }

    public void loadTasks() { // called once in startup in main clears both lists first then loads from both folders 

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();

        pendingTasks.clear();
        completedTasks.clear();

        loadTasksFromFolder(baseDir + File.separator + "pendingTasks",   pendingTasks);
        loadTasksFromFolder(baseDir + File.separator + "completedTasks", completedTasks);
    }

    private void loadTasksFromFolder(String path, List<Task> list) {

        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) return;

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".tsk"));
        if (files == null) return;

        for (File file : files) {
            try (FileInputStream fis  = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
// ObjectInputStream reads bytes from a file converts them back to java object - deserialisation
                Task task = (Task) ois.readObject(); // reads object and casts it to task cast is needed bec readObject() returns a generic Object type
                list.add(task);

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Failed loading: " + file.getName());
            }
        }
    }
}

