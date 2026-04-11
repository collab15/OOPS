public class Settings {

    public static class AI_Settings {
        private static int extentOfBacklogToLearnFrom = 10;
        private static double learningFactor = 0.3;

        public static int getBacklogSize() {
            return extentOfBacklogToLearnFrom;
        }

        public static double getLearningFactor() {
            return learningFactor;
        }
    }

    public static class UserSettings {
        private static int numberOfTasksToSuggest = 3;

        public static int getSuggestionCount() {
            return numberOfTasksToSuggest;
        }
    }

    public static void loadSettings() {
        // load logic
    }
}
