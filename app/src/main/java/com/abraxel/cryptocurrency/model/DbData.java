package com.abraxel.cryptocurrency.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class DbData implements Serializable {
    private String coinName;
    private int coinVal;
    private String deviceKey;
}
