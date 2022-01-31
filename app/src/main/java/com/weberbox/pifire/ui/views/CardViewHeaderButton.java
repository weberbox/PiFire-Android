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

@SuppressWarnings("unused")
public class CardViewHeaderButton extends LinearLayout {

    private TextView headerTitle;
    private TextView buttonTitle;
    private ImageView headerIcon;
    private boolean buttonEnabled;

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
        LayoutCardviewHeaderBinding binding = LayoutCardviewHeaderBinding.inflate(
                LayoutInflater.from(context), this, true);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CardViewHeaderButton);

            String headerText = typedArray.getString(R.styleable.CardViewHeaderButton_header_title);
            String buttonText = typedArray.getString(R.styleable.CardViewHeaderButton_button_text);
            buttonEnabled = typedArray.getBoolean(R.styleable.CardViewHeaderButton_button_enabled,
                    false);
            int headerIconArray = typedArray.getResourceId(R.styleable.CardViewHeaderButton_header_icon,
                    R.drawable.ic_pellet_edit);

            headerText = headerText == null ? "" : headerText;
            buttonText = buttonText == null ? "" : buttonText;

            headerTitle = binding.cardHeaderTitle;
            buttonTitle = binding.cardHeaderButton;
            headerIcon = binding.cardHeaderIcon;

            headerTitle.setText(headerText);
            buttonTitle.setText(buttonText);
            headerIcon.setImageResource(headerIconArray);

            if (buttonEnabled) {
                buttonTitle.setText(buttonText);
            } else {
                buttonTitle.setVisibility(GONE);
            }

            typedArray.recycle();
        }
    }

    public void setHeaderTitle(String text) {
        headerTitle.setText(text);
    }

    public void setButtonTitle(String text) {
        buttonTitle.setText(text);
    }

    public TextView getButton() {
        return buttonTitle;
    }

    public String getHeaderTitle() {
        return headerTitle.getText().toString();
    }

    public String getButtonTitle() {
        return buttonTitle.getText().toString();
    }

    public void setHeaderIcon(int icon) {
        headerIcon.setImageResource(icon);
    }

    public boolean getButtonEnabled() {
        return buttonEnabled;
    }

    public void setButtonEnabled(boolean buttonEnabled) {
        this.buttonEnabled = buttonEnabled;
    }

}
