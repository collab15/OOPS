public class Utils {

    public static void cls() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                // For Windows, use cmd /c cls
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // For Linux/macOS, use the clear command
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
