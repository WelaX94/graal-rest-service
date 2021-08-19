package com.project.graalrestservice.representationModels;

import org.springframework.http.HttpStatus;

public class ExceptionInfo {
    private final String title;
    private final int status;
    private final String message;

    public ExceptionInfo(String message, HttpStatus httpStatus) {
        this.message = message;
        this.title = httpStatus.name();
        this.status = httpStatus.value();
    }

    public String getTitle() {
        return title;
    }
    public int getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }

}
