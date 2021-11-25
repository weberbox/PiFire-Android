package com.weberbox.pifire.ui.utils;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.weberbox.pifire.utils.TimeUtils;

public class LineChartXAxisValueFormatter extends IndexAxisValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        return TimeUtils.getTimeFormatted(value, "hh:mm:ss");
    }
}
