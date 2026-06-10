package com.pheonix.authenticationsystem.assets;

public class CredentialsAlreadyExistsException extends RuntimeException {
    public CredentialsAlreadyExistsException(String message) {
        super(message);
    }
}
