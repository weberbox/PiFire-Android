package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.databinding.DialogPrimePickerBinding;
import com.weberbox.pifire.recycler.adapter.PrimePickerAdapter;
import com.weberbox.pifire.recycler.manager.PickerLayoutManager;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogPrimeCallback;
import com.weberbox.pifire.ui.utils.ViewUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrimePickerDialog {

    private String selectedAmount;
    private final BottomSheetDialog pickerBottomSheet;
    private final LayoutInflater inflater;
    private final DialogPrimeCallback callback;
    private final Context context;

    public PrimePickerDialog(Context context, DialogPrimeCallback callback) {
        pickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.callback = callback;
    }

    public BottomSheetDialog showDialog() {
        DialogPrimePickerBinding binding = DialogPrimePickerBinding.inflate(inflater);

        SwitchCompat startupSwitch = binding.primeStartupSwitch;
        Button confirmButton = binding.setPrimeConfirm;
        Button cancelButton = binding.setPrimeCancel;

        PickerLayoutManager pickerLayoutManager = new PickerLayoutManager(context,
                PickerLayoutManager.VERTICAL, true);
        pickerLayoutManager.setChangeAlpha(true);
        pickerLayoutManager.setScaleDownBy(0.99f);
        pickerLayoutManager.setScaleDownDistance(1.9f);

        RecyclerView recyclerView = binding.primeList;

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        PrimePickerAdapter adapter = new PrimePickerAdapter(
                generatePrimeList(AppConfig.PRIME_MIN_GRAMS, AppConfig.PRIME_MAX_GRAMS));

        recyclerView.setLayoutManager(pickerLayoutManager);
        recyclerView.setAdapter(adapter);

        selectedAmount = String.valueOf(AppConfig.PRIME_MIN_GRAMS);

        pickerLayoutManager.setOnScrollStopListener(
                view -> {
                    LinearLayout parent = view.findViewById(R.id.prime_item_container);
                    RelativeLayout parent_two = parent.findViewById(R.id.prime_item_container_two);
                    TextView text = parent_two.findViewById(R.id.prime_item_text_view);
                    selectedAmount = text.getText().toString();
                });

        RecyclerViewFastScroller fastScroll = binding.primeFastScroll;
        fastScroll.setHandleStateListener(new RecyclerViewFastScroller.HandleStateListener() {
            @Override
            public void onEngaged() {

            }

            @Override
            public void onDragged(float v, int position) {
                selectedAmount = String.valueOf(adapter.onChange(position));
            }

            @Override
            public void onReleased() {

            }
        });

        confirmButton.setOnClickListener(v -> {
            pickerBottomSheet.dismiss();
            callback.onPrimeAmountSelected(Integer.valueOf(selectedAmount),
                    startupSwitch.isChecked() ? ServerConstants.G_MODE_START :
                            ServerConstants.G_MODE_STOP);
        });

        cancelButton.setOnClickListener(v -> pickerBottomSheet.dismiss());


        pickerBottomSheet.setOnDismissListener(dialogInterface -> {

        });

        pickerBottomSheet.setContentView(binding.getRoot());

        pickerBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        pickerBottomSheet.show();

        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            if (pickerBottomSheet.getWindow() != null) {
                pickerBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
            }
        }

        return pickerBottomSheet;
    }

    @SuppressWarnings("SameParameterValue")
    private static List<Integer> generatePrimeList(int start, int end) {
        return IntStream.iterate(start, i -> i + 5).limit((end - start) / 5 + 1)
                .boxed().collect(Collectors.toList());
    }
}
