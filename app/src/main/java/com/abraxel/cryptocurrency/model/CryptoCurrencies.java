package com.abraxel.cryptocurrency.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoCurrencies {
    private String coinName;
    private String pair;
    private String last;
    private String high;
    private String low;
    private String dailyPercent;
    private int logoResourceId;
}
