import java.util.Scanner;

// Handles user input flows that require the keyboard to be in scanner mode
// KeyboardListener is paused and stdin is used directly
// Receives the shared TaskManager from Main so tasks are added to the same
// list that the rest of the app reads from.
public class ActionHandler {

    private static final Scanner scanner = new Scanner(System.in);

    // taskManager is now passed in — not created here — so this class
    // works on the same task list as the rest of the app.
    public static void addTask(TaskManager taskManager) {

        // Consume any leftover newline from previous input
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        String name       = "";
        int importance    = 0;
        int urgency       = 0;
        int effort        = 0;
        int length        = 0;

        // STEP 1: NAME
        while (true) {
            render(name, importance, urgency, effort, length, "ENTER TASK NAME", "");
            UI.printEmpty();
            UI.inputCenter("Name: ", true);

            String input = scanner.nextLine();

            if (input.isEmpty()) {
                render(name, importance, urgency, effort, length, "ENTER TASK NAME", "ERROR: Name cannot be empty");
                sleep();
                continue;
            }

            name = input;
            break;
        }

        // STEPS 2-5: NUMERIC FIELDS
        importance = inputStep("IMPORTANCE", name, importance, urgency, effort, length);
        urgency    = inputStep("URGENCY",    name, importance, urgency,  effort, length);
        effort     = inputStep("EFFORT",     name, importance, urgency,  effort, length);
        length     = inputStep("LENGTH",     name, importance, urgency,  effort, length);

        Task task = new Task(name, importance, urgency, effort, length);
        taskManager.addTask(task);

        render(name, importance, urgency, effort, length, "TASK CREATED SUCCESSFULLY!", "");
        sleep();
    }

    // ---------------- STEP INPUT (1-10) ----------------

    private static int inputStep(String field, String name, int importance, int urgency, int effort, int length) {
        while (true) {
            render(name, importance, urgency, effort, length, "ENTER " + field + " (1-10)", "");
            UI.printEmpty();
            UI.inputCenter(field + ": ");

            String input = scanner.nextLine().trim(); // .trim() removes any leading spaces 

            try {
                int value = Integer.parseInt(input); // to avoid NumberFormatException

                if (value < 1 || value > 10) {
                    render(name, importance, urgency, effort, length,
                            "ENTER " + field + " (1-10)", "ERROR: Value must be between 1 and 10");
                    sleep();
                    continue;
                }
                return value;

            } catch (NumberFormatException e) {
                render(name, importance, urgency, effort, length,
                        "ENTER " + field + " (1-10)", "ERROR: Invalid number");
                sleep();
            }
        }
    }

    // ---------------- RENDER ----------------

    private static void render(String name, int importance, int urgency, int effort, int length, String title, String error) {

        UI.cls(); // clears the terminal b4 drawing 
        System.out.println();
        UI.printFullWidth("=");
        UI.printEmpty();
        UI.printCenter("--- ADD TASK ---");
        UI.printEmpty();

        UI.printCenter("NAME       : " + (name.isEmpty() ? "-" : name));
        UI.printCenter("IMPORTANCE : " + (importance == 0 ? "-" : importance));
        UI.printCenter("URGENCY    : " + (urgency    == 0 ? "-" : urgency));
        UI.printCenter("EFFORT     : " + (effort     == 0 ? "-" : effort));
        UI.printCenter("LENGTH     : " + (length     == 0 ? "-" : length));

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

    // ---------------- UTILITIES ----------------
// pauses for 1.2 seconds before showing an error or success message 
    private static void sleep() {
        try {
            Thread.sleep(1200);
        } catch (InterruptedException ignored) {}
    }
}