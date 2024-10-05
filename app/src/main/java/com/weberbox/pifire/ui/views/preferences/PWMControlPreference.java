package com.weberbox.pifire.ui.views.preferences;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.interfaces.PWMControlCallback;
import com.weberbox.pifire.model.local.PWMControlModel;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.remote.SettingsDataModel.PWM.*;
import com.weberbox.pifire.recycler.adapter.PWMControlAdapter;
import com.weberbox.pifire.ui.dialogs.BottomButtonDialog;
import com.weberbox.pifire.ui.dialogs.PWMControlDialog;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogPWMCallback;
import com.weberbox.pifire.utils.AlertUtils;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

@SuppressWarnings("unused")
public class PWMControlPreference extends Preference implements PWMControlCallback,
        DialogPWMCallback {

    private Socket socket;
    private Context context;
    private String units;
    private PWMControlAdapter adapter;

    public PWMControlPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public PWMControlPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public PWMControlPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PWMControlPreference(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        PiFireApplication app = (PiFireApplication) context.getApplicationContext();
        this.socket = app.getSocket();
        this.context = context;
        this.units = Prefs.getString(context.getString(R.string.prefs_grill_units));
        setLayoutResource(R.layout.layout_pwm_preference);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false);

        List<PWMControlModel> pwmControlList = new ArrayList<>();

        RecyclerView recycler = (RecyclerView) holder.findViewById(R.id.pwm_control_recycler);

        List<Integer> rangeList = new Gson().fromJson(
                Prefs.getString(context.getString(R.string.prefs_pwm_temp_range)),
                new TypeToken<List<Integer>>() {
                }.getType());

        List<PWMProfile> profileList = new Gson().fromJson(
                Prefs.getString(context.getString(R.string.prefs_pwm_profiles)),
                new TypeToken<List<PWMProfile>>() {
                }.getType());

        if (!rangeList.isEmpty() && !profileList.isEmpty()) {

            rangeList.add(rangeList.get(rangeList.size() - 1) + 1);

            for (int i = 0; i <  rangeList.size(); i++) {
                pwmControlList.add(new PWMControlModel(rangeList.get(i),
                        profileList.get(i).getDutyCycle()));
            }

            adapter = new PWMControlAdapter(pwmControlList, units, this);

            recycler.setLayoutManager(new LinearLayoutManager(context));
            recycler.setAdapter(adapter);
        }
    }

    @Override
    public void onPWMControlEdit(int position) {
        if (socketConnected()) {
            List<PWMControlModel> controlItems = adapter.getControlItems();

            int temp;
            if (position == controlItems.size() - 1) {
                temp = controlItems.get(position).getTemp() - 1;
            } else {
                temp = controlItems.get(position).getTemp();
            }

            int dutyCycle = controlItems.get(position).getDutyCycle();
            int minTemp = position == 0 ? 0 : controlItems.get(position - 1).getTemp() + 1;

            Integer maxTemp;
            if (position == controlItems.size() - 1) {
                maxTemp = null;
            } else if (position == controlItems.size() - 2) {
                maxTemp = 100;
            } else {
                maxTemp = controlItems.get(position + 1).getTemp() - 1;
            }

            PWMControlDialog dialog = new PWMControlDialog(context,
                    R.string.settings_pwm_temp_range_edit, position, minTemp, maxTemp,
                    temp, dutyCycle, units, controlItems, this);
            dialog.showDialog();
        }
    }

    @Override
    public void onPWMControlDelete(int position) {
        if (socketConnected()) {
            BottomButtonDialog dialog = new BottomButtonDialog.Builder((Activity) context)
                    .setTitle(context.getString(R.string.settings_pwm_temp_range_delete))
                    .setMessage(context.getString(R.string.settings_pwm_temp_range_delete_content))
                    .setAutoDismiss(true)
                    .setNegativeButton(context.getString(R.string.cancel),
                            (dialogInterface, which) -> {
                            })
                    .setPositiveButtonWithColor(context.getString(R.string.delete),
                            R.color.dialog_positive_button_color_red,
                            (dialogInterface, which) -> {
                                adapter.removeControlItem(position);

                                List<Integer> rangeList = new ArrayList<>();
                                List<PWMProfile> profileList = new ArrayList<>();

                                List<PWMControlModel> controlItems = adapter.getControlItems();

                                int itemCount = controlItems.size();
                                for (int i = 0; i < itemCount; i++) {
                                    if (i != itemCount - 1) {
                                        rangeList.add(controlItems.get(i).getTemp());
                                    }
                                    profileList.add(new PWMProfile().withDutyCycle(
                                            controlItems.get(i).getDutyCycle()));

                                }

                                ServerControl.setPWMControlTable(socket, rangeList, profileList,
                                        this::processPostResponse);
                            })
                    .build();
            dialog.show();
        }
    }

    @Override
    public void onPWMControlAdd() {
        if (socketConnected()) {
            List<PWMControlModel> controlItems = adapter.getControlItems();
            int setTemp = controlItems.get(controlItems.size() - 1).getTemp();

            PWMControlDialog dialog = new PWMControlDialog(context,
                    R.string.settings_pwm_temp_range_add, null, null, null,
                    setTemp, 100, units, controlItems, this);
            dialog.showDialog();
        }
    }

    @Override
    public void onDialogAdd(List<PWMControlModel> controlItems, Integer temp, Integer dutyCycle) {
        adapter.addNewControlItem(temp, dutyCycle);

        List<Integer> rangeList = new ArrayList<>();
        List<PWMProfile> profileList = new ArrayList<>();

        int itemCount = controlItems.size();
        for (int i = 0; i < itemCount; i++) {
            if (i != itemCount - 1) {
                rangeList.add(controlItems.get(i).getTemp());
            }
            profileList.add(new PWMProfile().withDutyCycle(
                    controlItems.get(i).getDutyCycle()));

        }

        ServerControl.setPWMControlTable(socket, rangeList, profileList,
                this::processPostResponse);
    }

    @Override
    public void onDialogEdit(List<PWMControlModel> controlItems, int position, Integer temp,
                             Integer dutyCycle) {
        adapter.updateControlItem(position, temp, dutyCycle);

        if (position == controlItems.size() - 2) {
            adapter.updateControlItem(controlItems.size() - 1, temp + 1,
                    controlItems.get(controlItems.size() - 1).getDutyCycle());
        }

        List<Integer> rangeList = new ArrayList<>();
        List<PWMProfile> profileList = new ArrayList<>();

        int itemCount = controlItems.size();
        for (int i = 0; i < itemCount; i++) {
            if (i != itemCount - 1) {
                rangeList.add(controlItems.get(i).getTemp());
            }
            profileList.add(new PWMProfile().withDutyCycle(
                    controlItems.get(i).getDutyCycle()));

        }

        ServerControl.setPWMControlTable(socket, rangeList, profileList,
                this::processPostResponse);
    }

    private void processPostResponse(String response) {
        ServerResponseModel result = ServerResponseModel.parseJSON(response);
        if (result.getResult().equals("error")) {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(() ->
                        AlertUtils.createErrorAlert((Activity) context,
                                result.getMessage(), false));
            }
        }
    }

    private boolean socketConnected() {
        if (socket != null && socket.connected()) {
            return true;
        } else {
            AlertUtils.createErrorAlert((Activity) context, R.string.settings_error_offline, false);
            return false;
        }
    }
}
