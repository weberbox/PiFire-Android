package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogPmodeTableBinding;
import com.weberbox.pifire.recycler.adapter.PModeViewAdapter;
import com.weberbox.pifire.model.local.PModeModel;
import com.weberbox.pifire.ui.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class PModeTableDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final Context context;


    public PModeTableDialog(Context context) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public BottomSheetDialog showDialog(){
        DialogPmodeTableBinding binding = DialogPmodeTableBinding.inflate(inflater);

        RecyclerView recyclerView = binding.pmodeRecycler;

        PModeViewAdapter adapter = new PModeViewAdapter(getPModeList());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(adapter);

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

    private List<PModeModel> getPModeList() {
        List<PModeModel> pModeList = new ArrayList<>();

        String augerOn = Prefs.getString(context.getString(R.string.prefs_work_auger_on),
                context.getString(R.string.def_pmode_auger_on));

        String[] pmodes = context.getResources().getStringArray(R.array.pmode_setting);
        String[] pmode_times = context.getResources().getStringArray(R.array.pmode_times);

        pModeList.add(new PModeModel(pmodes[0], augerOn, pmode_times[0]));
        pModeList.add(new PModeModel(pmodes[1], augerOn, pmode_times[1]));
        pModeList.add(new PModeModel(pmodes[2], augerOn, pmode_times[2]));
        pModeList.add(new PModeModel(pmodes[3], augerOn, pmode_times[3]));
        pModeList.add(new PModeModel(pmodes[4], augerOn, pmode_times[4]));
        pModeList.add(new PModeModel(pmodes[5], augerOn, pmode_times[5]));
        pModeList.add(new PModeModel(pmodes[6], augerOn, pmode_times[6]));
        pModeList.add(new PModeModel(pmodes[7], augerOn, pmode_times[7]));
        pModeList.add(new PModeModel(pmodes[8], augerOn, pmode_times[8]));
        pModeList.add(new PModeModel(pmodes[9], augerOn, pmode_times[9]));

        return pModeList;
    }
}
