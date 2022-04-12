package com.weberbox.pifire.model.local;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.util.Date;

import timber.log.Timber;

public class PelletLogModel {

    private String pelletID;
    private String pelletDate;
    private String pelletName;
    private int pelletRating;

    public PelletLogModel(@NonNull final String pelletDate, @NonNull final String pelletName,
                          @NonNull final Integer pelletRating) {
        setPelletID(pelletDate);
        setPelletName(pelletName);
        setPelletRating(pelletRating);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(pelletDate);
            formatter.applyPattern("MM/dd");
            setPelletDate(formatter.format(date));
        } catch (ParseException e) {
            Timber.w(e, "Error parsing date %s", e.getMessage());
            setPelletDate(pelletDate);
        }

    }

    @NonNull
    public String getPelletID() {
        return pelletID;
    }

    public void setPelletID(@NonNull final String pelletID) {
        this.pelletID = pelletID;
    }

    @NonNull
    public String getPelletDate() {
        return pelletDate;
    }

    public void setPelletDate(@NonNull final String pelletDate) {
        this.pelletDate = pelletDate;
    }

    @NonNull
    public String getPelletName() {
        return pelletName;
    }

    public void setPelletName(@NonNull final String pelletName) {
        this.pelletName = pelletName;
    }

    public int getPelletRating() {
        return pelletRating;
    }

    public void setPelletRating(final int pelletRating) {
        this.pelletRating = pelletRating;
    }

}
