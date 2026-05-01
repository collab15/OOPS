import java.io.*;
import java.nio.file.Files;
import java.sql.*;

// Syncs local file storage with a Supabase PostgreSQL database.
// Called after every mutation so the cloud is always up to date.
// On startup, if any of the expected local folders or files are missing,
// it assumes this is a fresh machine and pulls everything back from the DB.
//
// Tables used:
//   tasks        — serialised Task objects (pending + completed)
//   weights      — the four AI weight values
//   task_history — serialised list of Deltas used by LearningEngine
public class PartitionManager {

    private static final String DB_URL =
            "jdbc:postgresql://aws-0-ap-northeast-2.pooler.supabase.com:6543/postgres"
            + "?sslmode=require"
            + "&prepareThreshold=0"
            + "&preferQueryMode=simple";

    private static final String DB_USER     = "postgres.urpacclpucfjnzufumqc";
    private static final String DB_PASSWORD = "NoorHuda@123";

    private static TaskManager mtaskManager;

    // not instantiated — all methods are static
    private PartitionManager() {}

    // entry point: called by TaskManager after every write operation
    public static void sync(TaskManager taskManager) {

        mtaskManager = taskManager;
        boolean success = false;

        try (Connection conn = getConnection()) {

            File baseDir = new File(Settings.AppSettings.getLocalStorageDirectory());

            if (needsRecovery(baseDir)) {
                // local storage is incomplete — pull everything down from DB
                restoreFromDB(conn);
                restoreWeights(conn);
                restoreTaskSelectionHistory(conn);
            } else {
                // local storage looks healthy — push it up to DB
                uploadLocalState(conn);
                syncWeights(conn);
                syncTaskSelectionHistory(conn);
            }

            success = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        // the status badge in every menu header reflects the result
        Status.set(success ? "ONLINE" : "OFFLINE");
    }

    // checks for all four expected local artifacts; if any are missing
    // we treat the local state as untrustworthy and restore from the DB
    private static boolean needsRecovery(File baseDir) {

        File pending  = new File(baseDir, "pendingTasks");
        File completed = new File(baseDir, "completedTasks");
        File weights  = new File(baseDir, "weights.sys");
        File history  = new File(baseDir, "task_history.sys");

        return !baseDir.exists()
                || !pending.exists()
                || !completed.exists()
                || !weights.exists()
                || !history.exists();
    }

    // pulls all task rows from DB and writes them as .tsk files into the
    // correct local subfolder based on their status column
    private static void restoreFromDB(Connection conn) throws Exception {

        File baseDir      = new File(Settings.AppSettings.getLocalStorageDirectory());
        File pendingDir   = new File(baseDir, "pendingTasks");
        File completedDir = new File(baseDir, "completedTasks");

        pendingDir.mkdirs();
        completedDir.mkdirs();

        String sql = "SELECT id, status, data FROM tasks";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String id     = rs.getString("id");
                String status = rs.getString("status");
                byte[] data   = rs.getBytes("data");

                File targetDir = status.equals("completed") ? completedDir : pendingDir;
                File file      = new File(targetDir, id + ".tsk");

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(data);
                }
            }
        }
    }

    // pushes both local task folders to DB
    private static void uploadLocalState(Connection conn) throws Exception {

        String baseDir = Settings.AppSettings.getLocalStorageDirectory();
        syncFolder(conn, baseDir + "/pendingTasks",   "pending");
        syncFolder(conn, baseDir + "/completedTasks", "completed");
    }

    // reads every .tsk file in a folder and upserts it to the tasks table
    private static void syncFolder(Connection conn, String path, String status) throws IOException {

        File dir = new File(path);
        if (!dir.exists()) return;

        File[] files = dir.listFiles((d, name) -> name.endsWith(".tsk"));
        if (files == null) return;

        for (File file : files) {
            byte[] data = Files.readAllBytes(file.toPath());
            Task task   = deserializeTask(data);

            if (task == null || task.getID() == null) continue;

            upsertTask(conn, task, status, data, file.lastModified());
        }
    }

    // INSERT ... ON CONFLICT UPDATE so we never get duplicate rows
    private static void upsertTask(Connection conn, Task task, String status,
                                   byte[] data, long lastModified) {

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

    private static void syncWeights(Connection conn) throws IOException {

        File file = new File(Settings.AppSettings.getLocalStorageDirectory(), "weights.sys");
        if (!file.exists()) return;

        byte[] data = Files.readAllBytes(file.toPath());

        String sql =
                "INSERT INTO weights (id, data, last_modified) VALUES (?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET data = EXCLUDED.data, " +
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

    private static void restoreWeights(Connection conn) throws SQLException, IOException {

        String sql = "SELECT data FROM weights WHERE id = 'GLOBAL_WEIGHTS'";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (!rs.next()) return;

            byte[] data = rs.getBytes("data");
            File file   = new File(Settings.AppSettings.getLocalStorageDirectory(), "weights.sys");

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }
        }
    }

    private static void syncTaskSelectionHistory(Connection conn) throws IOException {

        File file = new File(
                Settings.AppSettings.getLocalStorageDirectory(), "task_history.sys");

        if (!file.exists()) return;

        byte[] data = Files.readAllBytes(file.toPath());

        String sql =
                "INSERT INTO task_history (id, data, last_modified) VALUES (?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET data = EXCLUDED.data, " +
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

    private static void restoreTaskSelectionHistory(Connection conn)
            throws SQLException, IOException {

        String sql = "SELECT data FROM task_history WHERE id = 'GLOBAL_TASK_HISTORY'";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (!rs.next()) return;

            byte[] data = rs.getBytes("data");
            File file   = new File(
                    Settings.AppSettings.getLocalStorageDirectory(), "task_history.sys");

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }
        }
    }

    private static Connection getConnection() throws SQLException {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL driver not found");
        }

        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // deserialises a raw byte array back into a Task object;
    // returns null if the bytes are corrupt or from an incompatible version
    private static Task deserializeTask(byte[] data) {

        try (ObjectInputStream ois =
                new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (Task) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}
