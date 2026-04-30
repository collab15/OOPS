public class Settings {

    public static class AI_Settings {
        private static int extentOfBacklogToLearnFrom = 10; // AI will look at last 10 tasks to look at 
        private static double learningFactor = 0.3; // controls how fast the AI learns

        public static int getBacklogSize() {
            return extentOfBacklogToLearnFrom;
        }

        public static double getLearningFactor() {
            return learningFactor;
        }

        public static void setBacklogSize(int size) {
            extentOfBacklogToLearnFrom = size;
        }

        public static void setLearningFactor(double factor) {
            learningFactor = factor;
        }
    }

    public static class UserSettings {
        private static int numberOfTasksToSuggest = 5;

        public static int getSuggestionCount() {
            return numberOfTasksToSuggest;
        }
        public static void setSuggestionCount(int count) {
            numberOfTasksToSuggest = count;
        }
    }

    public static class AppSettings{
        private static int displayWidth = 115;
        
        private static String localStorageDirectory = "local/";

        public static int getDisplayWidth() {
            return displayWidth;
        }

        public static String getLocalStorageDirectory() {
            return localStorageDirectory;
        }

    }

    public static void loadSettings() {
        // load logic
    }
}

// static inner classes so u dont need to create an object of settings to use it can directly call it 
