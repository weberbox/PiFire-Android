package com.weberbox.pifire.recycler.viewholder;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.interfaces.LicensesCallbackInterface;
import com.weberbox.pifire.recycler.viewmodel.LicensesViewModel;

public class LicensesViewHolder extends RecyclerView.ViewHolder {

    private final TextView mProjectIcon;
    private final TextView mProjectText;
    private final TextView mProjectLicense;

    public LicensesViewHolder(final View itemView, LicensesCallbackInterface callback) {
        super(itemView);
        mProjectIcon = itemView.findViewById(R.id.license_icon_holder);
        mProjectText = itemView.findViewById(R.id.license_project_name);
        mProjectLicense = itemView.findViewById(R.id.license_text_holder);

        itemView.setOnClickListener(v -> callback.licenseClick(mProjectLicense.getText().toString()));
    }

    public void bindData(final LicensesViewModel viewModel) {
        ((GradientDrawable) mProjectIcon.getBackground()).setColor(viewModel.getEventIconColor());
        mProjectIcon.setText(viewModel.getProjectIcon());
        mProjectText.setText(viewModel.getProjectText());
        mProjectLicense.setText(viewModel.getProjectLicense());
    }
}
