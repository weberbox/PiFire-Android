package com.weberbox.pifire.ui.views.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.interfaces.ExtraHeadersCallback;
import com.weberbox.pifire.model.local.ExtraHeadersModel;
import com.weberbox.pifire.recycler.adapter.ExtraHeadersAdapter;
import com.weberbox.pifire.ui.dialogs.ExtraHeadersDialog;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogHeadersCallback;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.Objects;

import io.socket.client.Socket;

@SuppressWarnings("unused")
public class ExtraHeadersPreference extends Preference implements ExtraHeadersCallback,
        DialogHeadersCallback {

    private Socket socket;
    private Context context;
    private Activity activity;
    private String units;
    private ExtraHeadersAdapter adapter;

    public ExtraHeadersPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public ExtraHeadersPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ExtraHeadersPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExtraHeadersPreference(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        PiFireApplication app = (PiFireApplication) context.getApplicationContext();
        this.context = context;
        this.activity = (Activity) context;
        setLayoutResource(R.layout.layout_headers_preference);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false);

        RecyclerView recycler = (RecyclerView) holder.findViewById(R.id.extra_headers_recycler);

        String headers = SecurityUtils.decrypt(context, R.string.prefs_server_headers);

        ArrayList<ExtraHeadersModel> extraHeaders = ExtraHeadersModel.parseJSON(headers);

        adapter = new ExtraHeadersAdapter(Objects.requireNonNullElseGet(extraHeaders,
                ArrayList::new), this);

        recycler.setLayoutManager(new LinearLayoutManager(context));
        recycler.setAdapter(adapter);

        MaterialButton addHeader = (MaterialButton) holder.findViewById(R.id.headers_add_button);
        addHeader.setOnClickListener(view -> {
            ExtraHeadersDialog dialog = new ExtraHeadersDialog(context, null,
                    null, null, ExtraHeadersPreference.this);
            dialog.showDialog();
        });
    }

    @Override
    public void onHeaderEdit(int position) {
        ArrayList<ExtraHeadersModel> headerItems = adapter.getHeaderItems();

        String key = headerItems.get(position).getHeaderKey();
        String value = headerItems.get(position).getHeaderValue();

        ExtraHeadersDialog dialog = new ExtraHeadersDialog(context, key,
                value, position, this);
        dialog.showDialog();

    }

    @Override
    public void onDialogDelete(int position) {
        adapter.removeHeaderItem(position);
        if (saveExtraHeaders(adapter.getHeaderItems())) {
            AlertUtils.createErrorAlert(activity,
                    context.getString(R.string.settings_extra_headers_error), false);
        }
    }

    @Override
    public void onDialogSave(String key, String value, Integer position) {
        if (position != null) {
            adapter.updateHeaderItem(position, key, value);
        } else {
            adapter.addNewHeaderItem(key, value);
        }
        if (saveExtraHeaders(adapter.getHeaderItems())) {
            AlertUtils.createErrorAlert(activity,
                    context.getString(R.string.settings_extra_headers_error), false);
        }
    }

    private boolean saveExtraHeaders(ArrayList<ExtraHeadersModel> headers) {
        Intent intent = new Intent("extra-headers-update");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        return !SecurityUtils.encrypt(context, R.string.prefs_server_headers,
                new Gson().toJson(headers));
    }

}
