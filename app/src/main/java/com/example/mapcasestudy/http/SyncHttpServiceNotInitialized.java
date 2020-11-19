package com.example.mapcasestudy.http;

public class SyncHttpServiceNotInitialized extends NullPointerException {

    public SyncHttpServiceNotInitialized() {
    }

    /**
     * Constructs a new {@code NullPointerException} with the current stack
     * trace and the specified detail message.
     *
     * @param detailMessage the detail message for this exception.
     */
    public SyncHttpServiceNotInitialized(String detailMessage) {
        super(detailMessage);
    }

    @Override
    public String getMessage() {
        return "Sync Http service not properly initialized.";
    }
}
