package com.weberbox.pifire.recycler.viewmodel;

import android.graphics.Color;

import androidx.annotation.NonNull;

public class EventViewModel {

    private int mEventIconColor;
    private String mEventIcon;
    private String mEventDate;
    private String mEventTime;
    private String mEventText;

    public EventViewModel(@NonNull final String eventDate, @NonNull final String eventTime, @NonNull final String eventText) {
        setEventDate(eventDate);
        setEventTime(eventTime);
        setEventText(eventText);

        if(eventText.toLowerCase().contains("script starting")) {
            setEventIcon("S");
            setEventIconColor(Color.rgb(124, 220, 196)); // table-success
        } else if(eventText.toLowerCase().contains("mode started")) {
            setEventIcon("M");
            setEventIconColor(Color.rgb(133, 193, 234)); // table-info
        } else if(eventText.toLowerCase().contains("script ended")) {
            setEventIcon("S");
            setEventIconColor(Color.rgb(239, 143, 132)); // table-danger
        } else if(eventText.toUpperCase().contains("ERROR")) {
            setEventIcon("E");
            setEventIconColor(Color.rgb(239, 143, 132)); // table-danger
        } else if(eventText.toLowerCase().contains("mode ended")) {
            setEventIcon("M");
            setEventIconColor(Color.rgb(249, 196, 116)); // table-warning
        } else if(eventText.toUpperCase().contains("WARNING")) {
            setEventIcon("W");
            setEventIconColor(Color.rgb(249, 196, 116)); // table-warning
        } else {
            setEventIcon("I");
            setEventIconColor(Color.GRAY);
        }
    }

    public int getEventIconColor() {
        return mEventIconColor;
    }

    public void setEventIconColor(final int eventIconColor) {
        this.mEventIconColor = eventIconColor;
    }

    @NonNull
    public String getEventIcon() {
        return mEventIcon;
    }

    public void setEventIcon(@NonNull final String eventIcon) {
        this.mEventIcon = eventIcon;
    }

    @NonNull
    public String getEventDate() {
        return mEventDate;
    }

    public void setEventDate(@NonNull final String eventDate) {
        this.mEventDate = eventDate;
    }

    @NonNull
    public String getEventTime() {
        return mEventTime;
    }

    public void setEventTime(@NonNull final String eventTime) {
        this.mEventTime = eventTime;
    }

    @NonNull
    public String getEventText() {
        return mEventText;
    }

    public void setEventText(@NonNull final String eventText) {
        this.mEventText = eventText;
    }
}
