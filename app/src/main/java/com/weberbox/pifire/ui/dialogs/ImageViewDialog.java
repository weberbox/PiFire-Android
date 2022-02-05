package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogImageViewBinding;
import com.weberbox.pifire.ui.utils.ViewUtils;

public class ImageViewDialog {

    private final LayoutInflater inflater;
    private final AlertDialog.Builder dialog;
    private final Context context;
    private final String imageUri;

    public ImageViewDialog(Context context, String imageUri) {
        dialog = new AlertDialog.Builder(context, R.style.AlertDialogImageView);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.imageUri = imageUri;
    }

    public AlertDialog.Builder showDialog() {
        DialogImageViewBinding binding = DialogImageViewBinding.inflate(inflater);

        final ImageView imageView = binding.dialogImageView;

        RequestOptions requestOptions = new RequestOptions()
                .transform(new RoundedCorners(ViewUtils.dpToPx(20)));

        Glide.with(context)
                .load(Uri.parse(imageUri))
                .error(R.drawable.ic_recipe_placeholder_error)
                .apply(requestOptions)
                .into(imageView);

        dialog.setView(binding.getRoot());

        dialog.show();

        return dialog;
    }
}
