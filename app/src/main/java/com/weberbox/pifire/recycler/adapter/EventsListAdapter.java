package com.weberbox.pifire.recycler.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.ItemEventsListBinding;
import com.weberbox.pifire.model.remote.EventsModel.Events;
import com.weberbox.pifire.ui.dialogs.MaterialDialogText;
import com.weberbox.pifire.utils.StringUtils;
import com.weberbox.pifire.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {

    private final List<Events> list;
    private final Activity activity;
    private final boolean isFahrenheit;

    public EventsListAdapter(@NotNull Activity activity) {
        this.list = new ArrayList<>();
        this.activity = activity;
        isFahrenheit = Prefs.getString(activity.getString(R.string.prefs_grill_units)).equals("F");
    }

    @NonNull
    @Override
    public EventsListAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                           final int viewType) {
        return new ViewHolder(ItemEventsListBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Events event = list.get(position);
        holder.bindData(holder.itemView.getContext(), event, isFahrenheit);

        holder.root.setOnClickListener(view -> {
            if (StringUtils.hasEllipsis(holder.eventText)) {
                MaterialDialogText dialog = new MaterialDialogText.Builder(activity)
                        .setTitle(activity.getString(R.string.dialog_event))
                        .setMessage(holder.eventText.getText().toString())
                        .setPositiveButton(activity.getString(R.string.close),
                                (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressWarnings("NotifyDataSetChanged")
    public void setEventsList(List<Events> eventsList) {
        list.clear();
        list.addAll(eventsList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View root;
        private final TextView eventIcon;
        private final TextView eventDate;
        private final TextView eventTime;
        private final TextView eventText;

        public ViewHolder(ItemEventsListBinding binding) {
            super(binding.getRoot());
            root = binding.getRoot();
            eventIcon = binding.eventIconHolder;
            eventDate = binding.eventDateTextHolder;
            eventTime = binding.eventTimeTextHolder;
            eventText = binding.eventTextHolder;
        }

        public void bindData(Context context, Events event, boolean isFahrenheit) {
            eventIcon.setBackgroundColor(ContextCompat.getColor(context, event.getEventIconColor()));
            eventIcon.setText(event.getEventIcon());
            eventText.setText(event.getEventText());
            if (isFahrenheit && !event.getEventTime().equals("--:--:--")) {
                try {
                    eventTime.setText(TimeUtils.parseDate(
                            event.getEventTime(), "HH:mm:ss", "h:mm a"));
                    eventDate.setText(TimeUtils.parseDate(
                            event.getEventDate(), "yyyy-MM-dd", "MM-dd-yyyy"));
                } catch (ParseException e) {
                    Timber.e(e, "Failed parsing event dates");
                    eventTime.setText(event.getEventTime());
                    eventDate.setText(event.getEventDate());
                }
            } else {
                eventTime.setText(event.getEventTime());
                eventDate.setText(event.getEventDate());
            }
        }
    }
}
