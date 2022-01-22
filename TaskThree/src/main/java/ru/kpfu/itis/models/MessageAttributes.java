package ru.kpfu.itis.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageAttributes {

    private JmsSqsMessageType jmsSqsMessageType;

    @JsonProperty("JMS_SQSMessageType")
    public JmsSqsMessageType getMessageAttributeKey() {
        return jmsSqsMessageType;
    }

    @JsonProperty("JMS_SQSMessageType")
    public void setMessageAttributeKey(JmsSqsMessageType jmsSqsMessageType) {
        this.jmsSqsMessageType = jmsSqsMessageType;
    }
}
