package ru.kpfu.itis.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Details {
    private String queueID;
    private MessageQueue message;

    @JsonProperty("queue_id")
    public String getQueue_id() {
        return queueID;
    }

    @JsonProperty("queue_id")
    public void setQueue_id(String queueID) {
        this.queueID = queueID;
    }

    @JsonProperty("message")
    public MessageQueue getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(MessageQueue message) {
        this.message = message;
    }
}
