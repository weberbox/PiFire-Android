package com.weberbox.pifire.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.DialogBottomButtonIconBinding;
import com.weberbox.pifire.databinding.DialogButtonTextviewBinding;
import com.weberbox.pifire.databinding.DialogMaterialTextviewBinding;
import com.weberbox.pifire.databinding.DialogProgressLinearBinding;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogInterface;
import com.weberbox.pifire.ui.dialogs.interfaces.OnCancelListener;
import com.weberbox.pifire.ui.dialogs.interfaces.OnDismissListener;
import com.weberbox.pifire.ui.dialogs.interfaces.OnShowListener;
import com.weberbox.pifire.ui.dialogs.model.DialogButton;
import com.weberbox.pifire.ui.dialogs.model.DialogMessage;
import com.weberbox.pifire.ui.dialogs.model.DialogSwipeButton;
import com.weberbox.pifire.ui.dialogs.model.DialogTitle;
import com.weberbox.pifire.ui.dialogs.model.TextAlignment;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.TextViewRichDrawable;
import com.weberbox.pifire.ui.views.SwipeButton;

@SuppressWarnings({"unused", "rawtypes"})
public abstract class AbstractDialog implements DialogInterface {

    public static final int BUTTON_POSITIVE = -1;
    public static final int BUTTON_NEUTRAL = -2;
    public static final int BUTTON_NEGATIVE = -3;
    public static final int NO_ICON = -111;
    public static final int NO_ANIMATION = -111;
    public static final int NO_COLOR = -222;

    protected Dialog dialog;
    protected Activity activity;
    protected DialogTitle title;
    protected DialogMessage message;
    protected DialogButton positiveButton;
    protected DialogButton neutralButton;
    protected DialogButton negativeButton;
    protected DialogSwipeButton swipeButton;
    protected int animationResId;
    protected String animationFile;
    protected TextView progressMessage;
    protected LottieAnimationView animationView;
    protected LinearProgressIndicator progressIndicator;
    protected Handler handler;
    protected Runnable runnable;
    protected boolean cancelable;
    protected boolean autoDismiss;

    protected TextAlignment titleTextAlignment;
    protected TextAlignment messageTextAlignment;

    protected OnDismissListener onDismissListener;
    protected OnCancelListener onCancelListener;
    protected OnShowListener onShowListener;

    protected AbstractDialog(@NonNull Activity activity,
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
        this.activity = activity;
        this.title = title;
        this.message = message;
        this.cancelable = cancelable;
        this.autoDismiss = autoDismiss;
        this.positiveButton = positiveButton;
        this.neutralButton = neutralButton;
        this.negativeButton = negativeButton;
        this.swipeButton = swipeButton;
        this.animationResId = animationResId;
        this.animationFile = animationFile;
    }

    @SuppressLint("WrongConstant")
    @SuppressWarnings("SameParameterValue")
    protected View createMaterialTextView(@NonNull LayoutInflater inflater,
                                      @Nullable ViewGroup container) {
        DialogMaterialTextviewBinding binding = DialogMaterialTextviewBinding.inflate(inflater);

        TextView titleView = binding.materialTitle;
        TextView messageView = binding.materialMessage;
        MaterialButton positiveButtonView = binding.materialPositiveBtn;
        MaterialButton negativeButtonView = binding.materialNegativeBtn;
        LinearLayout buttonsContainer = binding.materialButtonsContainer;
        View spacer = binding.materialSpacing;

        if (title != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title.getText());
            titleView.setTextAlignment(title.getTextAlignment().getAlignment());
        } else {
            titleView.setVisibility(View.GONE);
        }

        if (message != null) {
            messageView.setVisibility(View.VISIBLE);

            messageView.setText(message.getText());
            messageView.setTextAlignment(message.getTextAlignment().getAlignment());
        } else {
            messageView.setVisibility(View.GONE);
        }

        if (positiveButton != null) {
            positiveButtonView.setVisibility(View.VISIBLE);
            positiveButtonView.setText(positiveButton.getTitle());
            if (positiveButton.getIcon() != NO_ICON) {
                positiveButtonView.setIcon(ContextCompat.getDrawable(activity,
                        positiveButton.getIcon()));
            }

            positiveButtonView.setOnClickListener(view -> {
                        positiveButton.getOnClickListener().onClick(AbstractDialog.this,
                                BUTTON_POSITIVE);
                        if (autoDismiss) dialog.dismiss();
                    }

            );
        } else {
            positiveButtonView.setVisibility(View.INVISIBLE);
        }

