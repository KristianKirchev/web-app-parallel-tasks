package com.krs.exception;

public class TaskDoesNotExistException extends RuntimeException {

    public TaskDoesNotExistException(String message) {
        super(message);
    }
}
