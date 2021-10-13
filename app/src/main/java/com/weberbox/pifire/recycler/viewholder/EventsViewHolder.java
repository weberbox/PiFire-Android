package com.weberbox.pifire.recycler.viewholder;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.recycler.viewmodel.EventViewModel;
import com.weberbox.pifire.utils.TextUtils;

public class EventsViewHolder extends RecyclerView.ViewHolder {

    private final TextView mEventIcon;
    private final TextView mEventDate;
    private final TextView mEventTime;
    private final TextView mEventText;

    public EventsViewHolder(final View itemView) {
        super(itemView);
        mEventIcon = itemView.findViewById(R.id.event_icon_holder);
        mEventDate = itemView.findViewById(R.id.event_date_text_holder);
        mEventTime = itemView.findViewById(R.id.event_time_text_holder);
        mEventText = itemView.findViewById(R.id.event_text_holder);

        itemView.setOnClickListener(view -> {
            if (TextUtils.hasEllipsis(mEventText)) {
                mEventText.setMaxLines(2);
                mEventText.setEllipsize(null);
            } else if (mEventText.getMaxLines() > 1) {
                mEventText.setMaxLines(1);
                mEventText.setEllipsize(android.text.TextUtils.TruncateAt.END);
            }
        });

    }

    public void bindData(final EventViewModel viewModel) {
        ((GradientDrawable) mEventIcon.getBackground()).setColor(viewModel.getEventIconColor());
        mEventIcon.setText(viewModel.getEventIcon());
        mEventDate.setText(viewModel.getEventDate());
        mEventTime.setText(viewModel.getEventTime());
        mEventText.setText(viewModel.getEventText());
    }
}