package com.cosmacare.cosmacare_user_service.exceptions;

public class ValidationErrorResponse extends ErrorResponse {

    private Object rejectedValue;   // Value that caused the error
    private Object allowedValues;   // List of allowed values
    private int status;             // HTTP status code

    public ValidationErrorResponse(
            String errorCode,
            String message,
            Object rejectedValue,
            Object allowedValues,
            String path,
            int status) {
        super(errorCode, message, path);
        this.rejectedValue = rejectedValue;
        this.allowedValues = allowedValues;
        this.status = status;
    }

    // Getters
    public Object getRejectedValue() { return rejectedValue; }
    public Object getAllowedValues() { return allowedValues; }
    public int getStatus() { return status; }
}
