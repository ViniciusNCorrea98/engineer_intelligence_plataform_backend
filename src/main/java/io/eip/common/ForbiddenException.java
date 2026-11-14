package io.eip.common;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("Access denied");
    }
}
