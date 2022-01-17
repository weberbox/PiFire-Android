package com.weberbox.pifire.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.skydoves.androidveil.VeilRecyclerFrameView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.LayoutPelletsEditorBinding;
import com.weberbox.pifire.databinding.LayoutPelletsProfileAddBinding;


@SuppressWarnings("unused")
public class PelletsEditorRecycler extends CardView {

    private LayoutPelletsProfileAddBinding mAddProfileContainer;
    private VeilRecyclerFrameView mRecyclerView;
    private LinearLayout mAddProfileHolder;
    private TextView mHeaderTitle;
    private TextView mHeaderButton;
    private ImageView mHeaderIcon;
    private View mGradient;
    private TextView mViewAllButton;
    private boolean mButtonEnabled;

    public PelletsEditorRecycler(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public PelletsEditorRecycler(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PelletsEditorRecycler(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutPelletsEditorBinding binding = LayoutPelletsEditorBinding.inflate(
                LayoutInflater.from(context), this, true);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PelletsEditorRecycler);

            String headerText = typedArray.getString(R.styleable.PelletsEditorRecycler_editor_header_title);
            String buttonText = typedArray.getString(R.styleable.PelletsEditorRecycler_editor_button_text);
            mButtonEnabled = typedArray.getBoolean(R.styleable.PelletsEditorRecycler_editor_button_enabled,
                    false);
            int headerIcon = typedArray.getResourceId(R.styleable.PelletsEditorRecycler_editor_header_icon,
                    R.drawable.ic_pellet_edit);

            headerText = headerText == null ? "" : headerText;
            buttonText = buttonText == null ? "" : buttonText;

            mHeaderTitle = binding.cardHeaderTitle;
            mHeaderButton = binding.addProfileButton;
            mHeaderIcon = binding.editorHeaderIcon;
            mRecyclerView = binding.editorRecycler;
            mGradient = binding.editorViewAllShadow;
            mViewAllButton = binding.editorViewAll;
            mAddProfileContainer = binding.pelletsAddProfileContainer;
            mAddProfileHolder = binding.pelletsAddProfile;

            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.setNestedScrollingEnabled(false);
            mRecyclerView.addVeiledItems(3);

            mHeaderTitle.setText(headerText);
            mHeaderIcon.setImageResource(headerIcon);

            if (mButtonEnabled) {
                mHeaderButton.setText(buttonText);
            } else {
                mHeaderButton.setVisibility(GONE);
            }

            typedArray.recycle();
        }
    }

    public String getHeaderTitle() {
        return mHeaderTitle.getText().toString();
    }

    public void setHeaderTitle(String text) {
        mHeaderTitle.setText(text);
    }

    public String getButtonTitle() {
        return mHeaderButton.getText().toString();
    }

    public void setButtonTitle(String text) {
        mHeaderButton.setText(text);
    }

    public boolean getButtonEnabled() {
        return mButtonEnabled;
    }

    public void setButtonEnabled(boolean buttonEnabled) {
        mButtonEnabled = buttonEnabled;
    }

    public void setHeaderIcon(int icon) {
        mHeaderIcon.setImageResource(icon);
    }

    public TextView getHeaderButton() {
        return mHeaderButton;
    }

    public LinearLayout getAddProfileView() {
        return mAddProfileHolder;
    }

    public LayoutPelletsProfileAddBinding getAddProfileContainer() {
        return mAddProfileContainer;
    }

    public VeilRecyclerFrameView getRecycler() {
        return mRecyclerView;
    }

    public void setViewAll(boolean shown) {
        mGradient.setVisibility(shown ? VISIBLE : GONE);
        mViewAllButton.setVisibility(shown ? VISIBLE : GONE);
    }

    public TextView getViewAllButton() {
        return mViewAllButton;
    }

}
