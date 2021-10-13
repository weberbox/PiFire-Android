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
import com.weberbox.pifire.recycler.viewmodel.PModeViewModel;
import com.weberbox.pifire.ui.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class PModeTableDialog {

    private final BottomSheetDialog mPModeBottomSheet;
    private final LayoutInflater mInflater;
    private final Context mContext;


    public PModeTableDialog(Context context) {
        mPModeBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mContext = context;

    }

    public BottomSheetDialog showDialog(){
        DialogPmodeTableBinding binding = DialogPmodeTableBinding.inflate(mInflater);

        RecyclerView recyclerView = binding.pmodeRecycler;

        PModeViewAdapter adapter = new PModeViewAdapter(getPModeList());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(adapter);

        mPModeBottomSheet.setContentView(binding.getRoot());

        mPModeBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        mPModeBottomSheet.show();

        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            mPModeBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return mPModeBottomSheet;
    }

    private List<PModeViewModel> getPModeList() {
        List<PModeViewModel> pModeList = new ArrayList<>();

        String augerOn = Prefs.getString(mContext.getString(R.string.prefs_work_auger_on),
                mContext.getString(R.string.def_pmode_auger_on));

        String[] pmodes = mContext.getResources().getStringArray(R.array.pmode_setting);
        String[] pmode_times = mContext.getResources().getStringArray(R.array.pmode_times);

        pModeList.add(new PModeViewModel(pmodes[0], augerOn, pmode_times[0]));
        pModeList.add(new PModeViewModel(pmodes[1], augerOn, pmode_times[1]));
        pModeList.add(new PModeViewModel(pmodes[2], augerOn, pmode_times[2]));
        pModeList.add(new PModeViewModel(pmodes[3], augerOn, pmode_times[3]));
        pModeList.add(new PModeViewModel(pmodes[4], augerOn, pmode_times[4]));
        pModeList.add(new PModeViewModel(pmodes[5], augerOn, pmode_times[5]));
        pModeList.add(new PModeViewModel(pmodes[6], augerOn, pmode_times[6]));
        pModeList.add(new PModeViewModel(pmodes[7], augerOn, pmode_times[7]));
        pModeList.add(new PModeViewModel(pmodes[8], augerOn, pmode_times[8]));
        pModeList.add(new PModeViewModel(pmodes[9], augerOn, pmode_times[9]));

        return pModeList;
    }
}
