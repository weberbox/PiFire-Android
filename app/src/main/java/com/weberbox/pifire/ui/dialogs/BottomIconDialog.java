package com.weberbox.pifire.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.weberbox.pifire.R;
import com.weberbox.pifire.ui.dialogs.model.DialogButton;
import com.weberbox.pifire.ui.dialogs.model.DialogMessage;
import com.weberbox.pifire.ui.dialogs.model.DialogSwipeButton;
import com.weberbox.pifire.ui.dialogs.model.DialogTitle;
import com.weberbox.pifire.ui.utils.ViewUtils;

public final class BottomIconDialog extends AbstractDialog {

    @SuppressWarnings("rawtypes")
    private BottomIconDialog(@NonNull final Activity activity,
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

        dialog = new BottomSheetDialog(activity);

        LayoutInflater inflater = activity.getLayoutInflater();

        View dialogView = createIconDialogView(inflater, null);
        dialog.setContentView(dialogView);

        dialog.setCancelable(cancelable);

        dialogView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                float radius = activity.getResources().getDimension(R.dimen.radiusTop);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight() +
                        (int) radius, radius);
            }
        });
        dialogView.setClipToOutline(true);

        dialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;

            FrameLayout bottomSheet = d.findViewById(
                    com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            Configuration configuration = activity.getResources().getConfiguration();
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                    configuration.screenWidthDp > 450) {
                if (this.dialog.getWindow() != null) {
                    this.dialog.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
                }
                this.dialog.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
            }
        });
    }

    @Override
    protected View createIconDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return super.createIconDialogView(inflater, container);
    }

    public static class Builder extends AbstractDialog.Builder<BottomIconDialog> {

        public Builder(@NonNull Activity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public BottomIconDialog build() {
            return new BottomIconDialog(
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

    static class BottomSheetDialog extends com.google.android.material.bottomsheet.BottomSheetDialog {
        BottomSheetDialog(@NonNull Context context) {
            super(context, R.style.BottomSheetDialogTheme);
        }
    }
}
