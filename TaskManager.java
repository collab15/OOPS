import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private static List<Task> pendingTasks = new ArrayList<>();
    private static List<Task> completedTasks = new ArrayList<>();

    public List<Task> getPendingTasks() {
        return new ArrayList<>(pendingTasks);
    }

    public List<Task> getCompletedTasks() {
        return new ArrayList<>(completedTasks);
    }

    public void addTask(Task task) {

        if (task == null) return;

        pendingTasks.add(task);

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        String dirPath = baseDir + File.separator + "pendingTasks";

        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, Utils.sanitizeFileName(task.getName()) + ".tsk");

        try (FileOutputStream fos = new FileOutputStream(file);
            
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(task);

        } catch (IOException e) {
            System.out.println("Failed to save task: " + e.getMessage());
        }
    }

    public void removeTask(Task task) {

        if (task == null) return;

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();

        String fileName = Utils.sanitizeFileName(task.getName()) + ".tsk";

        File pendingFile = new File(
                baseDir + File.separator + "pendingTasks",
                fileName
        );

        File completedFile = new File(
                baseDir + File.separator + "completedTasks",
                fileName
        );


        pendingTasks.remove(task);
        completedTasks.remove(task);
        
        if (pendingFile.exists()) {
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

    public void completeTask(Task task) {

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

        completedTasks.clear();

        if (completedDir.exists() && completedDir.isDirectory()) {

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

    public void loadTasks() {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();

        pendingTasks.clear();
        completedTasks.clear();

        loadTasksFromFolder(baseDir + File.separator + "pendingTasks", pendingTasks);
        loadTasksFromFolder(baseDir + File.separator + "completedTasks", completedTasks);
    }

    private static void loadTasksFromFolder(String path, List<Task> list) {

        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) return;

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".tsk"));
        if (files == null) return;

        for (File file : files) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {

                Task task = (Task) ois.readObject();
                list.add(task);

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Failed loading: " + file.getName());
            }
        }
    }
}