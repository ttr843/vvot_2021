package ru.kpfu.itis.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventMetadata {
    private String eventID;
    private String eventType;
    private String createdAt;
    private String tracingContext;
    private String cloudID;
    private String folderID;

    @JsonProperty("event_id")
    public String getEventID() { return eventID; }
    @JsonProperty("event_id")
    public void setEventID(String value) { this.eventID = value; }

    @JsonProperty("event_type")
    public String getEventType() { return eventType; }
    @JsonProperty("event_type")
    public void setEventType(String value) { this.eventType = value; }

    @JsonProperty("created_at")
    public String getCreatedAt() { return createdAt; }
    @JsonProperty("created_at")
    public void setCreatedAt(String value) { this.createdAt = value; }

    @JsonProperty("tracing_context")
    public String getTracingContext() {
        return tracingContext;
    }

    @JsonProperty("tracing_context")
    public void setTracingContext(String tracingContext) {
        this.tracingContext = tracingContext;
    }

    @JsonProperty("cloud_id")
    public String getCloudID() {
        return cloudID;
    }

    @JsonProperty("cloud_id")
    public void setCloudID(String cloudID) {
        this.cloudID = cloudID;
    }

    @JsonProperty("folder_id")
    public String getFolderID() {
        return folderID;
    }

    @JsonProperty("folder_id")
    public void setFolderID(String folderID) {
        this.folderID = folderID;
    }
}
