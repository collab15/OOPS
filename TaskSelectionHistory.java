import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskSelectionHistory {

    private List<Delta> deltas = new ArrayList<>();
    private String filePath;

    public TaskSelectionHistory() {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        filePath = baseDir + File.separator + "task_history.sys";

        loadFromFile();
    }

    public void addDelta(Delta delta) {

        if (delta == null) return;

        try {
            deltas.add(delta);
            saveToFile();
        } catch (Exception e) {
            System.err.println("[History] Failed to add delta: " + e.getMessage());
        }
    }

    public List<Delta> getDeltaBacklog(int n) {

        if (n <= 0) return new ArrayList<>();

        List<Delta> lastNDeltas = new ArrayList<>();

        int size  = deltas.size();
        int start = Math.max(0, size - n);

        for (int i = size - 1; i >= start; i--) {
            lastNDeltas.add(deltas.get(i));
        }

        return lastNDeltas;
    }

    // =========================
    // SAVE WITH SAFETY
    // =========================
    private void saveToFile() {
        File tempFile  = new File(filePath + ".tmp");
        File finalFile = new File(filePath);

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(tempFile))) {

            oos.writeObject(deltas);
            oos.flush();

            // atomic replace (prevents corruption)
            if (finalFile.exists()) finalFile.delete();
            tempFile.renameTo(finalFile);

        } catch (Exception e) {
            System.err.println("[History] Save failed: " + e.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    public void loadFromFile() {

        File file = new File(filePath);
        if (!file.exists()) return;

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {

            Object obj = ois.readObject();

            if (obj instanceof List) {
                deltas = (List<Delta>) obj;
            } else {
                throw new IOException("Invalid history format");
            }

        } catch (Exception e) {

            System.err.println("[History] Corrupted file detected, resetting history.");

            deltas = new ArrayList<>();

            // optional recovery: delete bad file
            file.delete();
        }
    }
}
