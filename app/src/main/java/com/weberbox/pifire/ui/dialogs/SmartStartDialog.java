package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogSmartStartBinding;
import com.weberbox.pifire.model.local.SmartStartModel;
import com.weberbox.pifire.model.remote.SettingsDataModel;
import com.weberbox.pifire.recycler.adapter.SmartStartAdapter;
import com.weberbox.pifire.ui.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmartStartDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final Context context;


    public SmartStartDialog(Context context) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public BottomSheetDialog showDialog(){
        DialogSmartStartBinding binding = DialogSmartStartBinding.inflate(inflater);

        List<SmartStartModel> smartStartList = new ArrayList<>();

        RecyclerView recycler = binding.smartStartRecycler;

        String units = Prefs.getString(context.getString(R.string.prefs_grill_units));

        List<Integer> rangeList = new Gson().fromJson(
                Prefs.getString(context.getString(R.string.prefs_smart_start_temp_range)),
                new TypeToken<List<Integer>>() {
                }.getType());

        List<SettingsDataModel.Profile> profileList = new Gson().fromJson(
                Prefs.getString(context.getString(R.string.prefs_smart_start_profiles)),
                new TypeToken<List<SettingsDataModel.Profile>>() {
                }.getType());

        if (rangeList.size() > 0 && profileList.size() > 0) {

            int rangeSize = rangeList.size();
            Integer min = Collections.min(rangeList);
            Integer max = Collections.max(rangeList);

            String minTemp = "< " + min + " " + units;
            String maxTemp = "> " + max + " " + units;

            smartStartList.add(new SmartStartModel(minTemp,
                    profileList.get(0).getStartUpTime(),
                    profileList.get(0).getAugerOnTime(),
                    profileList.get(0).getPMode()));

            for (int i = 1; i < rangeSize; i++) {
                String temp = rangeList.get(i - 1) + "-" + rangeList.get(i) + " " + units;
                smartStartList.add(new SmartStartModel(temp,
                        profileList.get(i).getStartUpTime(),
                        profileList.get(i).getAugerOnTime(),
                        profileList.get(i).getPMode()));
            }

            smartStartList.add(new SmartStartModel(maxTemp,
                    profileList.get(rangeSize).getStartUpTime(),
                    profileList.get(rangeSize).getAugerOnTime(),
                    profileList.get(rangeSize).getPMode()));

            SmartStartAdapter adapter = new SmartStartAdapter(smartStartList);

            recycler.setLayoutManager(new LinearLayoutManager(context));
            recycler.setAdapter(adapter);
        }

        bottomSheetDialog.setContentView(binding.getRoot());

        bottomSheetDialog.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        bottomSheetDialog.show();

        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            bottomSheetDialog.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return bottomSheetDialog;
    }
}

