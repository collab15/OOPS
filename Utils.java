import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

// Small utility methods used across multiple classes.
// Nothing stateful here — just helpers that didn't belong anywhere else.
public class Utils {

    // strips characters that would break a filename on Windows or Linux
    public static String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }

    // default pause used between UI redraws / after error messages
    public static void sleep() {
        try {
            Thread.sleep(1200);
        } catch (InterruptedException ignored) {}
    }

    // configurable pause — useful when you need a different delay length
    public static void sleep(int dur) {
        try {
            Thread.sleep(dur);
        } catch (InterruptedException ignored) {}
    }

    // returns something like "Friday 01 May 2026" for the header bar
    public static String getWeekDayAndDate() {

        LocalDate today = LocalDate.now();

        String day  = today.getDayOfWeek()
                           .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        String date = today.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

        return day + " " + date;
    }
}
