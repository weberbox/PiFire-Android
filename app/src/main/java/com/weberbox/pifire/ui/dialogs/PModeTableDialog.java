package com.weberbox.pifire.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.recycler.adapter.PModeViewAdapter;
import com.weberbox.pifire.recycler.viewmodel.PModeViewModel;

import java.util.ArrayList;
import java.util.List;

public class PModeTableDialog {
    private static String TAG = PModeTableDialog.class.getSimpleName();

    private final BottomSheetDialog mPModeBottomSheet;
    private final LayoutInflater mInflater;
    private final Context mContext;


    public PModeTableDialog(Context context) {
        mPModeBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mContext = context;

    }

    public BottomSheetDialog showDialog(){
        @SuppressLint("InflateParams")
        View sheetView = mInflater.inflate(R.layout.dialog_pmode_table, null);

        RecyclerView recyclerView = sheetView.findViewById(R.id.pmode_recycler);

        PModeViewAdapter adapter = new PModeViewAdapter(getPModeList());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(adapter);

        mPModeBottomSheet.setContentView(sheetView);

        mPModeBottomSheet.show();

        return mPModeBottomSheet;
    }

    private List<PModeViewModel> getPModeList() {
        List<PModeViewModel> pModeList = new ArrayList<>();

        String augerOn = Prefs.getString(mContext.getString(R.string.prefs_work_auger_on), "15");

        pModeList.add(new PModeViewModel("0", augerOn, "45 Seconds"));
        pModeList.add(new PModeViewModel("1", augerOn, "55 Seconds"));
        pModeList.add(new PModeViewModel("2", augerOn, "65 Seconds"));
        pModeList.add(new PModeViewModel("3", augerOn, "75 Seconds"));
        pModeList.add(new PModeViewModel("4", augerOn, "85 Seconds"));
        pModeList.add(new PModeViewModel("5", augerOn, "95 Seconds"));
        pModeList.add(new PModeViewModel("6", augerOn, "105 Seconds"));
        pModeList.add(new PModeViewModel("7", augerOn, "115 Seconds"));
        pModeList.add(new PModeViewModel("8", augerOn, "125 Seconds"));
        pModeList.add(new PModeViewModel("9", augerOn, "135 Seconds"));

        return pModeList;
    }
}
