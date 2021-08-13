package com.project.graalrestservice.domain.enums;

/**
 * Enum script status flag class. Also used for sorting.
 */
public enum ScriptStatus {
    IN_QUEUE('q'),
    RUNNING('r'),
    EXECUTION_SUCCESSFUL('s'),
    EXECUTION_FAILED('f'),
    EXECUTION_CANCELED('c');

    private final char letter;

    ScriptStatus(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    /**
     * A helpful class for setting the status priority
     */
    public static class Priority {

        private int queue;
        private int running;
        private int successful;
        private int failed;
        private int canceled;

        public Priority() {
            successful = 0;
            failed = 0;
            canceled = 0;
            running = 1;
            queue = 2;
        }

        public Priority(String filters) {
            int count = 1;
            for(int f = 0; f < filters.length(); f++) {
                switch (filters.charAt(f)) {
                    case 'q':
                        queue = count++;
                        break;
                    case 'r':
                        running = count++;
                        break;
                    case 's':
                        successful = count++;
                        break;
                    case 'f':
                        failed = count++;
                        break;
                    case 'c':
                        canceled = count++;
                        break;
                }
            }
        }

        public int getPriority(ScriptStatus scriptStatus) {
            switch (scriptStatus) {
                case IN_QUEUE:
                    return queue;
                case RUNNING:
                    return running;
                case EXECUTION_SUCCESSFUL:
                    return successful;
                case EXECUTION_FAILED:
                    return failed;
                case EXECUTION_CANCELED:
                    return canceled;
                default:
                    return -1;
            }
        }

    }

}
