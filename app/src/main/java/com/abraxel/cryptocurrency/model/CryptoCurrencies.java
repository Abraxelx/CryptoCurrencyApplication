package com.abraxel.cryptocurrency.model;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CryptoCurrencies implements Serializable {
    private String coinName;
    private String pair;
    private String last;
    private String high;
    private String low;
    private String dailyPercent;
    private int logoResourceId;
}
