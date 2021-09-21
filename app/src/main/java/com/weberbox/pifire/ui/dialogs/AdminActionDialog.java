package com.weberbox.pifire.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.interfaces.AdminCallbackInterface;

public class AdminActionDialog {
    private static String TAG = AdminActionDialog.class.getSimpleName();

    private final BottomSheetDialog mAdminActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final AdminCallbackInterface mCallBack;
    private final int mType;


    public AdminActionDialog(Context context, Fragment fragment, int type) {
        mAdminActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (AdminCallbackInterface) fragment;
        mType = type;
    }

    public BottomSheetDialog showDialog() {
        @SuppressLint("InflateParams")
        View sheetView = mInflater.inflate(R.layout.dialog_admin_action, null);

        LinearLayout actionLeftButton = sheetView.findViewById(R.id.admin_cancel_button);
        LinearLayout actionRightButton = sheetView.findViewById(R.id.admin_positive_button);
        TextView actionText = sheetView.findViewById(R.id.admin_dialog_text);
        TextView rightButtonText = sheetView.findViewById(R.id.admin_positive_text);
        ImageView rightButtonImage = sheetView.findViewById(R.id.admin_positive_image);

        switch (mType) {
            case Constants.ACTION_ADMIN_HISTORY:
                actionText.setText(R.string.history_delete_content);
                break;
            case Constants.ACTION_ADMIN_EVENTS:
                actionText.setText(R.string.settings_admin_delete_events_text);
                break;
            case Constants.ACTION_ADMIN_PELLET_LOG:
                actionText.setText(R.string.settings_admin_delete_pellets_log_text);
                break;
            case Constants.ACTION_ADMIN_PELLET:
                actionText.setText(R.string.settings_admin_delete_pellets_text);
                break;
            case Constants.ACTION_ADMIN_RESET:
                actionText.setText(R.string.settings_admin_factory_reset_text);
                rightButtonImage.setImageResource(R.drawable.ic_setup_finish);
                break;
            case Constants.ACTION_ADMIN_REBOOT:
                actionText.setText(R.string.settings_admin_reboot_text);
                rightButtonImage.setImageResource(R.drawable.ic_setup_finish);
                break;
            case Constants.ACTION_ADMIN_SHUTDOWN:
                actionText.setText(R.string.settings_admin_shutdown_text);
                rightButtonImage.setImageResource(R.drawable.ic_setup_finish);
                break;
        }

        actionLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdminActionsBottomSheet.dismiss();
            }
        });

        actionRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdminActionsBottomSheet.dismiss();
                mCallBack.onDialogPositive(mType);
            }
        });

        mAdminActionsBottomSheet.setContentView(sheetView);

        mAdminActionsBottomSheet.show();

        return mAdminActionsBottomSheet;
    }
}
