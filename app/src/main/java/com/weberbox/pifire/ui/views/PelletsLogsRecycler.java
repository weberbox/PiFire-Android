package com.weberbox.pifire.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.skydoves.androidveil.VeilRecyclerFrameView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.LayoutPelletsLogsBinding;

@SuppressWarnings("unused")
public class PelletsLogsRecycler extends CardView {

    private VeilRecyclerFrameView mRecyclerView;
    private TextView mHeaderTitle;
    private ImageView mHeaderIcon;
    private View mGradient;
    private TextView mViewAllButton;

    public PelletsLogsRecycler(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public PelletsLogsRecycler(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PelletsLogsRecycler(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutPelletsLogsBinding binding = LayoutPelletsLogsBinding.inflate(
                LayoutInflater.from(context), this, true);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PelletsLogsRecycler);

            String headerText = typedArray.getString(R.styleable.PelletsLogsRecycler_logs_header_title);
            int headerIcon = typedArray.getResourceId(R.styleable.PelletsLogsRecycler_logs_header_icon,
                    R.drawable.ic_menu_history);

            headerText = headerText == null ? "" : headerText;

            mHeaderTitle = binding.logsHeaderText;
            mHeaderIcon = binding.logsHeaderIcon;
            mRecyclerView = binding.logsRecycler;
            mGradient = binding.logsViewAllShadow;
            mViewAllButton = binding.logsViewAll;

            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.setNestedScrollingEnabled(false);
            mRecyclerView.addVeiledItems(3);


            mHeaderTitle.setText(headerText);
            mHeaderIcon.setImageResource(headerIcon);

            typedArray.recycle();
        }
    }

    public String getHeaderTitle() {
        return mHeaderTitle.getText().toString();
    }

    public void setHeaderTitle(String text) {
        mHeaderTitle.setText(text);
    }

    public void setHeaderIcon(int icon) {
        mHeaderIcon.setImageResource(icon);
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
