package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller;
import com.weberbox.pifire.databinding.ItemPickerPrimeBinding;

import java.util.List;

public class PrimePickerAdapter extends RecyclerView.Adapter<PrimePickerAdapter.ViewHolder> implements
        RecyclerViewFastScroller.OnPopupTextUpdate {

    private final List<Integer> list;

    public PrimePickerAdapter(final List<Integer> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public PrimePickerAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                           final int viewType) {
        return new ViewHolder(ItemPickerPrimeBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @NonNull
    @Override
    public CharSequence onChange(int position) {
        if (position < list.size()) {
            return list.get(position).toString();
        } else {
            return list.get(position - 1).toString();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView primeAmountText;
        private final TextView primeAmountUnits;

        public ViewHolder(ItemPickerPrimeBinding binding) {
            super(binding.getRoot());
            primeAmountText = binding.primeItemTextView;
            primeAmountUnits = binding.primeItemUnitTextView;
        }

        public void bindData(final Integer amount) {
            primeAmountText.setText(String.valueOf(amount));
            primeAmountUnits.setText("g");
        }
    }
}
