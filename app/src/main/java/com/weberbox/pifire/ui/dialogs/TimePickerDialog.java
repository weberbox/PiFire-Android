package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogTimerPickerBinding;
import com.weberbox.pifire.recycler.adapter.TimePickerAdapter;
import com.weberbox.pifire.recycler.manager.PickerLayoutManager;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogDashboardCallback;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.ViewUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;

public class TimePickerDialog {

    private final BottomSheetDialog pickerBottomSheet;
    private final DialogDashboardCallback callback;
    private RecyclerView hoursList;
    private RecyclerView minutesList;
    private String hoursSelected = "00";
    private String minutesSelected = "00";
    private final LayoutInflater inflater;
    private final Context context;
    private final int scrollHours;
    private final int scrollMinutes;

    public TimePickerDialog(@NotNull Context context, @NotNull DialogDashboardCallback callback) {
        pickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.callback = callback;
        this.scrollHours = 0;
        this.scrollMinutes = 0;
    }

    public BottomSheetDialog showDialog() {
        DialogTimerPickerBinding binding = DialogTimerPickerBinding.inflate(inflater);

        RelativeLayout optionsContainer = binding.timerOptionsContainer;
        SwitchCompat shutdownSwitch = binding.timerShutdownSwitch;
        SwitchCompat keepWarmSwitch = binding.timerKeepWarmSwitch;
        MaterialButton confirmButton = binding.setTimerConfirm;
        MaterialButton optionsButton = binding.timerOptions;

        PickerLayoutManager hoursPickerLayoutManager = new PickerLayoutManager(context,
                PickerLayoutManager.VERTICAL, false);
        hoursPickerLayoutManager.setChangeAlpha(true);
        hoursPickerLayoutManager.setScaleDownBy(0.99f);
        hoursPickerLayoutManager.setScaleDownDistance(1.9f);

        PickerLayoutManager minsPickerLayoutManager = new PickerLayoutManager(context,
                PickerLayoutManager.VERTICAL, false);
        minsPickerLayoutManager.setChangeAlpha(true);
        minsPickerLayoutManager.setScaleDownBy(0.96f);
        minsPickerLayoutManager.setScaleDownDistance(1.9f);

        hoursList = binding.hoursList;
        minutesList = binding.minutesList;

        SnapHelper hoursSnapHelper = new LinearSnapHelper();
        SnapHelper minutesSnapHelper = new LinearSnapHelper();
        hoursSnapHelper.attachToRecyclerView(hoursList);
        minutesSnapHelper.attachToRecyclerView(minutesList);

        TimePickerAdapter hoursAdapter = new TimePickerAdapter(generateTimeList(0, 24));
        TimePickerAdapter minsAdapter = new TimePickerAdapter(generateTimeList(0, 60));

        hoursList.setLayoutManager(hoursPickerLayoutManager);
        hoursList.setAdapter(hoursAdapter);

        minutesList.setLayoutManager(minsPickerLayoutManager);
        minutesList.setAdapter(minsAdapter);

        optionsButton.setOnClickListener(v -> {
            if (optionsContainer.getVisibility() == View.GONE) {
                AnimUtils.slideOpen(optionsContainer);
            } else {
                AnimUtils.slideClosed(optionsContainer);
            }
        });

        shutdownSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                keepWarmSwitch.setEnabled(!isChecked));

        keepWarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                shutdownSwitch.setEnabled(!isChecked));

        hoursPickerLayoutManager.setOnScrollStopListener(
                view -> {
                    LinearLayout parent = view.findViewById(R.id.timer_item_container);
                    TextView text = parent.findViewById(R.id.timer_item_text_view);
                    hoursSelected = text.getText().toString();
                });

        minsPickerLayoutManager.setOnScrollStopListener(
                view -> {
                    LinearLayout parent = view.findViewById(R.id.timer_item_container);
                    TextView text = parent.findViewById(R.id.timer_item_text_view);
                    minutesSelected = text.getText().toString();
                });

        confirmButton.setOnClickListener(v -> {
            pickerBottomSheet.dismiss();
            callback.onTimerConfirmClicked(hoursSelected, minutesSelected,
                    shutdownSwitch.isChecked(), keepWarmSwitch.isChecked());
        });

        pickerBottomSheet.setOnDismissListener(dialogInterface -> {

        });

        pickerBottomSheet.setContentView(binding.getRoot());

        if (scrollHours != 0) {
            setCurrentHours(scrollHours, false);
            hoursSelected = String.format(Locale.getDefault(), "%02d", scrollHours);
        } else {
            setCurrentHours(0, false);
        }

        if (scrollMinutes != 0) {
            setCurrentMinutes(scrollMinutes, false);
            minutesSelected = String.format(Locale.getDefault(), "%02d", scrollMinutes);
        } else {
            setCurrentMinutes(0, false);
        }

        pickerBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
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
    private void setCurrentHours(int position, boolean smooth) {
        if (smooth) {
            hoursList.smoothScrollToPosition(position);
        } else {
            hoursList.scrollToPosition(position);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void setCurrentMinutes(int position, boolean smooth) {
        if (smooth) {
            minutesList.smoothScrollToPosition(position);
        } else {
            minutesList.scrollToPosition(position);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private List<String> generateTimeList(int start, int end) {
        List<String> strings;

        NumberFormat formatter = new DecimalFormat("00");
        strings = IntStream.range(start, end).mapToObj(formatter::format).collect(
                Collectors.toList());

        return strings;
    }
}
