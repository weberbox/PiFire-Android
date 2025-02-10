package com.weberbox.pifire.ui.views.preferences;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.interfaces.SmartStartCallback;
import com.weberbox.pifire.model.local.SmartStartModel;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.remote.SettingsDataModel.Startup.SmartStart.SSProfile;
import com.weberbox.pifire.recycler.adapter.SmartStartAdapter;
import com.weberbox.pifire.ui.dialogs.SmartStartDialog;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogSmartStartCallback;
import com.weberbox.pifire.utils.AlertUtils;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

@SuppressWarnings("unused")
public class SmartStartPreference extends Preference implements SmartStartCallback,
        DialogSmartStartCallback {

    private Socket socket;
    private Context context;
    private String units;
    private SmartStartAdapter adapter;

    public SmartStartPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public SmartStartPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SmartStartPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SmartStartPreference(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        PiFireApplication app = (PiFireApplication) context.getApplicationContext();
        this.socket = app.getSocket();
        this.context = context;
        this.units = Prefs.getString(context.getString(R.string.prefs_grill_units));
        setLayoutResource(R.layout.layout_smart_start_preference);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false);

        List<SmartStartModel> smartStartList = new ArrayList<>();

        RecyclerView recycler = (RecyclerView) holder.findViewById(R.id.smart_start_recycler);

        List<Integer> rangeList = new Gson().fromJson(
                Prefs.getString(context.getString(R.string.prefs_smart_start_temp_range)),
                new TypeToken<List<Integer>>() {
                }.getType());

        List<SSProfile> profileList = new Gson().fromJson(
                Prefs.getString(context.getString(R.string.prefs_smart_start_profiles)),
                new TypeToken<List<SSProfile>>() {
                }.getType());

        if (!rangeList.isEmpty() && !profileList.isEmpty()) {

            rangeList.add(rangeList.get(rangeList.size() - 1) + 1);

            for (int i = 0; i < rangeList.size(); i++) {
                smartStartList.add(new SmartStartModel(rangeList.get(i),
                        profileList.get(i).getStartUpTime(),
                        profileList.get(i).getAugerOnTime(),
                        profileList.get(i).getPMode()));
            }

            adapter = new SmartStartAdapter(smartStartList, units, this);

            recycler.setLayoutManager(new LinearLayoutManager(context));
            recycler.setAdapter(adapter);
        }

        MaterialButton addButton = (MaterialButton) holder.findViewById(R.id.smart_start_add_button);
        addButton.setOnClickListener(view -> {
            if (socketConnected()) {
                List<SmartStartModel> items = adapter.getSmartStartItems();
                int setTemp = items.get(items.size() - 1).getTemp();

                SmartStartDialog dialog = new SmartStartDialog(context,
                        R.string.settings_smart_start_temp_range_add, null, null, null,
                        setTemp, 240, 15, 2, units, false, items, this);
                dialog.showDialog();
            }
        });
    }

    @Override
    public void onSmartStartEdit(int position) {
        if (socketConnected()) {
            List<SmartStartModel> items = adapter.getSmartStartItems();

            int temp;
            if (position == items.size() - 1) {
                temp = items.get(position).getTemp() - 1;
            } else {
                temp = items.get(position).getTemp();
            }

            int startUp = items.get(position).getStartUp();
            int augerOn = items.get(position).getAugerOn();
            int pMode = items.get(position).getPMode();
            int minTemp = position == 0 ? 0 : items.get(position - 1).getTemp() + 1;

            Integer maxTemp;
            if (position == items.size() - 1) {
                maxTemp = null;
            } else if (position == items.size() - 2) {
                maxTemp = 1000;
            } else {
                maxTemp = items.get(position + 1).getTemp() - 1;
            }

            boolean deleteEnabled = position != 0 && position != 1 && position >=
                    adapter.getItemCount() - 1;

            SmartStartDialog dialog = new SmartStartDialog(context,
                    R.string.settings_smart_start_temp_range_edit, position, minTemp, maxTemp,
                    temp, startUp, augerOn, pMode, units, deleteEnabled, items, this);
            dialog.showDialog();
        }
    }

    @Override
    public void onDialogDelete(int position) {
        if (socketConnected()) {
            adapter.removeSmartStartItem(position);

            List<Integer> rangeList = new ArrayList<>();
            List<SSProfile> profileList = new ArrayList<>();

            List<SmartStartModel> items = adapter.getSmartStartItems();

            int itemCount = items.size();
            for (int i = 0; i < itemCount; i++) {
                if (i != itemCount - 1) {
                    rangeList.add(items.get(i).getTemp());
                }
                profileList.add(new SSProfile()
                        .withStartUpTime(items.get(i).getStartUp())
                        .withAugerOnTime(items.get(i).getAugerOn())
                        .withPMode(items.get(i).getPMode()));

            }

            ServerControl.setSmartStartTable(socket, rangeList, profileList,
                    this::processPostResponse);
        }
    }

    @Override
    public void onDialogAdd(List<SmartStartModel> list, Integer temp, Integer startUp,
                            Integer augerOn, Integer pMode) {
        adapter.addNewSmartStartItem(temp, startUp, augerOn, pMode);

        List<Integer> rangeList = new ArrayList<>();
        List<SSProfile> profileList = new ArrayList<>();

        int itemCount = list.size();
        for (int i = 0; i < itemCount; i++) {
            if (i != itemCount - 1) {
                rangeList.add(list.get(i).getTemp());
            }
            profileList.add(new SSProfile()
                    .withStartUpTime(list.get(i).getStartUp())
                    .withAugerOnTime(list.get(i).getAugerOn())
                    .withPMode(list.get(i).getPMode()));

        }

        ServerControl.setSmartStartTable(socket, rangeList, profileList,
                this::processPostResponse);

    }

    @Override
    public void onDialogEdit(List<SmartStartModel> list, int position, Integer temp,
                             Integer startUp, Integer augerOn, Integer pMode) {
        adapter.updateSmartStartItem(position, temp, startUp, augerOn, pMode);

        if (position == list.size() - 2) {
            adapter.updateSmartStartItem(
                    list.size() - 1, temp + 1,
                    list.get(list.size() - 1).getStartUp(),
                    list.get(list.size() - 1).getAugerOn(),
                    list.get(list.size() - 1).getPMode());
        }

        List<Integer> rangeList = new ArrayList<>();
        List<SSProfile> profileList = new ArrayList<>();

        int itemCount = list.size();
        for (int i = 0; i < itemCount; i++) {
            if (i != itemCount - 1) {
                rangeList.add(list.get(i).getTemp());
            }
            profileList.add(new SSProfile()
                    .withStartUpTime(list.get(i).getStartUp())
                    .withAugerOnTime(list.get(i).getAugerOn())
                    .withPMode(list.get(i).getPMode()));

        }

        ServerControl.setSmartStartTable(socket, rangeList, profileList,
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
