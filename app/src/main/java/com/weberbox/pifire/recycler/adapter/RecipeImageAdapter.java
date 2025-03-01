package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.ItemRecipeImageBinding;
import com.weberbox.pifire.databinding.ItemRecipeImagePagerBinding;
import com.weberbox.pifire.record.RecipeImageRecord;
import com.weberbox.pifire.ui.fragments.recipes.RecipeViewFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import timber.log.Timber;

public class RecipeImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final OnItemClickListener onItemClickListener;
    private final Fragment fragment;
    private final ArrayList<RecipeImageRecord> imagesList;

    public RecipeImageAdapter(@NotNull Fragment fragment,
                              @NotNull OnItemClickListener onItemClickListener) {
        this.fragment = fragment;
        this.onItemClickListener = onItemClickListener;
        this.imagesList = new ArrayList<>();
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (fragment instanceof RecipeViewFragment) {
            return new ImageViewHolder(ItemRecipeImageBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        } else {
            return new ImagePagerViewHolder(ItemRecipeImagePagerBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ImageViewHolder vh) {
                RecipeImageRecord record = imagesList.get(position);
                vh.bindData(fragment, record.encodedImage(), vh);
                vh.itemView.setOnClickListener(view -> onItemClickListener.onClick(
                        vh.imageView, record.recipeFilename(), record.recipeImage()));
            } else if (holder instanceof ImagePagerViewHolder vh) {
                RecipeImageRecord record = imagesList.get(position);
                vh.bindData(fragment, imagesList.get(position).encodedImage(), vh);
                vh.frameLayout.setOnClickListener(view -> onItemClickListener.onClick(
                        vh.imageView, imagesList.get(position).recipeFilename(),
                        record.recipeImage()));
            }
        } catch (Exception e) {
            Timber.e(e, "onBindViewHolder Error");
        }
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRecipeImages(@NotNull ArrayList<RecipeImageRecord> images) {
        imagesList.clear();
        imagesList.addAll(images);
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public ImageViewHolder(ItemRecipeImageBinding binding) {
            super(binding.getRoot());
            imageView = binding.recipeViewImage;
        }

        public void bindData(Fragment fragment, String encodedImage, ImageViewHolder holder) {
            loadImage(fragment, encodedImage, holder.imageView);
        }
    }

    public static class ImagePagerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final FrameLayout frameLayout;

        public ImagePagerViewHolder(ItemRecipeImagePagerBinding binding) {
            super(binding.getRoot());
            imageView = binding.recipeViewImage;
            frameLayout = binding.imageItemContainer;
        }

        public void bindData(Fragment fragment, String encodedImage, ImagePagerViewHolder holder) {
            loadImage(fragment, encodedImage, holder.imageView);
        }
    }

    private static void loadImage(Fragment fragment, String encodedImage, ImageView imageView) {
        Glide.with(fragment)
                .asBitmap()
                .load(decodeImage(encodedImage))
                .error(R.drawable.ic_recipe_placeholder_error)
                .transition(BitmapTransitionOptions.withCrossFade())
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target,
                                                boolean isFirstResource) {
                        fragment.startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model,
                                                   Target<Bitmap> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        fragment.startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(imageView);
    }

    private static Bitmap decodeImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public interface OnItemClickListener {
        void onClick(ImageView imageView, String encodedImage, String recipeImage);
    }
}
