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

    private final LayoutInflater inflater;
    private final AlertDialog.Builder dialog;
    private final Context context;
    private final String imageUri;

    public ImageViewDialog(Context context, String uri) {
        dialog = new AlertDialog.Builder(context, R.style.AlertDialogImageView);
        inflater = LayoutInflater.from(context);
        this.context = context;
        imageUri = uri;
    }

    public AlertDialog.Builder showDialog() {
        DialogImageViewBinding binding = DialogImageViewBinding.inflate(inflater);

        final ImageView imageView = binding.dialogImageView;

        Glide.with(context)
                .load(Uri.parse(imageUri))
                .error(R.drawable.ic_recipe_placeholder_error)
                .into(imageView);

        dialog.setView(binding.getRoot());

        dialog.show();

        return dialog;
    }
}
