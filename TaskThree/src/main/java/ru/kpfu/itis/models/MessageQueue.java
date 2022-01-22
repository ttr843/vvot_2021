package ru.kpfu.itis.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageQueue {

    private String messageID;
    private String md5OfBody;
    private String body;
    private Attributes attributes;
    private MessageAttributes messageAttributes;
    private String md5OfMessageAttributes;

    @JsonProperty("message_id")
    public String getMessageID() {
        return messageID;
    }

    @JsonProperty("message_id")
    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    @JsonProperty("md5_of_body")
    public String getMd5OfBody() {
        return md5OfBody;
    }

    @JsonProperty("md5_of_body")
    public void setMd5OfBody(String md5OfBody) {
        this.md5OfBody = md5OfBody;
    }

    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
    }

    @JsonProperty("attributes")
    public Attributes getAttributes() {
        return attributes;
    }

    @JsonProperty("attributes")
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @JsonProperty("message_attributes")
    public MessageAttributes getMessageAttributes() {
        return messageAttributes;
    }

    @JsonProperty("message_attributes")
    public void setMessageAttributes(MessageAttributes messageAttributes) {
        this.messageAttributes = messageAttributes;
    }

    @JsonProperty("md5_of_message_attributes")
    public String getMd5OfMessageAttributes() {
        return md5OfMessageAttributes;
    }

    @JsonProperty("md5_of_message_attributes")
    public void setMd5OfMessageAttributes(String md5OfMessageAttributes) {
        this.md5OfMessageAttributes = md5OfMessageAttributes;
    }
}
