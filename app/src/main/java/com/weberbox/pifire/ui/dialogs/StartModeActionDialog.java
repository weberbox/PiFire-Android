package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.DialogModeStartActionBinding;
import com.weberbox.pifire.interfaces.DashboardCallbackInterface;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.ui.views.SwipeButton;

public class StartModeActionDialog {

    private final BottomSheetDialog mModeActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final DashboardCallbackInterface mCallBack;
    private final Context mContext;

    private SwipeButton mSwipeButton;
    private TableLayout mButtonsTable;
    private Handler mHandler;


    public StartModeActionDialog(Context context, Fragment fragment) {
        mModeActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mCallBack = (DashboardCallbackInterface) fragment;
    }

    public BottomSheetDialog showDialog() {
        DialogModeStartActionBinding binding = DialogModeStartActionBinding.inflate(mInflater);

        LinearLayout startButton = binding.modeStartButton;
        LinearLayout monitorButton = binding.modeMonitorButton;
        LinearLayout stopButton = binding.modeStopButton;

        mSwipeButton = binding.modeSwipeStartButton;
        mButtonsTable = binding.startModeSheetContainer;

        mSwipeButton.setCenterTextStyle(R.style.Text16Aller);

        mHandler = new Handler();

        startButton.setOnClickListener(v -> {
            if (Prefs.getBoolean(mContext.getString(R.string.prefs_grill_swipe_start),
                    mContext.getResources().getBoolean(R.bool.def_grill_swipe_start))) {
                if (mSwipeButton.getVisibility() == View.INVISIBLE) {
                    startShowDelay();
                    AnimUtils.fadeView(mButtonsTable, 300, Constants.FADE_OUT);
                    AnimUtils.fadeView(mSwipeButton, 300, Constants.FADE_IN);
                    mModeActionsBottomSheet.setCancelable(false);
                    mModeActionsBottomSheet.setCanceledOnTouchOutside(true);
                }
            } else {
                mModeActionsBottomSheet.dismiss();
                mCallBack.onModeActionClicked(Constants.ACTION_MODE_START);
            }
        });

        mSwipeButton.setOnSwipeTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    stopShowDelay();
                    break;
                case MotionEvent.ACTION_UP:
                    startShowDelay();
                    break;
            }
        });

        mSwipeButton.setOnStateChangeListener(active -> {
            if (active) {
                mModeActionsBottomSheet.dismiss();
                mCallBack.onModeActionClicked(Constants.ACTION_MODE_START);
                mSwipeButton.toggleState();
            }
        });

        monitorButton.setOnClickListener(v -> {
            mModeActionsBottomSheet.dismiss();
            mCallBack.onModeActionClicked(Constants.ACTION_MODE_MONITOR);
        });

        stopButton.setOnClickListener(v -> {
            mModeActionsBottomSheet.dismiss();
            mCallBack.onModeActionClicked(Constants.ACTION_MODE_STOP);
        });

        mModeActionsBottomSheet.setOnDismissListener(dialogInterface -> stopShowDelay());

        mModeActionsBottomSheet.setContentView(binding.getRoot());

        mModeActionsBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            if (ViewUtils.isTablet(mContext)) {
                bottomSheetBehavior.setDraggable(false);
            }
        });

        mModeActionsBottomSheet.show();

        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            mModeActionsBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return mModeActionsBottomSheet;
    }

    private void stopShowDelay() {
        mHandler.removeCallbacks(runnable);
    }

    private void startShowDelay() {
        mHandler.postDelayed(runnable, 3000);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            AnimUtils.fadeAnimation(mSwipeButton, 300, Constants.FADE_OUT);
            AnimUtils.fadeAnimation(mButtonsTable, 300, Constants.FADE_IN);
            mModeActionsBottomSheet.setCancelable(true);
            mModeActionsBottomSheet.setCanceledOnTouchOutside(true);
        }
    };
}
