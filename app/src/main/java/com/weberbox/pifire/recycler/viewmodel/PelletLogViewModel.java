package com.weberbox.pifire.recycler.viewmodel;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;

import androidx.annotation.NonNull;

import com.weberbox.pifire.utils.StringUtils;

import java.text.ParseException;
import java.util.Date;

import timber.log.Timber;

public class PelletLogViewModel {

    private String mPelletID;
    private String mPelletDate;
    private String mPelletName;
    private int mPelletRating;

    public PelletLogViewModel(@NonNull final String pelletDate, @NonNull final String pelletName, @NonNull final Integer pelletRating) {
        setPelletID(pelletDate);
        setPelletName(pelletName);
        setPelletRating(StringUtils.getRatingText(pelletRating));

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
        return mPelletID;
    }

    public void setPelletID(@NonNull final String pelletID) {
        this.mPelletID = pelletID;
    }

    @NonNull
    public String getPelletDate() {
        return mPelletDate;
    }

    public void setPelletDate(@NonNull final String pelletDate) {
        this.mPelletDate = pelletDate;
    }

    @NonNull
    public String getPelletName() {
        return mPelletName;
    }

    public void setPelletName(@NonNull final String pelletName) {
        this.mPelletName = pelletName;
    }

    public int getPelletRating() {
        return mPelletRating;
    }

    public void setPelletRating(final int pelletRating) {
        this.mPelletRating = pelletRating;
    }

}
