package com.krs.exception;

public class AwsException extends RuntimeException {

    public AwsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
