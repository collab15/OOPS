// All terminal drawing goes through here so the rest of the app never
// touches System.out directly for layout. WIDTH comes from Settings so
// you can resize the whole UI from one place.
public class UI {

    static int WIDTH       = Settings.AppSettings.getDisplayWidth();
    static int INNER_WIDTH = WIDTH - 2; // space between the two border pipes

    // clears the terminal — works on both Windows and Unix
    public static void cls() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // prints text horizontally centred inside the border
    public static void printCenter(String text) {

        int space      = Math.max(0, INNER_WIDTH - text.length());
        int paddingLeft  = space / 2;
        int paddingRight = space - paddingLeft; // makes sure the line is exactly WIDTH wide

        String line = " |" + " ".repeat(paddingLeft) + text
                           + " ".repeat(paddingRight) + "|";
        System.out.println(line);
    }

    // prints text flush against the left border
    public static void printLeft(String text) {

        int space = Math.max(0, INNER_WIDTH - text.length()) - 1;

        String line = " | " + text + " ".repeat(space) + "|";
        System.out.println(line);
    }

    // prints text flush against the right border
    public static void printRight(String text) {

        int space = Math.max(0, INNER_WIDTH - text.length());

        String line = " |" + " ".repeat(space) + text + "|";
        System.out.println(line);
    }

    // puts left text at the left margin and right text at the right margin —
    // used for the date / status line in the header
    public static void printAtMargins(String left, String right) {

        int space = Math.max(0, INNER_WIDTH - left.length() - right.length()) - 1;

        String line = " | " + left + " ".repeat(space) + right + "|";
        System.out.println(line);
    }

    // positions an input prompt roughly in the centre without the border pipes
    public static void inputCenter(String text) {

        int space      = Math.max(0, INNER_WIDTH - text.length());
        int paddingLeft = space / 2 - 2;

        System.out.print("  " + " ".repeat(paddingLeft) + text);
    }

    // same as above but lets you shift the prompt left a bit when the label
    // is longer (l = true adds extra left indent to keep it visually centred)
    public static void inputCenter(String text, boolean l) {

        int space   = Math.max(0, INNER_WIDTH - text.length());
        int toLeft  = l ? 12 : 2;
        int paddingLeft = space / 2 - toLeft;

        System.out.print("  " + " ".repeat(paddingLeft) + text);
    }

    // blank row inside the border — used for vertical spacing
    public static void printEmpty() {
        System.out.println(" |" + " ".repeat(INNER_WIDTH) + "|");
    }

    // prints a full-width divider line using the given character, e.g. "=" or "*"
    public static void printFullWidth(String text) {
        System.out.println(" +" + text.repeat(WIDTH - 2) + "+");
    }
}
