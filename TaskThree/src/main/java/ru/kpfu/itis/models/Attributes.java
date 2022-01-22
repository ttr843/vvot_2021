package ru.kpfu.itis.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attributes {
    private String sentTimeStamp;
    private String approximateFirstReceiveTimeStamp;
    private String approximateReceiveCount;

    @JsonProperty("ApproximateFirstReceiveTimestamp")
    public String getApproximateFirstReceiveTimeStamp() {
        return approximateFirstReceiveTimeStamp;
    }

    @JsonProperty("ApproximateFirstReceiveTimestamp")
    public void setApproximateFirstReceiveTimeStamp(String approximateFirstReceiveTimeStamp) {
        this.approximateFirstReceiveTimeStamp = approximateFirstReceiveTimeStamp;
    }

    @JsonProperty("ApproximateReceiveCount")
    public String getApproximateReceiveCount() {
        return approximateReceiveCount;
    }

    @JsonProperty("ApproximateReceiveCount")
    public void setApproximateReceiveCount(String approximateReceiveCount) {
        this.approximateReceiveCount = approximateReceiveCount;
    }

    @JsonProperty("SentTimestamp")
    public String getSentTimeStamp() {
        return sentTimeStamp;
    }

    @JsonProperty("SentTimestamp")
    public void setSentTimeStamp(String sentTimeStamp) {
        this.sentTimeStamp = sentTimeStamp;
    }
}