        if (negativeButton != null) {
            negativeButtonView.setVisibility(View.VISIBLE);
            negativeButtonView.setText(negativeButton.getTitle());
            if (negativeButton.getIcon() != NO_ICON) {
                negativeButtonView.setIcon(ContextCompat.getDrawable(activity,
                        negativeButton.getIcon()));
            }

            negativeButtonView.setOnClickListener(view -> {
                        negativeButton.getOnClickListener().onClick(AbstractDialog.this,
                                BUTTON_NEGATIVE);
                        if (autoDismiss) dialog.dismiss();
                    }
            );
        } else {
            negativeButtonView.setVisibility(View.INVISIBLE);
        }

        if (positiveButton == null && negativeButton == null) {
            buttonsContainer.setVisibility(View.GONE);
            spacer.setVisibility(View.VISIBLE);
        }

        binding.getRoot().setBackgroundResource(R.drawable.bg_alert_dialog);

        titleView.setTextColor(ContextCompat.getColor(activity,
                R.color.material_dialog_title_text_color));

        messageView.setTextColor(ContextCompat.getColor(activity,
                R.color.material_dialog_message_text_color));

        ColorStateList positiveButtonTint = ContextCompat.getColorStateList(
                activity.getApplicationContext(),
                R.color.dialog_positive_button_text_color);
        positiveButtonView.setTextColor(positiveButtonTint);
        positiveButtonView.setIconTint(positiveButtonTint);

        ColorStateList negativeButtonTint = ContextCompat.getColorStateList(
                activity.getApplicationContext(),
                R.color.dialog_negative_button_text_color);
        negativeButtonView.setIconTint(negativeButtonTint);
        negativeButtonView.setTextColor(negativeButtonTint);

