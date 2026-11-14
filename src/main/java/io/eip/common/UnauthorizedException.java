package io.eip.common;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Authentication failed");
    }
}
