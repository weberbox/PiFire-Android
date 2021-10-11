package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.DialogModeStartActionBinding;
import com.weberbox.pifire.interfaces.DashboardCallbackInterface;
import com.weberbox.pifire.interfaces.OnStateChangeListener;
import com.weberbox.pifire.interfaces.OnSwipeTouchListener;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.views.SwipeButton;

public class StartModeActionDialog {

    private DialogModeStartActionBinding mBinding;
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
        mBinding = DialogModeStartActionBinding.inflate(mInflater);

        LinearLayout startButton = mBinding.modeStartButton;
        LinearLayout monitorButton = mBinding.modeMonitorButton;
        LinearLayout stopButton = mBinding.modeStopButton;

        mSwipeButton = mBinding.modeSwipeStartButton;
        mButtonsTable = mBinding.startModeSheetContainer;

        mSwipeButton.setCenterTextStyle(R.style.Text16Aller);

        mHandler = new Handler();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Prefs.getBoolean(mContext.getString(R.string.prefs_grill_swipe_start),
                        mContext.getResources().getBoolean(R.bool.def_grill_swipe_start))) {
                    if (mSwipeButton.getVisibility() == View.INVISIBLE) {
                        startShowDelay();
                        fadeView(mButtonsTable, Constants.FADE_OUT);
                        fadeView(mSwipeButton, Constants.FADE_IN);
                        mModeActionsBottomSheet.setCancelable(false);
                        mModeActionsBottomSheet.setCanceledOnTouchOutside(true);
                    }
                } else {
                    mModeActionsBottomSheet.dismiss();
                    mCallBack.onModeActionClicked(Constants.ACTION_MODE_START);
                }
            }
        });

        mSwipeButton.setOnSwipeTouchListener(new OnSwipeTouchListener() {
            @Override
            public void onSwipeTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        stopShowDelay();
                        break;
                    case MotionEvent.ACTION_UP:
                        startShowDelay();
                        break;
                }
            }
        });

        mSwipeButton.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                if (active) {
                    mModeActionsBottomSheet.dismiss();
                    mCallBack.onModeActionClicked(Constants.ACTION_MODE_START);
                    mSwipeButton.toggleState();
                }
            }
        });

        monitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModeActionsBottomSheet.dismiss();
                mCallBack.onModeActionClicked(Constants.ACTION_MODE_MONITOR);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModeActionsBottomSheet.dismiss();
                mCallBack.onModeActionClicked(Constants.ACTION_MODE_STOP);
            }
        });

        mModeActionsBottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                stopShowDelay();
            }
        });

        mModeActionsBottomSheet.setContentView(mBinding.getRoot());

        mModeActionsBottomSheet.show();

        return mModeActionsBottomSheet;
    }

    private void fadeView(View view, int direction) {
        switch (direction) {
            case Constants.FADE_OUT:
                AnimUtils.fadeView(view, 1.0f, 0.0f, 300);
                view.setVisibility(View.INVISIBLE);
                break;
            case Constants.FADE_IN:
                AnimUtils.fadeView(view, 0.0f, 1.0f, 300);
                view.setVisibility(View.VISIBLE);
                break;
        }
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
        }
    };
}
