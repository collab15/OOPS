import java.util.Stack;

public class Navigator {

    private static final Stack<Menu> stack = new Stack<>();

    public static void goTo(Menu menu) {
        stack.push(menu);
    }

    public static void back() {
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }

    public static Menu current() {
        return stack.isEmpty() ? null : stack.peek();
    }

    public static boolean canGoBack() {
        return stack.size() > 1;
    }

    public static void run() {

        while (current() != null) {

            Menu menu = current();

            menu.reset();

            Menu next = menu.display();

            if (next == menu) {
                // stay in same menu
            } 
            else if (next != null) {
                goTo(next);
            } 
            else {
                back();
            }
        }
    }
}