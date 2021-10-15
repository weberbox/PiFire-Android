package com.weberbox.pifire.ui.utils;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LineChartXAxisValueFormatter extends IndexAxisValueFormatter {

    @Override
    public String getFormattedValue(float value) {

        // Show time in local version
        LocalTime localTime = LocalTime.ofSecondOfDay((long) value);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");

        return formatter.format(localTime);
    }
}
