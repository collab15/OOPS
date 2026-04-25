import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;


public class Utils {

    public static String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }

    public static void sleep() {
        try {
            Thread.sleep(1200);
        } catch (InterruptedException ignored) {}
    }

    public static void sleep(int dur) {
        try {
            Thread.sleep(dur);
        } catch (InterruptedException ignored) {}
    }

    public static String getWeekDayAndDate() {

        LocalDate today = LocalDate.now();

        String day = today.getDayOfWeek()
                          .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        String date = today.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

        return day + " " + date;
    }

}
