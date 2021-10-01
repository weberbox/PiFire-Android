package com.weberbox.pifire.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.LayoutPelletsListCardviewBinding;

public class PelletsCardViewRecycler extends CardView {

    private LayoutPelletsListCardviewBinding mBinding;
    private LinearLayout mHolderView;
    private TextView mHeaderTitle;
    private TextView mHeaderButton;
    private ImageView mHeaderIcon;
    private RecyclerView mRecyclerView;
    private boolean mButtonEnabled;

    public PelletsCardViewRecycler(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public PelletsCardViewRecycler(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PelletsCardViewRecycler(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mBinding = LayoutPelletsListCardviewBinding.inflate(LayoutInflater.from(context), this,
                true);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PelletsCardViewRecycler);

            String headerText = typedArray.getString(R.styleable.PelletsCardViewRecycler_pellets_header_title);
            String buttonText = typedArray.getString(R.styleable.PelletsCardViewRecycler_pellets_button_text);
            mButtonEnabled = typedArray.getBoolean(R.styleable.PelletsCardViewRecycler_pellets_button_enabled,
                    false);
            int headerIcon = typedArray.getResourceId(R.styleable.PelletsCardViewRecycler_pellets_header_icon,
                    R.drawable.ic_pellet_edit);

            headerText = headerText == null ? "" : headerText;
            buttonText = buttonText == null ? "" : buttonText;

            mHolderView = mBinding.pelletsCardviewHolder;
            mHeaderTitle = mBinding.cardHeaderTitle;
            mHeaderButton = mBinding.cardHeaderButton;
            mHeaderIcon = mBinding.cardHeaderIcon;
            mRecyclerView = mBinding.pelletsRecycler;

            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            mRecyclerView.setNestedScrollingEnabled(false);

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

    @SuppressWarnings("unused")
    public String getHeaderTitle() {
        return mHeaderTitle.getText().toString();
    }

    @SuppressWarnings("unused")
    public void setHeaderTitle(String text) {
        mHeaderTitle.setText(text);
    }

    @SuppressWarnings("unused")
    public String getButtonTitle() {
        return mHeaderButton.getText().toString();
    }

    @SuppressWarnings("unused")
    public void setButtonTitle(String text) {
        mHeaderButton.setText(text);
    }

    @SuppressWarnings("unused")
    public boolean getButtonEnabled() {
        return mButtonEnabled;
    }

    @SuppressWarnings("unused")
    public void setButtonEnabled(boolean buttonEnabled) {
        mButtonEnabled = buttonEnabled;
    }

    @SuppressWarnings("unused")
    public void setHeaderIcon(int icon) {
        mHeaderIcon.setImageResource(icon);
    }

    public TextView getHeaderButton() {
        return mHeaderButton;
    }

    public LinearLayout getHolderView() {
        return mHolderView;
    }

    public RecyclerView getRecycler() {
        return mRecyclerView;
    }

}
