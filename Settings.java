public class Settings {

    public static class AI_Settings {
        public static int extentOfBacklogToLearnFrom = 10;
        public static double learningFactor = 0.3;
    }

    public static class UserSettings {
        public static int numberOfTasksToSuggest = 3;
    }

    public static void loadSettings() {
        // load settings from memory
    }
}