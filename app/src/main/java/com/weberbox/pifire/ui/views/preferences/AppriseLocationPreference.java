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
import com.weberbox.pifire.interfaces.AppriseCallback;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.recycler.adapter.AppriseAdapter;
import com.weberbox.pifire.ui.dialogs.AppriseDialog;
import com.weberbox.pifire.ui.dialogs.BottomButtonDialog;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogAppriseCallback;
import com.weberbox.pifire.utils.AlertUtils;

import java.util.List;

import io.socket.client.Socket;

@SuppressWarnings("unused")
public class AppriseLocationPreference extends Preference implements AppriseCallback,
        DialogAppriseCallback {

    private Socket socket;
    private Context context;
    private AppriseAdapter adapter;

    public AppriseLocationPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public AppriseLocationPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AppriseLocationPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppriseLocationPreference(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        PiFireApplication app = (PiFireApplication) context.getApplicationContext();
        this.socket = app.getSocket();
        this.context = context;
        setLayoutResource(R.layout.layout_apprise_preference);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false);

        RecyclerView recycler = (RecyclerView) holder.findViewById(R.id.apprise_locations_recycler);

        List<String> locations = new Gson().fromJson(
                Prefs.getString(context.getString(R.string.prefs_notif_apprise_locations)),
                new TypeToken<List<String>>() {
                }.getType());

        adapter = new AppriseAdapter(locations, this);
        recycler.setLayoutManager(new LinearLayoutManager(context));
        recycler.setAdapter(adapter);
    }

    @Override
    public void onLocationAdd() {
        if (socketConnected()) {
            AppriseDialog dialog = new AppriseDialog(context,
                    R.string.settings_apprise_add, null, null, this);
            dialog.showDialog();
        }
    }

    @Override
    public void onLocationEdit(int position) {
        if (socketConnected()) {
            List<String> locations = adapter.getLocations();
            String location = locations.get(position);
            AppriseDialog dialog = new AppriseDialog(context,
                    R.string.settings_apprise_edit, position, location, this);
            dialog.showDialog();
        }
    }

    @Override
    public void onLocationDelete(int position) {
        if (socketConnected()) {
            BottomButtonDialog dialog = new BottomButtonDialog.Builder((Activity) context)
                    .setTitle(context.getString(R.string.settings_apprise_delete))
                    .setMessage(context.getString(R.string.settings_apprise_delete_content))
                    .setAutoDismiss(true)
                    .setNegativeButton(context.getString(R.string.cancel),
                            (dialogInterface, which) -> {
                            })
                    .setPositiveButtonWithColor(context.getString(R.string.delete),
                            R.color.dialog_positive_button_color_red,
                            (dialogInterface, which) -> {
                                adapter.removeLocation(position);
                                updateLocations(adapter.getLocations());
                            })
                    .build();
            dialog.show();
        }
    }

    @Override
    public void onDialogAdd(String location) {
        adapter.addLocation(location);
        updateLocations(adapter.getLocations());
    }

    @Override
    public void onDialogEdit(int position, String location) {
        adapter.updateLocation(position, location);
        updateLocations(adapter.getLocations());
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

    private void updateLocations(List<String> locations) {
        if (locations != null) {
            Prefs.putString(context.getString(R.string.prefs_notif_apprise_locations),
                    new Gson().toJson(locations));
            ServerControl.setAppriseLocations(socket, locations, this::processPostResponse);
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

