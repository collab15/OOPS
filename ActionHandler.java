import java.util.Scanner;

public class ActionHandler {

    private static final Scanner scanner = new Scanner(System.in);
    private static TaskManager taskManager = new TaskManager();

    public static void addTask() {

        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        String name = "";
        int importance = 0;
        int urgency = 0;
        int effort = 0;
        int length = 0;

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

        // STEP 2: IMPORTANCE
        importance = inputStep("IMPORTANCE", name, importance, urgency, effort, length);

        // STEP 3: URGENCY
        urgency = inputStep("URGENCY", name, importance, urgency, effort, length);

        // STEP 4: EFFORT
        effort = inputStep("EFFORT", name, importance, urgency, effort, length);

        // STEP 5: LENGTH
        length = inputStep("LENGTH", name, importance, urgency, effort, length);

        Task task = new Task(name, importance, urgency, effort, length);
        taskManager.addTask(task);

        render(name, importance, urgency, effort, length, "TASK CREATED SUCCESSFULLY!", "");
        sleep();

    }

    // -----------------------------
    // STEP INPUT (1–10)
    // -----------------------------
    private static int inputStep(String field,
                                 String name,
                                 int importance,
                                 int urgency,
                                 int effort,
                                 int length) {

        int value = 0;

        while (true) {

            render(name, importance, urgency, effort, length,
                    "ENTER " + field + " (1-10)", "");
            
            UI.printEmpty();
            UI.inputCenter(field + ": ");

            String input = scanner.nextLine().trim();

            try {
                value = Integer.parseInt(input);

                if (value < 1 || value > 10) {

                    render(name, importance, urgency, effort, length,
                            "ENTER " + field + " (1-10)",
                            "ERROR: Value must be between 1 and 10");

                    sleep();
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {

                render(name, importance, urgency, effort, length,
                        "ENTER " + field + " (1-10)",
                        "ERROR: Invalid number");

                sleep();
            }
        }
    }

    // -----------------------------
    // RENDER SCREEN (STATEFUL)
    // -----------------------------
    private static void render(String name,
                               int importance,
                               int urgency,
                               int effort,
                               int length,
                               String title,
                               String error) {

        UI.cls();

        System.out.println();
        UI.printFullWidth("=");
        UI.printEmpty();
        UI.printCenter("--- ADD TASK ---");
        UI.printEmpty();

        UI.printCenter("NAME : " + (name.isEmpty() ? "-" : name));
        UI.printCenter("IMPORTANCE: " + importance);
        UI.printCenter("URGENCY : " + urgency);
        UI.printCenter("EFFORT : " + effort);
        UI.printCenter("LENGTH : " + length);

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

    // -----------------------------
    // UTILITIES
    // -----------------------------
    private static void sleep() {
        try {
            Thread.sleep(1200);
        } catch (InterruptedException ignored) {}
    }

}