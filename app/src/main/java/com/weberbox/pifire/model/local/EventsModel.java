package com.weberbox.pifire.model.local;

import android.graphics.Color;

import androidx.annotation.NonNull;

public class EventsModel {

    private int eventIconColor;
    private String eventIcon;
    private String eventDate;
    private String eventTime;
    private String eventText;

    public EventsModel(@NonNull final String eventDate, @NonNull final String eventTime,
                       @NonNull final String eventText) {
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
        } else if(eventText.toLowerCase().startsWith("*")) {
            setEventIcon("D");
            setEventIconColor(Color.rgb(47, 126, 245)); // debug
        } else {
            setEventIcon("I");
            setEventIconColor(Color.GRAY);
        }
    }

    public int getEventIconColor() {
        return eventIconColor;
    }

    public void setEventIconColor(final int eventIconColor) {
        this.eventIconColor = eventIconColor;
    }

    @NonNull
    public String getEventIcon() {
        return eventIcon;
    }

    public void setEventIcon(@NonNull final String eventIcon) {
        this.eventIcon = eventIcon;
    }

    @NonNull
    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(@NonNull final String eventDate) {
        this.eventDate = eventDate;
    }

    @NonNull
    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(@NonNull final String eventTime) {
        this.eventTime = eventTime;
    }

    @NonNull
    public String getEventText() {
        return eventText;
    }

    public void setEventText(@NonNull final String eventText) {
        this.eventText = eventText;
    }
}
