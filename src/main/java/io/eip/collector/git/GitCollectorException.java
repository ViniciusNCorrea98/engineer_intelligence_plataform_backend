package io.eip.collector.git;

public class GitCollectorException extends RuntimeException {

    public GitCollectorException(String message) {
        super(message);
    }

    public GitCollectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