        return binding.getRoot();
    }

    @SuppressLint("WrongConstant")
    @SuppressWarnings("SameParameterValue")
    protected View createMaterialListView(@NonNull LayoutInflater inflater,
                                          @Nullable ViewGroup container) {
        DialogMaterialTextviewBinding binding = DialogMaterialTextviewBinding.inflate(inflater);

        TextView titleView = binding.materialTitle;
        TextView messageView = binding.materialMessage;
        MaterialButton positiveButtonView = binding.materialPositiveBtn;
        MaterialButton negativeButtonView = binding.materialNegativeBtn;

        if (title != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title.getText());
            titleView.setTextAlignment(title.getTextAlignment().getAlignment());
        } else {
            titleView.setVisibility(View.GONE);
        }

        if (message != null) {
            messageView.setVisibility(View.VISIBLE);

            messageView.setText(message.getText());
            messageView.setTextAlignment(message.getTextAlignment().getAlignment());
        } else {
            messageView.setVisibility(View.GONE);
        }

        if (positiveButton != null) {
            positiveButtonView.setVisibility(View.VISIBLE);
            positiveButtonView.setText(positiveButton.getTitle());
            if (positiveButton.getIcon() != NO_ICON) {
                positiveButtonView.setIcon(ContextCompat.getDrawable(activity,
                        positiveButton.getIcon()));
            }

            positiveButtonView.setOnClickListener(view -> {
                        positiveButton.getOnClickListener().onClick(AbstractDialog.this,
                                BUTTON_POSITIVE);
                        if (autoDismiss) dialog.dismiss();
                    }

            );
        } else {
            positiveButtonView.setVisibility(View.INVISIBLE);
        }

        if (negativeButton != null) {
            negativeButtonView.setVisibility(View.VISIBLE);
            negativeButtonView.setText(negativeButton.getTitle());
            if (negativeButton.getIcon() != NO_ICON) {
                negativeButtonView.setIcon(ContextCompat.getDrawable(activity,
                        negativeButton.getIcon()));
            }

            negativeButtonView.setOnClickListener(view -> {
                        negativeButton.getOnClickListener().onClick(AbstractDialog.this,
                                BUTTON_NEGATIVE);
                        if (autoDismiss) dialog.dismiss();
                    }
            );
        } else {
            negativeButtonView.setVisibility(View.INVISIBLE);
        }

        binding.getRoot().setBackgroundResource(R.drawable.bg_alert_dialog);

        titleView.setTextColor(ContextCompat.getColor(activity,
                R.color.material_dialog_title_text_color));

        messageView.setTextColor(ContextCompat.getColor(activity,
                R.color.material_dialog_message_text_color));

        ColorStateList positiveButtonTint = ContextCompat.getColorStateList(
                activity.getApplicationContext(),
                R.color.dialog_positive_button_text_color);
        positiveButtonView.setTextColor(positiveButtonTint);
        positiveButtonView.setIconTint(positiveButtonTint);

        ColorStateList negativeButtonTint = ContextCompat.getColorStateList(
                activity.getApplicationContext(),
                R.color.dialog_negative_button_text_color);
        negativeButtonView.setIconTint(negativeButtonTint);
        negativeButtonView.setTextColor(negativeButtonTint);

        return binding.getRoot();
    }

    @SuppressLint("WrongConstant")
    protected View createButtonView(@NonNull LayoutInflater inflater,
                                    @Nullable ViewGroup container) {
        DialogButtonTextviewBinding binding = DialogButtonTextviewBinding.inflate(inflater);

        TextView titleView = binding.textViewTitle;
        TextView messageView = binding.textViewMessage;
        MaterialButton positiveButtonView = binding.buttonPositive;
        MaterialButton negativeButtonView = binding.buttonNegative;
        animationView = binding.animationView;

        if (title != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title.getText());
            titleView.setTextAlignment(title.getTextAlignment().getAlignment());
        } else {
            titleView.setVisibility(View.GONE);
        }

        if (message != null) {
            messageView.setVisibility(View.VISIBLE);

            messageView.setText(message.getText());
            messageView.setTextAlignment(message.getTextAlignment().getAlignment());
        } else {
            messageView.setVisibility(View.GONE);
        }

        if (positiveButton != null) {
            positiveButtonView.setVisibility(View.VISIBLE);
            positiveButtonView.setText(positiveButton.getTitle());
            if (positiveButton.getIcon() != NO_ICON) {
                positiveButtonView.setIcon(ContextCompat.getDrawable(activity,
                        positiveButton.getIcon()));
            }
            if (positiveButton.getBackgroundColor() != NO_COLOR) {
                positiveButtonView.setBackgroundTintList(ContextCompat.getColorStateList(
                        activity.getApplicationContext(),
                        positiveButton.getBackgroundColor()));
            } else {
                positiveButtonView.setBackgroundTintList(ContextCompat.getColorStateList(
                        activity.getApplicationContext(),
                        R.color.dialog_positive_button_color));
            }

            positiveButtonView.setOnClickListener(view -> {
                        positiveButton.getOnClickListener().onClick(AbstractDialog.this,
                                BUTTON_POSITIVE);
                        if (autoDismiss) dialog.dismiss();
                    }

            );
        } else {
            positiveButtonView.setVisibility(View.INVISIBLE);
        }

        if (negativeButton != null) {
            negativeButtonView.setVisibility(View.VISIBLE);
            negativeButtonView.setText(negativeButton.getTitle());
            if (negativeButton.getIcon() != NO_ICON) {
                negativeButtonView.setIcon(ContextCompat.getDrawable(activity,
                        negativeButton.getIcon()));
            }

            negativeButtonView.setOnClickListener(view -> {
                        negativeButton.getOnClickListener().onClick(AbstractDialog.this,
                                BUTTON_NEGATIVE);
                        if (autoDismiss) dialog.dismiss();
                    }
            );
        } else {
            negativeButtonView.setVisibility(View.INVISIBLE);
        }

        int orientation = activity.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            animationView.setVisibility(View.GONE);
        } else {
            if (animationResId != NO_ANIMATION) {
                animationView.setVisibility(View.VISIBLE);
                animationView.setAnimation(animationResId);
                animationView.playAnimation();

            } else if (animationFile != null) {
                animationView.setVisibility(View.VISIBLE);
                animationView.setAnimation(animationFile);
                animationView.playAnimation();

            } else {
                animationView.setVisibility(View.GONE);
            }
        }

        binding.getRoot().setBackgroundColor(ContextCompat.getColor(activity,
                R.color.material_dialog_background));

        titleView.setTextColor(ContextCompat.getColor(activity,
                R.color.material_dialog_title_text_color));

        messageView.setTextColor(ContextCompat.getColor(activity,
                R.color.material_dialog_message_text_color));

        ColorStateList positiveButtonTint = ContextCompat.getColorStateList(
                activity.getApplicationContext(),
                R.color.dialog_positive_button_text_color);
        positiveButtonView.setTextColor(positiveButtonTint);
        positiveButtonView.setIconTint(positiveButtonTint);

        ColorStateList negativeButtonTint = ContextCompat.getColorStateList(
                activity.getApplicationContext(),
                R.color.dialog_negative_button_text_color);
        negativeButtonView.setIconTint(negativeButtonTint);
        negativeButtonView.setTextColor(negativeButtonTint);

        if (negativeButtonTint != null) {
            negativeButtonView.setRippleColor(negativeButtonTint.withAlpha(75));
        }

        return binding.getRoot();
    }

    @SuppressLint("WrongConstant")
    protected View createIconDialogView(@NonNull LayoutInflater inflater,
                                        @Nullable ViewGroup container) {
        DialogBottomButtonIconBinding binding = DialogBottomButtonIconBinding.inflate(inflater);

        LinearLayout buttonsView = binding.bsdBtnContainer;
        TextViewRichDrawable positiveButtonView = binding.bsdPositiveBtn;
        TextViewRichDrawable neutralButtonView = binding.bsdNeutralBtn;
        TextViewRichDrawable negativeButtonView = binding.bsdNegativeBtn;
        SwipeButton swipeButtonView = binding.bsdSwipeBtn;

        if (positiveButton != null) {
            positiveButtonView.setVisibility(View.VISIBLE);
            positiveButtonView.setText(positiveButton.getTitle());
            if (positiveButton.getIcon() != NO_ICON) {
                positiveButtonView.setDrawableTopVectorId(positiveButton.getIcon());
            }

            positiveButtonView.setOnClickListener(view -> {
                        positiveButton.getOnClickListener().onClick(AbstractDialog.this,
                                BUTTON_POSITIVE);
                        if (autoDismiss) dialog.dismiss();
                    }
            );
        } else {
            positiveButtonView.setVisibility(View.GONE);
        }

        if (neutralButton != null) {
            neutralButtonView.setVisibility(View.VISIBLE);
            neutralButtonView.setText(neutralButton.getTitle());
            if (neutralButton.getIcon() != NO_ICON) {
                neutralButtonView.setDrawableTopVectorId(neutralButton.getIcon());
            }

            neutralButtonView.setOnClickListener(view -> {
                        neutralButton.getOnClickListener().onClick(AbstractDialog.this,
                                BUTTON_NEUTRAL);
                        if (autoDismiss) dialog.dismiss();
                    }

            );
        } else {
            neutralButtonView.setVisibility(View.GONE);
        }

        if (negativeButton != null) {
            negativeButtonView.setVisibility(View.VISIBLE);
            negativeButtonView.setText(negativeButton.getTitle());
            if (negativeButton.getIcon() != NO_ICON) {
                negativeButtonView.setDrawableTopVectorId(negativeButton.getIcon());
            }

            if (swipeButton != null && swipeButton.getEnabled()) {
                handler = new Handler(Looper.getMainLooper());
                runnable = () -> {
                    AnimUtils.fadeAnimation(swipeButtonView, 300, Constants.FADE_OUT);
                    AnimUtils.fadeAnimation(buttonsView, 300, Constants.FADE_IN);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);
                };

                swipeButtonView.setText(swipeButton.getText());

                negativeButtonView.setOnClickListener(view -> {
                    if (swipeButtonView.getVisibility() == View.INVISIBLE) {
                        handler.postDelayed(runnable, 3000);
                        AnimUtils.fadeViewInvisible(buttonsView, 300, Constants.FADE_OUT);
                        AnimUtils.fadeViewInvisible(swipeButtonView, 300, Constants.FADE_IN);
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(true);
                    }
                });

                swipeButtonView.setOnSwipeTouchListener((view, motionEvent) -> {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN -> handler.removeCallbacks(runnable);
                        case MotionEvent.ACTION_UP -> handler.postDelayed(runnable, 3000);
                    }
                });

                swipeButtonView.setOnStateChangeListener(active -> {
                    swipeButton.getOnStateChangeListener().onStateChange(
                            AbstractDialog.this, active);
                    if (autoDismiss) dialog.dismiss();
                });

                dialog.setOnDismissListener(dialog -> handler.removeCallbacks(runnable));
            } else {
                negativeButtonView.setOnClickListener(view -> {
                            negativeButton.getOnClickListener().onClick(AbstractDialog.this,
                                    BUTTON_NEGATIVE);
                            if (autoDismiss) dialog.dismiss();
                        }
                );
            }
        } else {
            negativeButtonView.setVisibility(View.GONE);
        }

        binding.getRoot().setBackgroundColor(ContextCompat.getColor(activity,
                R.color.material_dialog_background));

        return binding.getRoot();
    }

    @SuppressLint("WrongConstant")
    @SuppressWarnings("SameParameterValue")
    protected View createProgressDialogView(@NonNull LayoutInflater inflater,
                                            @Nullable ViewGroup container) {
        DialogProgressLinearBinding binding = DialogProgressLinearBinding.inflate(inflater);

        progressMessage = binding.dialogProgressMessage;
        progressIndicator = binding.dialogProgressLinear;
        TextView titleView = binding.dialogProgressTitle;
        MaterialButton positiveButtonView = binding.progressPositiveBtn;
        MaterialButton negativeButtonView = binding.progressNegativeBtn;

        if (title != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title.getText());
            titleView.setTextAlignment(title.getTextAlignment().getAlignment());
        } else {
            titleView.setVisibility(View.GONE);
        }

        if (message != null) {
            progressMessage.setVisibility(View.VISIBLE);

            progressMessage.setText(message.getText());
            progressMessage.setTextAlignment(message.getTextAlignment().getAlignment());
        } else {
            progressMessage.setVisibility(View.GONE);
        }

        if (positiveButton != null) {
            positiveButtonView.setVisibility(View.VISIBLE);
            positiveButtonView.setText(positiveButton.getTitle());
            if (positiveButton.getIcon() != NO_ICON) {
                positiveButtonView.setIcon(ContextCompat.getDrawable(activity,
                        positiveButton.getIcon()));
            }
            if (positiveButton.getBackgroundColor() != NO_COLOR) {
                positiveButtonView.setBackgroundTintList(ContextCompat.getColorStateList(
                        activity.getApplicationContext(),
                        positiveButton.getBackgroundColor()));
            } else {
                positiveButtonView.setBackgroundTintList(ContextCompat.getColorStateList(
                        activity.getApplicationContext(),
                        R.color.dialog_positive_button_color));
            }

            positiveButtonView.setOnClickListener(view ->
                    positiveButton.getOnClickListener().onClick(AbstractDialog.this,
                            BUTTON_POSITIVE)
            );
        } else {
            positiveButtonView.setVisibility(View.GONE);
        }

        if (negativeButton != null) {
            negativeButtonView.setVisibility(View.VISIBLE);
            negativeButtonView.setText(negativeButton.getTitle());
            if (negativeButton.getIcon() != NO_ICON) {
                negativeButtonView.setIcon(ContextCompat.getDrawable(activity,
                        negativeButton.getIcon()));
            }

            negativeButtonView.setOnClickListener(view ->
                    negativeButton.getOnClickListener().onClick(AbstractDialog.this,
                            BUTTON_NEGATIVE)
            );
        } else {
            negativeButtonView.setVisibility(View.GONE);
        }

        binding.getRoot().setBackgroundResource(R.drawable.bg_alert_dialog);

        progressMessage.setTextColor(ContextCompat.getColor(activity,
                R.color.material_dialog_message_text_color));

        ColorStateList positiveButtonTint = ContextCompat.getColorStateList(
                activity.getApplicationContext(),
                R.color.dialog_positive_button_text_color);
        positiveButtonView.setTextColor(positiveButtonTint);
        positiveButtonView.setIconTint(positiveButtonTint);

        ColorStateList negativeButtonTint = ContextCompat.getColorStateList(
                activity.getApplicationContext(),
                R.color.dialog_negative_button_text_color);
        negativeButtonView.setIconTint(negativeButtonTint);
        negativeButtonView.setTextColor(negativeButtonTint);

        if (negativeButtonTint != null) {
            negativeButtonView.setRippleColor(negativeButtonTint.withAlpha(75));
        }

        return binding.getRoot();
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        } else {
            throwNullDialog();
        }
    }

    @Override
    public void cancel() {
        if (dialog != null) {
            dialog.cancel();
        } else {
            throwNullDialog();
        }
    }

    @Override
    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        } else {
            throwNullDialog();
        }
    }

    public void setOnShowListener(@NonNull final OnShowListener onShowListener) {
        this.onShowListener = onShowListener;

        dialog.setOnShowListener(dialogInterface -> showCallback());
    }

    public void setOnCancelListener(@NonNull final OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;

        dialog.setOnCancelListener(dialogInterface -> cancelCallback());
    }

    public void setOnDismissListener(@NonNull final OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;

        dialog.setOnDismissListener(dialogInterface -> dismissCallback());
    }

    public LottieAnimationView getAnimationView() {
        return animationView;
    }

    public LinearProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public TextView getProgressMessage() {
        return progressMessage;
    }

    private void showCallback() {
        if (onShowListener != null) {
            onShowListener.onShow(this);
        }
    }

    private void dismissCallback() {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
    }

    private void cancelCallback() {
        if (onCancelListener != null) {
            onCancelListener.onCancel(this);
        }
    }

    private void throwNullDialog() {
        throw new NullPointerException("Called method on null Dialog. Create dialog using " +
                "`Builder` before calling on Dialog");
    }

    public interface OnClickListener {
        void onClick(DialogInterface dialogInterface, int which);
    }

    public interface OnStateChangeListener {
        void onStateChange(DialogInterface dialogInterface, boolean active);
    }

    /**
     * Builder for {@link AbstractDialog}.
     */
    public static abstract class Builder<D extends AbstractDialog> {
        protected final Activity activity;
        protected DialogTitle title;
        protected DialogMessage message;
        protected boolean isCancelable = true;
        protected boolean autoDismiss;
        protected DialogButton positiveButton;
        protected DialogButton neutralButton;
        protected DialogButton negativeButton;
        protected DialogSwipeButton swipeButton;
        protected int animationResId = NO_ANIMATION;
        protected String animationFile;

        /**
         * @param activity where Material Dialog is to be built.
         */
        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        /**
         * @param title Sets the Title of Material Dialog with the default alignment as center.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setTitle(@NonNull String title) {
            return setTitle(title, TextAlignment.CENTER);
        }

        /**
         * @param title     Sets the Title of Material Dialog.
         * @param alignment Sets the Alignment for the title.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setTitle(@NonNull String title, @NonNull TextAlignment alignment) {
            this.title = new DialogTitle(title, alignment);
            return this;
        }

        /**
         * @param message Sets the plain text Message of Material Dialog with the default
         *                alignment as center.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setMessage(@NonNull String message) {
            return setMessage(message, TextAlignment.CENTER);
        }

        /**
         * @param message   Sets the plain text Message of Material Dialog.
         * @param alignment Sets the Alignment for the message.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setMessage(@NonNull String message, @NonNull TextAlignment alignment) {
            this.message = DialogMessage.text(message, alignment);
            return this;
        }

        /**
         * @param message Sets the spanned text Message of Material Dialog with the default
         *                alignment as center.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setMessage(@NonNull Spanned message) {
            return setMessage(message, TextAlignment.CENTER);
        }

        /**
         * @param message   Sets the spanned text Message of Material Dialog.
         * @param alignment Sets the Alignment for the message.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setMessage(@NonNull Spanned message, @NonNull TextAlignment alignment) {
            this.message = DialogMessage.spanned(message, alignment);
            return this;
        }

        /**
         * @param isCancelable Sets cancelable property of Material Dialog.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        /**
         * @param autoDismiss Sets auto dismiss of dialog when button is pressed
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setAutoDismiss(boolean autoDismiss) {
            this.autoDismiss = autoDismiss;
            return this;
        }

        /**
         * Sets the Positive Button to Material Dialog without icon
         *
         * @param name            sets the name/label of button.
         * @param onClickListener interface for callback event on click of button.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setPositiveButton(@NonNull String name,
                                            @NonNull OnClickListener onClickListener) {
            return setPositiveButton(name, NO_ICON, NO_COLOR, onClickListener);
        }

        /**
         * Sets the Positive Button to Material Dialog with icon
         *
         * @param name            sets the name/label of button.
         * @param icon            sets the resource icon for button.
         * @param onClickListener interface for callback event on click of button.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setPositiveButton(@NonNull String name, int icon,
                                            @NonNull OnClickListener onClickListener) {
            return setPositiveButton(name, icon, NO_COLOR, onClickListener);
        }

        /**
         * Sets the Positive Button to Material Dialog with background color
         *
         * @param name            sets the name/label of button.
         * @param colorStateList  sets the button background color.
         * @param onClickListener interface for callback event on click of button.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setPositiveButtonWithColor(@NonNull String name, int colorStateList,
                                                     @NonNull OnClickListener onClickListener) {
            return setPositiveButton(name, NO_ICON, colorStateList, onClickListener);
        }

        /**
         * Sets the Positive Button to Material Dialog with icon and background color
         *
         * @param name            sets the name/label of button.
         * @param icon            sets the resource icon for button.
         * @param colorStateList  sets the button background color.
         * @param onClickListener interface for callback event on click of button.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setPositiveButton(@NonNull String name, int icon, int colorStateList,
                                            @NonNull OnClickListener onClickListener) {
            positiveButton = new DialogButton(name, icon, colorStateList, onClickListener);
            return this;
        }

        /**
         * Sets the Neutral Button to Material Dialog without icon.
         *
         * @param name            sets the name/label of button.
         * @param onClickListener interface for callback event on click of button.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setNeutralButton(@NonNull String name,
                                           @NonNull OnClickListener onClickListener) {
            return setNeutralButton(name, NO_ICON, onClickListener);
        }

        /**
         * Sets the Neutral Button to Material Dialog with icon
         *
         * @param name            sets the name/label of button.
         * @param icon            sets the resource icon for button.
         * @param onClickListener interface for callback event on click of button.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setNeutralButton(@NonNull String name, int icon,
                                           @NonNull OnClickListener onClickListener) {
            neutralButton = new DialogButton(name, icon, NO_COLOR, onClickListener);
            return this;
        }

        /**
         * Sets the Negative Button to Material Dialog without icon.
         *
         * @param name            sets the name/label of button.
         * @param onClickListener interface for callback event on click of button.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setNegativeButton(@NonNull String name,
                                            @NonNull OnClickListener onClickListener) {
            return setNegativeButton(name, NO_ICON, onClickListener);
        }

        /**
         * Sets the Negative Button to Material Dialog with icon
         *
         * @param name            sets the name/label of button.
         * @param icon            sets the resource icon for button.
         * @param onClickListener interface for callback event on click of button.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setNegativeButton(@NonNull String name, int icon,
                                            @NonNull OnClickListener onClickListener) {
            negativeButton = new DialogButton(name, icon, NO_COLOR, onClickListener);
            return this;
        }

        /**
         * Sets the Swipe Button callback
         *
         * @param text                  sets the text in the swipe view.
         * @param enabled               sets the swipe button enabled.
         * @param onStateChangeListener interface for callback event on swipe of button.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setSwipeButton(@NonNull String text, boolean enabled,
                                         @NonNull OnStateChangeListener onStateChangeListener) {
            swipeButton = new DialogSwipeButton(text, enabled, onStateChangeListener);
            return this;
        }

        /**
         * It sets the resource json to the {@link LottieAnimationView}.
         *
         * @param animationResId sets the resource to {@link LottieAnimationView}.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setAnimation(@RawRes int animationResId) {
            this.animationResId = animationResId;
            return this;
        }

        /**
         * It sets the json file to the {@link LottieAnimationView} from assets.
         *
         * @param fileName sets the file from assets to {@link LottieAnimationView}.
         * @return this, for chaining.
         */
        @NonNull
        public Builder<D> setAnimation(@NonNull String fileName) {
            this.animationFile = fileName;
            return this;
        }

        /**
         * Builds the dialog.
         */
        @NonNull
        public abstract D build();
    }
}
