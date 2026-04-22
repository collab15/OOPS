public class UI {

    static int WIDTH = Settings.AppSettings.getDisplayWidth();
    static int INNER_WIDTH = WIDTH-2;

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

    public static void printCenter(String text) {

        int space = Math.max(0, INNER_WIDTH - text.length());

        int paddingLeft = space / 2;
        int paddingRight = space - paddingLeft; // ensures perfect fit

        String line =" |" + " ".repeat(paddingLeft) + text
                        + " ".repeat(paddingRight) + "|";

        System.out.println(line);
    }

    public static void inputCenter(String text) {

        int space = Math.max(0, INNER_WIDTH - text.length());

        int paddingLeft = space / 2 - 2;

        String line ="  " + " ".repeat(paddingLeft) + text;

        System.out.print(line);
    }

    public static void inputCenter(String text, boolean l) {

        int space = Math.max(0, INNER_WIDTH - text.length());
        
        int toLeft = (l) ? 12 : 2;
        int paddingLeft = space / 2 - toLeft;

        String line ="  " + " ".repeat(paddingLeft) + text;

        System.out.print(line);
    }

    public static void printEmpty() {

        String line = " |"+" ".repeat(INNER_WIDTH)+"|";
        System.out.println(line);
    }

    public static void printFullWidth(String text){
        System.out.println(" " + text.repeat(WIDTH));
    }
}
