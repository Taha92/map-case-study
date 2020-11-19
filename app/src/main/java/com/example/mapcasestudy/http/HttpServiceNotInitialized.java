package com.example.mapcasestudy.http;


public class HttpServiceNotInitialized extends NullPointerException {

    public HttpServiceNotInitialized() {
    }

    /**
     * Constructs a new {@code NullPointerException} with the current stack
     * trace and the specified detail message.
     *
     * @param detailMessage the detail message for this exception.
     */
    public HttpServiceNotInitialized(String detailMessage) {
        super(detailMessage);
    }

    @Override
    public String getMessage() {
        return "Http service not properly initialized.";
    }
}
