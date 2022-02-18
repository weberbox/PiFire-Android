package com.weberbox.pifire.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;

import java.util.Locale;

@SuppressLint("ViewConstructor")
public class CustomMarkerView extends MarkerView {

    private final TextView textView;
    private final boolean useFahrenheit;

    public CustomMarkerView(Context context, int layoutResource, int valueSize, int valueColor) {
        super(context, layoutResource);
        useFahrenheit = Prefs.getString(context.getString(R.string.prefs_grill_units)).equals("F");
        textView = findViewById(R.id.chart_marker);
        textView.setTextSize(valueSize);
        textView.setTextColor(valueColor);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String temp;
        if (useFahrenheit) {
            temp = String.format(Locale.US, "%.0f %s", e.getY(), "F");
        } else {
            temp = String.format(Locale.US, "%s %s", e.getY(), "C");
        }
        textView.setText(temp);
    }

}