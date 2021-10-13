package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogProfilePickerBinding;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;
import com.weberbox.pifire.model.PelletProfileModel;
import com.weberbox.pifire.recycler.adapter.PelletProfileAdapter;
import com.weberbox.pifire.recycler.manager.PickerLayoutManager;
import com.weberbox.pifire.ui.utils.ViewUtils;

import java.util.List;

public class PelletPickerDialog {

    private final BottomSheetDialog mPelletPickerBottomSheet;
    private final LayoutInflater mInflater;
    private final PelletsCallbackInterface mCallBack;
    private final List<PelletProfileModel> mPelletsList;
    private final Context mContext;
    private RecyclerView mProfileList;
    private String mCurrentProfile;
    private String mCurrentProfileId;

    public PelletPickerDialog(Context context, Fragment fragment,
                              List<PelletProfileModel> pelletList, String currentProfile) {
        mPelletPickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (PelletsCallbackInterface) fragment;
        mContext = context;
        mPelletsList = pelletList;
        mCurrentProfileId = currentProfile;
    }

    public BottomSheetDialog showDialog() {
        DialogProfilePickerBinding binding = DialogProfilePickerBinding.inflate(mInflater);

        Button confirmButton = binding.setProfileLoad;
        Button cancelButton = binding.setProfileCancel;

        PickerLayoutManager pelletPickerLayoutManager = new PickerLayoutManager(mContext,
                PickerLayoutManager.VERTICAL, false);
        pelletPickerLayoutManager.setChangeAlpha(true);
        pelletPickerLayoutManager.setScaleDownBy(0.99f);
        pelletPickerLayoutManager.setScaleDownDistance(1.2f);

        mProfileList = binding.profileList;

        SnapHelper profileSnapHelper = new LinearSnapHelper();
        profileSnapHelper.attachToRecyclerView(mProfileList);

        PelletProfileAdapter profileAdapter = new PelletProfileAdapter(mPelletsList);

        mProfileList.setLayoutManager(pelletPickerLayoutManager);
        mProfileList.setAdapter(profileAdapter);

        mCurrentProfileId = mPelletsList.get(0).getId();

        pelletPickerLayoutManager.setOnScrollStopListener(
                view -> {
                    LinearLayout parent = view.findViewById(R.id.profile_item_container);
                    RelativeLayout parent_two = parent.findViewById(R.id.profile_item_container_two);
                    TextView text = parent_two.findViewById(R.id.profile_item_text_view);
                    TextView id = parent_two.findViewById(R.id.profile_item_id);
                    mCurrentProfile = text.getText().toString();
                    mCurrentProfileId = id.getText().toString();
                });

        confirmButton.setOnClickListener(v -> {
            mPelletPickerBottomSheet.dismiss();
            mCallBack.onProfileSelected(mCurrentProfile, mCurrentProfileId);
        });

        cancelButton.setOnClickListener(v -> mPelletPickerBottomSheet.dismiss());

        mPelletPickerBottomSheet.setContentView(binding.getRoot());

        mPelletPickerBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        mPelletPickerBottomSheet.show();

        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            mPelletPickerBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return mPelletPickerBottomSheet;
    }

    private void setDefaultProfile(int position, boolean smooth){
        if (smooth) {
            mProfileList.smoothScrollToPosition(position);
        } else {
            mProfileList.scrollToPosition(position);
        }
    }
}
