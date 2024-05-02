package com.weberbox.pifire.ui.dialogs;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AlertDialog;

import com.weberbox.pifire.R;
import com.weberbox.pifire.ui.dialogs.model.DialogButton;
import com.weberbox.pifire.ui.dialogs.model.DialogMessage;
import com.weberbox.pifire.ui.dialogs.model.DialogSwipeButton;
import com.weberbox.pifire.ui.dialogs.model.DialogTitle;

@SuppressWarnings("unused")
public final class MaterialDialogList extends AbstractDialog {

    @SuppressWarnings("rawtypes")
    private MaterialDialogList(@NonNull final Activity activity,
                               @NonNull DialogTitle title,
                               @NonNull DialogMessage message,
                               boolean cancelable,
                               boolean autoDismiss,
                               @NonNull DialogButton positiveButton,
                               @NonNull DialogButton neutralButton,
                               @NonNull DialogButton negativeButton,
                               @Nullable DialogSwipeButton swipeButton,
                               @RawRes int animationResId,
                               @NonNull String animationFile) {
        super(activity, title, message, cancelable, autoDismiss, positiveButton, neutralButton,
                negativeButton, swipeButton, animationResId, animationFile);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity,
                R.style.AlertDialogTheme);

        LayoutInflater inflater = activity.getLayoutInflater();

        View dialogView = createMaterialListView(inflater, null);

        builder.setView(dialogView);

        builder.setCancelable(cancelable);

        dialog = builder.create();
    }


    public static class Builder extends AbstractDialog.Builder<MaterialDialogList> {

        public Builder(@NonNull Activity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public MaterialDialogList build() {
            return new MaterialDialogList(
                    activity,
                    title,
                    message,
                    isCancelable,
                    autoDismiss,
                    positiveButton,
                    neutralButton,
                    negativeButton,
                    swipeButton,
                    animationResId,
                    animationFile
            );
        }
    }
}