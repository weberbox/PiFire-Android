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

import com.google.android.material.materialswitch.MaterialSwitch;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.ItemTempTypeBinding;

@SuppressWarnings("unused")
public class ProbeTypeCard extends CardView {

    private TextView typeTitle;
    private ImageView typeIcon;

    private MaterialSwitch reqSwitch;

    public ProbeTypeCard(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ProbeTypeCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProbeTypeCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ItemTempTypeBinding binding = ItemTempTypeBinding.inflate(LayoutInflater.from(context),
                this, true);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProbeTypeCard);

            String title = typedArray.getString(R.styleable.ProbeTypeCard_probe_type_title);
            int icon = typedArray.getResourceId(R.styleable.ProbeTypeCard_probe_type_icon,
                    R.drawable.ic_menu_target);

            title = title == null ? "" : title;

            typeTitle = binding.probeTempTypeText;
            typeIcon = binding.probeTempTypeIcon;
            reqSwitch = binding.probeTypeReqSwitch;

            typeTitle.setText(title);
            typeIcon.setImageResource(icon);

            typedArray.recycle();
        }
    }

    public TextView getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String title) {
        typeTitle.setText(title);
    }

    public ImageView getTypeIcon() {
        return typeIcon;
    }

    public void setTypeIcon(int icon) {
        typeIcon.setImageResource(icon);
    }

    public MaterialSwitch getReqSwitch() {
        return reqSwitch;
    }

}

