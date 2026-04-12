package com.project.exceptions;

public class MediaDurationExceededException extends RuntimeException {
    public MediaDurationExceededException(String message) {
        super(message);
    }
}
