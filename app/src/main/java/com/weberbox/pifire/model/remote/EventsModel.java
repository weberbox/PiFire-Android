package com.weberbox.pifire.model.remote;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weberbox.pifire.R;
import com.weberbox.pifire.utils.adapters.CustomTypeAdapterFactory;

import java.util.List;

@SuppressWarnings("unused")
public class EventsModel {

    @SerializedName("events_list")
    @Expose
    private List<List<String>> eventsList;

    public List<List<String>> getEventsList() {
        return eventsList;
    }

    public void setEventsList(List<List<String>> eventsList) {
        this.eventsList = eventsList;
    }

    public static class Events {

        private final int eventIconColor;
        private final String eventIcon;
        private final String eventDate;
        private final String eventTime;
        private final String eventText;

        public Events(@NonNull final String eventDate, @NonNull final String eventTime,
                           @NonNull final String eventText) {
            this.eventDate = eventDate;
            this.eventTime = eventTime;
            this.eventText = eventText;

            if (eventText.toLowerCase().contains("script starting")) {
                this.eventIcon = "S";
                this.eventIconColor = R.color.eventsScriptStart;
            } else if (eventText.toLowerCase().contains("mode started")) {
                this.eventIcon = "M";
                this.eventIconColor = R.color.eventsModeStarted;
            } else if (eventText.toLowerCase().contains("script ended")) {
                this.eventIcon = "S";
                this.eventIconColor = R.color.eventsScriptEnded;
            } else if (eventText.toUpperCase().contains("ERROR")) {
                this.eventIcon = "E";
                this.eventIconColor = R.color.eventsError;
            } else if (eventText.toLowerCase().contains("mode ended")) {
                this.eventIcon = "M";
                this.eventIconColor = R.color.eventsModeEnded;
            } else if (eventText.toUpperCase().contains("WARNING")) {
                this.eventIcon = "W";
                this.eventIconColor = R.color.eventsWarning;
            } else if (eventText.toLowerCase().startsWith("*")) {
                this.eventIcon = "D";
                this.eventIconColor = R.color.eventsDebug;
            } else {
                this.eventIcon = "I";
                this.eventIconColor = R.color.eventsInfo;
            }
        }

        public int getEventIconColor() {
            return eventIconColor;
        }

        @NonNull
        public String getEventIcon() {
            return eventIcon;
        }

        @NonNull
        public String getEventDate() {
            return eventDate;
        }

        @NonNull
        public String getEventTime() {
            return eventTime;
        }

        @NonNull
        public String getEventText() {
            return eventText;
        }
    }

    public static EventsModel parseJSON(String response) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .registerTypeAdapterFactory(new CustomTypeAdapterFactory())
                .create();
        return gson.fromJson(response, EventsModel.class);
    }

}
