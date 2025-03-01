package com.weberbox.pifire.recycler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.databinding.ItemRecipeStepsBinding;
import com.weberbox.pifire.model.remote.RecipesModel.Step;
import com.weberbox.pifire.utils.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder> {

    private final List<Step> list;
    private String units;
    private boolean running = false;

    public RecipeStepsAdapter() {
        this.list = new ArrayList<>();
        this.units = "F";
    }

    @NonNull
    @Override
    public RecipeStepsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                            final int viewType) {
        return new ViewHolder(ItemRecipeStepsBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(list.get(position), holder, units, position, running);
    }

    @SuppressWarnings("NotifyDataSetChanged")
    public void setRecipeSteps(@NotNull List<Step> steps, String units) {
        list.clear();
        list.addAll(steps);
        this.units = units;
        notifyDataSetChanged();
    }

    public void setStepSelected(int currentStep) {
        this.running = true;
        for (int i = 0; i < list.size(); i++) {
            Step step = list.get(i);
            step.setCurrentStep(i == currentStep);
        }
        notifyItemChanged(currentStep);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView step;
        private final View stepColor;
        private final TextView stepHeader;
        private final TextView mode;
        private final TextView options;
        private final TextView notifHeader;
        private final TextView notifications;

        public ViewHolder(ItemRecipeStepsBinding binding) {
            super(binding.getRoot());
            step = binding.recipeStepsStep;
            stepColor = binding.recipeStepsStepColor;
            stepHeader = binding.recipeStepsHeader;
            mode = binding.recipeStepsMode;
            options = binding.recipeStepsOptions;
            notifHeader = binding.recipeStepsNotifHeader;
            notifications = binding.recipeStepsNotif;
        }

        public void bindData(Step step, ViewHolder holder, String units, int position,
                             boolean running) {
            Context context = holder.itemView.getContext();
            holder.step.setText(String.format(context.getString(R.string.recipes_step_step),
                    position));
            if (step.getMode().equalsIgnoreCase(ServerConstants.G_MODE_HOLD)) {
                holder.mode.setText(String.format(context.getString(
                        R.string.recipes_step_mode_hold), step.getMode(),
                        step.getHoldTemp(), units));
            } else {
                holder.mode.setText(step.getMode());
            }
            if (step.getMode().equalsIgnoreCase(ServerConstants.G_MODE_SHUTDOWN)) {
                holder.stepHeader.setVisibility(ViewGroup.GONE);
                holder.notifHeader.setVisibility(View.GONE);
                holder.notifications.setVisibility(View.GONE);
            }
            if (step.getMode().equalsIgnoreCase(ServerConstants.G_MODE_START)) {
                holder.stepHeader.setText(context.getString(R.string.recipes_step_startup_header));
            }
            holder.options.setText(formatOptions(context, step, units));
            if (!step.getMessage().isBlank()) {
                holder.notifications.setText(String.format(context.getString(
                        R.string.recipes_step_notif), step.getMessage().trim()));
            } else {
                holder.notifHeader.setVisibility(View.GONE);
                holder.notifications.setVisibility(View.GONE);
            }
            if (running) {
                if (step.getCurrentStep()) {
                    holder.stepColor.setBackgroundColor(context.getResources()
                            .getColor(R.color.colorAccentDisabled, null));
                } else {
                    holder.stepColor.setBackgroundColor(context.getResources()
                            .getColor(R.color.colorLighterGrey, null));
                }
            }
        }
    }

    private static CharSequence formatOptions(Context context, Step step, String units) {
        ArrayList<String> options = new ArrayList<>();
        if (step.getMode().equalsIgnoreCase(ServerConstants.G_MODE_START)) {
            options.add(context.getString(R.string.recipes_step_options_startup));
        } else {
            if (step.getTriggerTemps().getPrimary() > 0) {
                options.add(String.format(context.getString(R.string.recipes_step_options_primary),
                        step.getTriggerTemps().getPrimary(), units));
            }
            int count = 1;
            for (Integer temp : step.getTriggerTemps().getFood()) {
                if (temp > 0) {
                    options.add(String.format(context.getString(
                            R.string.recipes_step_options_food), count, temp, units));
                    count++;
                }
            }
            if (step.getTimer() > 0) {
                options.add(String.format(context.getString(
                        R.string.recipes_step_options_timer), step.getTimer()));
            }
            if (step.getPause()) {
                options.add(context.getString(R.string.recipes_step_options_user));
            }
        }
        return StringUtils.toBulletedList(options);
    }
}
