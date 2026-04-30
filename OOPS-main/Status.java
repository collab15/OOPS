public class Status {

    private static String status = "OFFLINE";

    private Status() {}

    public static void set(String newStatus) {
        status = newStatus;
    }

    public static String get() {
        return status;
    }
}