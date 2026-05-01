// Simple global flag that tracks whether the last Supabase sync worked.
// Every menu reads this and shows it in the top-right corner so the user
// always knows if they're working offline.
public class Status {

    private static String status = "OFFLINE";

    // not meant to be instantiated
    private Status() {}

    public static void set(String newStatus) {
        status = newStatus;
    }

    public static String get() {
        return status;
    }
}
