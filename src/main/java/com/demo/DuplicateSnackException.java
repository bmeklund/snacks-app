package com.demo;

public class DuplicateSnackException extends RuntimeException {

    public DuplicateSnackException(String snackName) {
        super("Snack with name '" + snackName + "' already exists.");
    }

    public DuplicateSnackException(String snackName, Throwable cause) {
        super("Snack with name '" + snackName + "' already exists.", cause);
    }
}
