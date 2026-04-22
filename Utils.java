public class Utils {

    public static String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }

}
