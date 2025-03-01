package com.weberbox.pifire.model.local;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.util.Date;

import timber.log.Timber;

public class PelletLogModel {

    private final String pelletID;
    private final String pelletName;
    private final int pelletRating;
    private String pelletDate;

    public PelletLogModel(@NonNull final String pelletDate, @NonNull final String pelletName,
                          @NonNull final Integer pelletRating) {
        this.pelletID = pelletDate;
        this.pelletName = pelletName;
        this.pelletRating = pelletRating;

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(pelletDate);
            formatter.applyPattern("MM/dd");
            this.pelletDate = formatter.format(date);
        } catch (ParseException e) {
            Timber.w(e, "Error parsing date %s", e.getMessage());
            this.pelletDate = pelletDate;
        }

    }

    @NonNull
    public String getPelletID() {
        return pelletID;
    }

    @NonNull
    public String getPelletDate() {
        return pelletDate;
    }

    @NonNull
    public String getPelletName() {
        return pelletName;
    }

    public int getPelletRating() {
        return pelletRating;
    }

}
