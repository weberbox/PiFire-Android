package com.weberbox.pifire.recycler.viewmodel;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;

import androidx.annotation.NonNull;

import com.weberbox.pifire.utils.Log;
import com.weberbox.pifire.utils.StringUtils;

import java.text.ParseException;
import java.util.Date;

public class PelletLogViewModel {
    private static final String TAG = PelletLogViewModel.class.getSimpleName();

    private String mPelletDate;
    private String mPelletName;
    private int mPelletRating;

    public PelletLogViewModel(@NonNull final String pelletDate, @NonNull final String pelletName, @NonNull final Integer pelletRating) {
        setPelletName(pelletName);
        setPelletRating(StringUtils.getRatingText(pelletRating));

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(pelletDate);
            formatter.applyPattern("MM/dd");
            setPelletDate(formatter.format(date));
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date " + e.getMessage());
            setPelletDate(pelletDate);
        }

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
