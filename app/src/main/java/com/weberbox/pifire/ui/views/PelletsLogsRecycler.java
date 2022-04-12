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

import com.skydoves.androidveil.VeilRecyclerFrameView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.LayoutPelletsLogsBinding;
import com.weberbox.pifire.recycler.manager.ScrollDisableLayoutManager;

@SuppressWarnings("unused")
public class PelletsLogsRecycler extends CardView {

    private VeilRecyclerFrameView recyclerView;
    private TextView headerTitle;
    private ImageView headerIcon;
    private View gradient;
    private TextView viewAllButton;

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
            int headerIconArray = typedArray.getResourceId(R.styleable.PelletsLogsRecycler_logs_header_icon,
                    R.drawable.ic_menu_history);

            headerText = headerText == null ? "" : headerText;

            headerTitle = binding.logsHeaderText;
            headerIcon = binding.logsHeaderIcon;
            recyclerView = binding.logsRecycler;
            gradient = binding.logsViewAllShadow;
            viewAllButton = binding.logsViewAll;

            recyclerView.setLayoutManager(new ScrollDisableLayoutManager(context));
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.addVeiledItems(3);

            headerTitle.setText(headerText);
            headerIcon.setImageResource(headerIconArray);

            typedArray.recycle();
        }
    }

    public String getHeaderTitle() {
        return headerTitle.getText().toString();
    }

    public void setHeaderTitle(String text) {
        headerTitle.setText(text);
    }

    public void setHeaderIcon(int icon) {
        headerIcon.setImageResource(icon);
    }

    public VeilRecyclerFrameView getRecycler() {
        return recyclerView;
    }

    public void setViewAll(boolean shown) {
        gradient.setVisibility(shown ? VISIBLE : GONE);
        viewAllButton.setVisibility(shown ? VISIBLE : GONE);
    }

    public TextView getViewAllButton() {
        return viewAllButton;
    }

}
