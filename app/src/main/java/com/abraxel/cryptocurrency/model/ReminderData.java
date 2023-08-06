package com.abraxel.cryptocurrency.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ReminderData implements Serializable {
    private String coinName;
    private int currentValue;
    private int desiredValue;
    private String deviceKey;
    private boolean isNotified;
}
