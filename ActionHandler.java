import java.util.Scanner;

// Handles the "Add Task" input flow. KeyboardListener is paused for
// the duration so raw arrow-key bytes don't land in the Scanner buffer,
// then resumed once the task is saved.
public class ActionHandler {

    private static final Scanner scanner = new Scanner(System.in);

    // taskManager is passed in from MainMenu so we're adding to the same
    // list the rest of the app reads — no separate instance needed here
    public static void addTask(TaskManager taskManager) {

        // drain any leftover newline from previous Scanner or keyboard input
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        String name    = "";
        int importance = 0;
        int urgency    = 0;
        int effort     = 0;
        int length     = 0;

        // step 1: task name — can't be blank
        while (true) {
            render(name, importance, urgency, effort, length, "ENTER TASK NAME", "");
            UI.printEmpty();
            UI.inputCenter("Name: ", true);

            String input = scanner.nextLine();

            if (input.isEmpty()) {
                render(name, importance, urgency, effort, length, "ENTER TASK NAME",
                        "ERROR: Name cannot be empty");
                Utils.sleep();
                continue;
            }

            name = input;
            break;
        }

        // steps 2–5: numeric fields, each validated to 1–10 before moving on
        importance = inputStep("IMPORTANCE", name, importance, urgency, effort, length);
        urgency    = inputStep("URGENCY",    name, importance, urgency,  effort, length);
        effort     = inputStep("EFFORT",     name, importance, urgency,  effort, length);
        length     = inputStep("LENGTH",     name, importance, urgency,  effort, length);

        Task task = new Task(name, importance, urgency, effort, length);
        taskManager.addTask(task);

        render(name, importance, urgency, effort, length, "TASK CREATED SUCCESSFULLY!", "");
        Utils.sleep();
    }

    // shared input loop for each numeric field — loops until the user
    // enters something that parses to an integer in [1, 10]
    private static int inputStep(String field, String name,
                                 int importance, int urgency,
                                 int effort, int length) {
        while (true) {
            render(name, importance, urgency, effort, length,
                    "ENTER " + field + " (1-10)", "");
            UI.printEmpty();
            UI.inputCenter(field + ": ");

            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);

                if (value < 1 || value > 10) {
                    render(name, importance, urgency, effort, length,
                            "ENTER " + field + " (1-10)",
                            "ERROR: Value must be between 1 and 10");
                    Utils.sleep();
                    continue;
                }
                return value;

            } catch (NumberFormatException e) {
                render(name, importance, urgency, effort, length,
                        "ENTER " + field + " (1-10)", "ERROR: Invalid number");
                Utils.sleep();
            }
        }
    }

    // redraws the "Add Task" form with whatever has been filled in so far
    // so the user always sees their progress while typing each field
    private static void render(String name, int importance, int urgency,
                               int effort, int length,
                               String title, String error) {

        UI.cls();

        String status = "ONLINE".equals(Status.get())
                ? Status.get() + " "
                : Status.get() + " - Press S to Retry Sync ";

        System.out.println();
        UI.printFullWidth("*");
        UI.printCenter("████████   █████    █████   ██   ██    ██   ██");
        UI.printCenter("   ██     ██   ██   ██      ██  ██      ██ ██ ");
        UI.printCenter("   ██     ███████   █████   █████        ███   ");
        UI.printCenter("    ██     ██   ██      ██   ██  ██      ██ ██  ");
        UI.printCenter("   ██     ██   ██   █████   ██   ██    ██   ██");
        UI.printFullWidth("-");
        UI.printAtMargins(Utils.getWeekDayAndDate() + " ", status);
        UI.printFullWidth("=");
        UI.printEmpty();
        UI.printCenter("--- ADD TASK ---");
        UI.printEmpty();

        // show a dash for any field not yet filled in
        UI.printCenter("NAME       : " + (name.isEmpty()   ? "-" : name));
        UI.printCenter("IMPORTANCE : " + (importance == 0  ? "-" : importance));
        UI.printCenter("URGENCY    : " + (urgency    == 0  ? "-" : urgency));
        UI.printCenter("EFFORT     : " + (effort     == 0  ? "-" : effort));
        UI.printCenter("LENGTH     : " + (length     == 0  ? "-" : length));

        UI.printEmpty();
        UI.printFullWidth("-");
        UI.printEmpty();
        UI.printCenter(title);

        if (!error.isEmpty()) {
            UI.printEmpty();
            UI.printCenter(error);
        }

        UI.printEmpty();
        UI.printFullWidth("=");
    }
}
