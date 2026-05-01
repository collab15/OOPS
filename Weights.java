import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// Holds the four multipliers the HeuristicEngine uses to score tasks.
// The values start with defaults and drift over time as the user makes
// selections — each call to applyDelta nudges them based on past behaviour.
public class Weights {

    private double importance;
    private double urgency;
    private double effort;
    private double length;

    public Weights(double i, double u, double e, double l) {
        this.importance = i;
        this.urgency    = u;
        this.effort     = e;
        this.length     = l;
    }

    // Adjusts all four weights by the amounts in the delta, then immediately
    // writes to disk so nothing is lost if the app closes unexpectedly.
    public void applyDelta(Delta delta) {
        this.importance += delta.getImportance();
        this.urgency    += delta.getUrgency();
        this.effort     += delta.getEffort();
        this.length     += delta.getLength();

        saveToFile();
    }

    // Writes weights as a single space-separated line — same format
    // that AIEngine.loadState() reads back on startup.
    private void saveToFile() {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        File file = new File(baseDir, "weights.sys");

        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(
                importance + " " +
                urgency    + " " +
                effort     + " " +
                length
            );
        } catch (IOException ignored) {}
    }

    public double getImportance() { return importance; }
    public double getUrgency()    { return urgency; }
    public double getEffort()     { return effort; }
    public double getLength()     { return length; }
}
