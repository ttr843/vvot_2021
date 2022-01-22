package ru.kpfu.itis.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JmsSqsMessageType {
    private String dataType;
    private String stringValue;

    @JsonProperty("data_type")
    public String getDataType() {
        return dataType;
    }

    @JsonProperty("data_type")
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @JsonProperty("string_value")
    public String getStringValue() {
        return stringValue;
    }

    @JsonProperty("string_value")
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
