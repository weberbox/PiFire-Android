package com.weberbox.pifire.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.LayoutCardviewHeaderBinding;

public class CardViewHeaderButton extends LinearLayout {

    private LayoutCardviewHeaderBinding mBinding;
    private TextView mHeaderTitle;
    private TextView mButtonTitle;
    private ImageView mHeaderIcon;
    private boolean mButtonEnabled;

    public CardViewHeaderButton(Context context) {
        super(context);
        init(context, null);
    }

    public CardViewHeaderButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CardViewHeaderButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CardViewHeaderButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mBinding = LayoutCardviewHeaderBinding.inflate(LayoutInflater.from(context), this, true);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CardViewHeaderButton);

            String headerText = typedArray.getString(R.styleable.CardViewHeaderButton_header_title);
            String buttonText = typedArray.getString(R.styleable.CardViewHeaderButton_button_text);
            mButtonEnabled = typedArray.getBoolean(R.styleable.CardViewHeaderButton_button_enabled,
                    false);
            int headerIcon = typedArray.getResourceId(R.styleable.CardViewHeaderButton_header_icon,
                    R.drawable.ic_pellet_edit);

            headerText = headerText == null ? "" : headerText;
            buttonText = buttonText == null ? "" : buttonText;

            mHeaderTitle = mBinding.cardHeaderTitle;
            mButtonTitle = mBinding.cardHeaderButton;
            mHeaderIcon = mBinding.cardHeaderIcon;

            mHeaderTitle.setText(headerText);
            mButtonTitle.setText(buttonText);
            mHeaderIcon.setImageResource(headerIcon);

            if (mButtonEnabled) {
                mButtonTitle.setText(buttonText);
            } else {
                mButtonTitle.setVisibility(GONE);
            }

            typedArray.recycle();
        }
    }

    @SuppressWarnings("unused")
    public void setHeaderTitle(String text) {
        mHeaderTitle.setText(text);
    }

    @SuppressWarnings("unused")
    public void setButtonTitle(String text) {
        mButtonTitle.setText(text);
    }

    @SuppressWarnings("unused")
    public TextView getButton() {
        return mButtonTitle;
    }

    @SuppressWarnings("unused")
    public String getHeaderTitle() {
        return mHeaderTitle.getText().toString();
    }

    @SuppressWarnings("unused")
    public String getButtonTitle() {
        return mButtonTitle.getText().toString();
    }

    @SuppressWarnings("unused")
    public void setHeaderIcon(int icon) {
        mHeaderIcon.setImageResource(icon);
    }

    @SuppressWarnings("unused")
    public boolean getButtonEnabled() {
        return mButtonEnabled;
    }

    @SuppressWarnings("unused")
    public void setButtonEnabled(boolean buttonEnabled) {
        mButtonEnabled = buttonEnabled;
    }

}