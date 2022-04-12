package com.weberbox.pifire.model.local;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.weberbox.pifire.R;

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

        if (eventText.toLowerCase().contains("script starting")) {
            setEventIcon("S");
            setEventIconColor(R.color.eventsScriptStart);
        } else if (eventText.toLowerCase().contains("mode started")) {
            setEventIcon("M");
            setEventIconColor(R.color.eventsModeStarted);
        } else if (eventText.toLowerCase().contains("script ended")) {
            setEventIcon("S");
            setEventIconColor(R.color.eventsScriptEnded);
        } else if (eventText.toUpperCase().contains("ERROR")) {
            setEventIcon("E");
            setEventIconColor(R.color.eventsError);
        } else if (eventText.toLowerCase().contains("mode ended")) {
            setEventIcon("M");
            setEventIconColor(R.color.eventsModeEnded);
        } else if (eventText.toUpperCase().contains("WARNING")) {
            setEventIcon("W");
            setEventIconColor(R.color.eventsWarning);
        } else if (eventText.toLowerCase().startsWith("*")) {
            setEventIcon("D");
            setEventIconColor(R.color.eventsDebug);
        } else {
            setEventIcon("I");
            setEventIconColor(R.color.eventsInfo);
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
