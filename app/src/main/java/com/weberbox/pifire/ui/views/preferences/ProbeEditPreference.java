package com.weberbox.pifire.ui.views.preferences;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

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
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeInfo;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeMap;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeProfileModel;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.recycler.adapter.ProbeEditAdapter;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogProbeCallback;
import com.weberbox.pifire.utils.AlertUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;

@SuppressWarnings("unused")
public class ProbeEditPreference extends Preference implements DialogProbeCallback {

    private Socket socket;
    private Context context;
    private ProbeMap probeMap;
    private List<ProbeProfileModel> profiles;
    private ProbeEditAdapter adapter;

    public ProbeEditPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public ProbeEditPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ProbeEditPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProbeEditPreference(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        PiFireApplication app = (PiFireApplication) context.getApplicationContext();
        this.socket = app.getSocket();
        this.context = context;
        setLayoutResource(R.layout.layout_probes_preference);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false);

        List<String> probes = new ArrayList<>();

        RecyclerView recycler = (RecyclerView) holder.findViewById(R.id.probes_recycler);
        TextView noProbesText = (TextView) holder.findViewById(R.id.no_probes_tv);

        Map<String, ProbeProfileModel> profilesHash = new Gson().fromJson(
                Prefs.getString(context.getString(R.string.prefs_probe_profiles)),
                new TypeToken<HashMap<String, ProbeProfileModel>>() {}.getType());

        profiles = new ArrayList<>(profilesHash.values());

        probeMap = new Gson().fromJson(
                Prefs.getString(context.getString(R.string.prefs_probe_map)),
                new TypeToken<ProbeMap>() {}.getType());

        List<ProbeInfo> probeInfo = probeMap.getProbeInfo();

        if (probeInfo != null && !probeInfo.isEmpty()) {
            adapter = new ProbeEditAdapter(probeInfo, profiles, this);
            recycler.setLayoutManager(new LinearLayoutManager(context));
            recycler.setAdapter(adapter);
        } else {
            recycler.setVisibility(View.GONE);
            noProbesText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProbeUpdated(int position, @NotNull String probeName,
                               @NonNull String probeProfile) {
        if (socketConnected()) {
            adapter.updateProbeType(position, probeName);
            probeMap.getProbeInfo().get(position).setName(probeName);
            for (ProbeProfileModel profile : profiles) {
                if (profile.getName().equals(probeProfile)) {
                    probeMap.getProbeInfo().get(position).setProbeProfile(profile);
                }
            }
            Prefs.putString(context.getString(R.string.prefs_probe_map),
                    new Gson().toJson(probeMap));
            ServerControl.sendUpdatedProbeMap(socket, probeMap, this::processPostResponse);
        }
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
