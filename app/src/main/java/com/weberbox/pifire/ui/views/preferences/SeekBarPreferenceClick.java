package com.weberbox.pifire.ui.views.preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SeekBarPreference;

@SuppressWarnings("unused")
public class SeekBarPreferenceClick extends SeekBarPreference {

    private Context context;

    public SeekBarPreferenceClick(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public SeekBarPreferenceClick(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SeekBarPreferenceClick(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SeekBarPreferenceClick(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {

    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false);
    }
}
