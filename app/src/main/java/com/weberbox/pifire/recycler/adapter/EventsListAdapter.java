package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemEventsListBinding;
import com.weberbox.pifire.model.local.EventsModel;
import com.weberbox.pifire.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {

    private List<EventsModel> list;

    public EventsListAdapter() {}

    @NonNull
    @Override
    public EventsListAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                           final int viewType) {
        return new ViewHolder(ItemEventsListBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final EventsModel event = list.get(position);
        holder.bindData(event);

        holder.root.setOnClickListener(view -> {
            if (TextUtils.hasEllipsis(holder.eventText)) {
                holder.eventText.setMaxLines(2);
                holder.eventText.setEllipsize(null);
            } else if (holder.eventText.getMaxLines() > 1) {
                holder.eventText.setMaxLines(1);
                holder.eventText.setEllipsize(android.text.TextUtils.TruncateAt.END);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEventsList(List<EventsModel> eventsList) {
        this.list = new ArrayList<>(eventsList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View root;
        private final TextView eventIcon;
        private final TextView eventDate;
        private final TextView eventTime;
        private final TextView eventText;

        public ViewHolder(final ItemEventsListBinding binding) {
            super(binding.getRoot());
            root = binding.getRoot();
            eventIcon = binding.eventIconHolder;
            eventDate = binding.eventDateTextHolder;
            eventTime = binding.eventTimeTextHolder;
            eventText = binding.eventTextHolder;
        }

        public void bindData(final EventsModel model) {
            ((GradientDrawable) eventIcon.getBackground()).setColor(model.getEventIconColor());
            eventIcon.setText(model.getEventIcon());
            eventDate.setText(model.getEventDate());
            eventTime.setText(model.getEventTime());
            eventText.setText(model.getEventText());
        }
    }
}
