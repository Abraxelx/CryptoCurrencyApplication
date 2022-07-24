package com.abraxel.cryptocurrency.formatter;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class LineChartXAxisFormatter extends IndexAxisValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        long emissionsMilliSince1970Time = ((long) value);
        Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
        DateFormat dateTimeFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        return dateTimeFormat.format(timeMilliseconds);
    }
}