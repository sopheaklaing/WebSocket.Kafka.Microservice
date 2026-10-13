package com.sopheak.order_service.event;

public class ServiceErrorEvent {
    private String serviceName;
    private String errorMessage;
    private String severity;
    private String timestamp;

    public ServiceErrorEvent() {

    }

    public ServiceErrorEvent(
            String serviceName,
            String errorMessage,
            String severity,
            String timestamp) {
        this.serviceName = serviceName;
        this.errorMessage = errorMessage;
        this.severity = severity;
        this.timestamp = timestamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}