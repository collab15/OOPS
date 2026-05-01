import java.util.Stack;

// Manages the menu stack. Pushing a menu navigates into it;
// popping goes back to whatever was underneath. run() is the
// main loop — it calls display() on the top menu and interprets
// the return value to decide whether to push, pop, or stay put.
public class Navigator {

    private static final Stack<Menu> stack = new Stack<>();

    public static void goTo(Menu menu) { stack.push(menu); }

    public static void back() {
        if (!stack.isEmpty()) stack.pop();
    }

    public static Menu current() {
        return stack.isEmpty() ? null : stack.peek();
    }

    public static boolean canGoBack() {
        return stack.size() > 1; // at least one menu below the current one
    }

    // display() returns:
    //   the same menu → stay (no stack change)
    //   a different menu → push it (navigate forward)
    //   null → pop (go back)
    public static void run() {

        while (current() != null) {

            Menu menu = current();
            menu.reset();
            Menu next = menu.display();

            if (next == menu) {
                // stay on the same screen — nothing to do
            } else if (next != null) {
                goTo(next);
            } else {
                back();
            }
        }
    }
}
