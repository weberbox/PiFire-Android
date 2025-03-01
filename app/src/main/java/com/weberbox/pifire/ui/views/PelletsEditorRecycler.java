package com.weberbox.pifire.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.skydoves.androidveil.VeilRecyclerFrameView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.LayoutPelletsEditorBinding;
import com.weberbox.pifire.recycler.manager.ScrollDisableLayoutManager;

@SuppressWarnings("unused")
public class PelletsEditorRecycler extends CardView {

    private VeilRecyclerFrameView recyclerView;
    private TextView headerTitle;
    private TextView headerButton;
    private ImageView headerIcon;
    private boolean buttonEnabled;

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
            buttonEnabled = typedArray.getBoolean(R.styleable.PelletsEditorRecycler_editor_button_enabled,
                    false);
            int headerIconArray = typedArray.getResourceId(R.styleable.PelletsEditorRecycler_editor_header_icon,
                    R.drawable.ic_pellet_edit);

            headerText = headerText == null ? "" : headerText;
            buttonText = buttonText == null ? "" : buttonText;

            headerTitle = binding.cardHeaderTitle;
            headerButton = binding.addProfileButton;
            headerIcon = binding.editorHeaderIcon;
            recyclerView = binding.editorRecycler;

            recyclerView.setLayoutManager(new ScrollDisableLayoutManager(context));
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.addVeiledItems(3);

            headerTitle.setText(headerText);
            headerIcon.setImageResource(headerIconArray);

            if (buttonEnabled) {
                headerButton.setText(buttonText);
            } else {
                headerButton.setVisibility(GONE);
            }

            typedArray.recycle();
        }
    }

    public String getHeaderTitle() {
        return headerTitle.getText().toString();
    }

    public void setHeaderTitle(String text) {
        headerTitle.setText(text);
    }

    public String getButtonTitle() {
        return headerButton.getText().toString();
    }

    public void setButtonTitle(String text) {
        headerButton.setText(text);
    }

    public boolean getButtonEnabled() {
        return buttonEnabled;
    }

    public void setButtonEnabled(boolean buttonEnabled) {
        this.buttonEnabled = buttonEnabled;
    }

    public void setHeaderIcon(int icon) {
        headerIcon.setImageResource(icon);
    }

    public TextView getHeaderButton() {
        return headerButton;
    }

    public VeilRecyclerFrameView getRecycler() {
        return recyclerView;
    }

}
