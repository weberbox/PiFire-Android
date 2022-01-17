package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogImageViewBinding;

public class ImageViewDialog {

    private final LayoutInflater mInflater;
    private final AlertDialog.Builder mDialog;
    private final Context mContext;
    private final String mImageUri;

    public ImageViewDialog(Context context, String uri) {
        mDialog = new AlertDialog.Builder(context, R.style.AlertDialogImageView);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mImageUri = uri;
    }

    public AlertDialog.Builder showDialog() {
        DialogImageViewBinding binding = DialogImageViewBinding.inflate(mInflater);

        final ImageView imageView = binding.dialogImageView;

        Glide.with(mContext)
                .load(Uri.parse(mImageUri))
                .error(R.drawable.ic_recipe_placeholder_error)
                .into(imageView);

        mDialog.setView(binding.getRoot());

        mDialog.show();

        return mDialog;
    }
}
