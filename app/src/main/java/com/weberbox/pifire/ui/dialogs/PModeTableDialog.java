package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogPmodeBinding;
import com.weberbox.pifire.record.PModeRecord;
import com.weberbox.pifire.recycler.adapter.PModeViewAdapter;
import com.weberbox.pifire.ui.utils.ViewUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;

public class PModeTableDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final Context context;


    public PModeTableDialog(@NotNull Context context) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public BottomSheetDialog showDialog(){
        DialogPmodeBinding binding = DialogPmodeBinding.inflate(inflater);

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

        binding.getRoot().setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                float radius = context.getResources().getDimension(R.dimen.radiusTop);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight() +
                        (int) radius, radius);
            }
        });
        binding.getRoot().setClipToOutline(true);

        binding.getRoot().setBackgroundColor(ContextCompat.getColor(context,
                R.color.material_dialog_background));

        Insetter.builder()
                .margin(WindowInsetsCompat.Type.systemBars(), Side.BOTTOM)
                .applyToView(binding.dialogContainer);

        bottomSheetDialog.show();

        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            if (bottomSheetDialog.getWindow() != null) {
                bottomSheetDialog.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
            }
        }

        return bottomSheetDialog;
    }

    private List<PModeRecord> getPModeList() {
        List<PModeRecord> pModeList = new ArrayList<>();

        String augerOn = Prefs.getString(context.getString(R.string.prefs_work_auger_on),
                context.getString(R.string.def_pmode_auger_on));

        String[] pmodes = context.getResources().getStringArray(R.array.pmode_values);
        String[] pmode_times = context.getResources().getStringArray(R.array.pmode_times);

        for (int i = 0; i < pmodes.length; i++) {
            PModeRecord mode = new PModeRecord(
                    pmodes[i], augerOn,
                    pmode_times[i]
            );
            pModeList.add(mode);
        }

        return pModeList;
    }
}
