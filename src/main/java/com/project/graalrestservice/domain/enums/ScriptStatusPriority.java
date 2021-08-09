package com.project.graalrestservice.domain.enums;

public class ScriptStatusPriority {

    private int queue;
    private int running;
    private int successful;
    private int failed;
    private int canceled;

    public ScriptStatusPriority() {
        successful = 0;
        failed = 0;
        canceled = 0;
        running = 1;
        queue = 2;
    }

    public ScriptStatusPriority(char[] filters) {
        int count = 1;
        for(char filter: filters) {
            switch (filter) {
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
