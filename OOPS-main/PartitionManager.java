import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.Base64;

// =========================
// PARTITION MANAGER
// =========================
// Handles sync between LOCAL FILE SYSTEM ↔ SUPABASE POSTGRES
// Includes:
// - pendingTasks
// - completedTasks
// - weights
// - task selection history
public class PartitionManager {

    // =========================
    // DB CONFIG
    // =========================
    private static final String DB_URL =
            "jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:6543/postgres"
            + "?sslmode=require"
            + "&prepareThreshold=0"
            + "&preferQueryMode=simple";

    private static final String DB_USER = "postgres.urpacclpucfjnzufumqc";
    private static final String DB_PASSWORD = "NoorHuda@123";

    private static TaskManager mtaskManager;

    private PartitionManager() {}

    // =========================
    // MAIN SYNC
    // =========================
    public static void sync(TaskManager taskManager) {

        mtaskManager = taskManager;
        boolean success = false;

        try (Connection conn = getConnection()) {

            File baseDir = new File(Settings.AppSettings.getLocalStorageDirectory());

            if (needsRecovery(baseDir)) {

                restoreFromDB(conn);
                restoreWeights(conn);
                restoreTaskSelectionHistory(conn);

            } else {

                uploadLocalState(conn);
                syncWeights(conn);
                syncTaskSelectionHistory(conn);
            }

            success = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        Status.set(success ? "ONLINE" : "OFFLINE");
    }

    // =========================
    // RECOVERY CHECK
    // =========================
    private static boolean needsRecovery(File baseDir) {

        File pending = new File(baseDir, "pendingTasks");
        File completed = new File(baseDir, "completedTasks");
        File weights = new File(baseDir, "weights.sys");
        File history = new File(baseDir, "task_history.sys");

        return !baseDir.exists()
                || !pending.exists()
                || !completed.exists()
                || !weights.exists()
                || !history.exists();
    }

    // =========================
    // RESTORE FROM DB (TASKS)
    // =========================
    private static void restoreFromDB(Connection conn) throws Exception {

        File baseDir = new File(Settings.AppSettings.getLocalStorageDirectory());
        File pendingDir = new File(baseDir, "pendingTasks");
        File completedDir = new File(baseDir, "completedTasks");

        pendingDir.mkdirs();
        completedDir.mkdirs();

        String sql = "SELECT id, status, data FROM tasks";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String id = rs.getString("id");
                String status = rs.getString("status");
                byte[] data = rs.getBytes("data");

                File targetDir = status.equals("completed")
                        ? completedDir
                        : pendingDir;

                File file = new File(targetDir, id + ".tsk");

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(data);
                }
            }
        }
    }

    // =========================
    // LOCAL → DB SYNC
    // =========================
    private static void uploadLocalState(Connection conn) throws Exception {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();

        syncFolder(conn, baseDir + "/pendingTasks", "pending");
        syncFolder(conn, baseDir + "/completedTasks", "completed");
    }

    private static void syncFolder(Connection conn, String path, String status) throws IOException {

        File dir = new File(path);
        if (!dir.exists()) return;

        File[] files = dir.listFiles((d, name) -> name.endsWith(".tsk"));
        if (files == null) return;

        for (File file : files) {

            byte[] data = Files.readAllBytes(file.toPath());
            Task task = deserializeTask(data);

            if (task == null || task.getID() == null) continue;

            upsertTask(conn, task, status, data, file.lastModified());
        }
    }

    // =========================
    // UPSERT TASK
    // =========================
    private static void upsertTask(
            Connection conn,
            Task task,
            String status,
            byte[] data,
            long lastModified
    ) {

        String sql =
                "INSERT INTO tasks (id, status, data, last_modified) " +
                "VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "status = EXCLUDED.status, " +
                "data = EXCLUDED.data, " +
                "last_modified = EXCLUDED.last_modified";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getID());
            stmt.setString(2, status);
            stmt.setBytes(3, data);
            stmt.setLong(4, lastModified);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // WEIGHTS SYNC → DB
    // =========================
    private static void syncWeights(Connection conn) throws IOException {

        File file = new File(Settings.AppSettings.getLocalStorageDirectory(), "weights.sys");
        if (!file.exists()) return;

        byte[] data = Files.readAllBytes(file.toPath());

        String sql =
                "INSERT INTO weights (id, data, last_modified) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "data = EXCLUDED.data, " +
                "last_modified = EXCLUDED.last_modified";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "GLOBAL_WEIGHTS");
            stmt.setBytes(2, data);
            stmt.setLong(3, file.lastModified());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // WEIGHTS RESTORE ← DB
    // =========================
    private static void restoreWeights(Connection conn) throws SQLException, IOException {

        String sql = "SELECT data FROM weights WHERE id = 'GLOBAL_WEIGHTS'";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (!rs.next()) return;

            byte[] data = rs.getBytes("data");

            File file = new File(Settings.AppSettings.getLocalStorageDirectory(), "weights.sys");

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }
        }
    }

    // =========================
    // TASK HISTORY SYNC → DB
    // =========================
    private static void syncTaskSelectionHistory(Connection conn) throws IOException {

        File file = new File(
                Settings.AppSettings.getLocalStorageDirectory(),
                "task_history.sys"
        );

        if (!file.exists()) return;

        byte[] data = Files.readAllBytes(file.toPath());

        String sql =
                "INSERT INTO task_history (id, data, last_modified) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "data = EXCLUDED.data, " +
                "last_modified = EXCLUDED.last_modified";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "GLOBAL_TASK_HISTORY");
            stmt.setBytes(2, data);
            stmt.setLong(3, file.lastModified());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // TASK HISTORY RESTORE ← DB
    // =========================
    private static void restoreTaskSelectionHistory(Connection conn)
            throws SQLException, IOException {

        String sql =
                "SELECT data FROM task_history WHERE id = 'GLOBAL_TASK_HISTORY'";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (!rs.next()) return;

            byte[] data = rs.getBytes("data");

            File file = new File(
                    Settings.AppSettings.getLocalStorageDirectory(),
                    "task_history.sys"
            );

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }
        }
    }

    // =========================
    // CONNECTION
    // =========================
    private static Connection getConnection() throws SQLException {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL driver not found");
        }

        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // =========================
    // DESERIALIZE
    // =========================
    private static Task deserializeTask(byte[] data) {

        try (ObjectInputStream ois =
                     new ObjectInputStream(new ByteArrayInputStream(data))) {

            return (Task) ois.readObject();

        } catch (Exception e) {
            return null;
        }
    }
}