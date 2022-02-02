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
import com.weberbox.pifire.databinding.DialogScrollPickerBinding;
import com.weberbox.pifire.interfaces.PelletsProfileCallback;
import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;
import com.weberbox.pifire.recycler.adapter.PelletProfileAdapter;
import com.weberbox.pifire.recycler.manager.PickerLayoutManager;
import com.weberbox.pifire.ui.utils.ViewUtils;

import java.util.List;

public class ProfilePickerDialog {

    private final BottomSheetDialog pickerBottomSheet;
    private final LayoutInflater inflater;
    private final PelletsProfileCallback callBack;
    private final List<PelletProfileModel> pelletsList;
    private final Context context;
    private RecyclerView profileList;
    private String currentProfile;
    private String currentProfileId;

    public ProfilePickerDialog(Context context, Fragment fragment,
                               List<PelletProfileModel> pelletList, String currentProfile) {
        pickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        inflater = LayoutInflater.from(context);
        callBack = (PelletsProfileCallback) fragment;
        this.context = context;
        pelletsList = pelletList;
        currentProfileId = currentProfile;
    }

    public BottomSheetDialog showDialog() {
        DialogScrollPickerBinding binding = DialogScrollPickerBinding.inflate(inflater);

        Button confirmButton = binding.setProfileLoad;

        PickerLayoutManager pelletPickerLayoutManager = new PickerLayoutManager(context,
                PickerLayoutManager.VERTICAL, false);
        pelletPickerLayoutManager.setChangeAlpha(true);
        pelletPickerLayoutManager.setScaleDownBy(0.99f);
        pelletPickerLayoutManager.setScaleDownDistance(1.2f);

        profileList = binding.profileList;

        SnapHelper profileSnapHelper = new LinearSnapHelper();
        profileSnapHelper.attachToRecyclerView(profileList);

        PelletProfileAdapter profileAdapter = new PelletProfileAdapter(pelletsList);

        profileList.setLayoutManager(pelletPickerLayoutManager);
        profileList.setAdapter(profileAdapter);

        currentProfileId = pelletsList.get(0).getId();

        pelletPickerLayoutManager.setOnScrollStopListener(
                view -> {
                    LinearLayout parent = view.findViewById(R.id.profile_item_container);
                    RelativeLayout parent_two = parent.findViewById(R.id.profile_item_container_two);
                    TextView text = parent_two.findViewById(R.id.profile_item_text_view);
                    TextView id = parent_two.findViewById(R.id.profile_item_id);
                    currentProfile = text.getText().toString();
                    currentProfileId = id.getText().toString();
                });

        confirmButton.setOnClickListener(v -> {
            pickerBottomSheet.dismiss();
            callBack.onProfileSelected(currentProfile, currentProfileId);
        });

        pickerBottomSheet.setContentView(binding.getRoot());

        pickerBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        pickerBottomSheet.show();

        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            pickerBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return pickerBottomSheet;
    }

    @SuppressWarnings("unused")
    private void setDefaultProfile(int position, boolean smooth){
        if (smooth) {
            profileList.smoothScrollToPosition(position);
        } else {
            profileList.scrollToPosition(position);
        }
    }
}
