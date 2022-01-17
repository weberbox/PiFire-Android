package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogImagePickerBinding;
import com.weberbox.pifire.interfaces.PickerOptionCallback;
import com.weberbox.pifire.ui.utils.ViewUtils;

public class ImagePickerDialog {

    private final BottomSheetDialog mBottomSheetDialog;
    private final LayoutInflater mInflater;
    private final PickerOptionCallback mCallBack;
    private final Context mContext;

    public ImagePickerDialog(Context context, PickerOptionCallback callback) {
        mBottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mCallBack = callback;
    }

    public BottomSheetDialog showDialog(){
        DialogImagePickerBinding binding = DialogImagePickerBinding.inflate(mInflater);

        LinearLayout cameraButton = binding.dialogPickerCamera;
        LinearLayout galleryButton = binding.dialogPickerGallery;

        cameraButton.setOnClickListener(v -> {
            mCallBack.onTakeCameraSelected();
            mBottomSheetDialog.dismiss();
        });

        galleryButton.setOnClickListener(v -> {
            mCallBack.onChooseGallerySelected();
            mBottomSheetDialog.dismiss();
        });

        mBottomSheetDialog.setContentView(binding.getRoot());

        mBottomSheetDialog.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        mBottomSheetDialog.show();

        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            mBottomSheetDialog.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return mBottomSheetDialog;
    }
}
