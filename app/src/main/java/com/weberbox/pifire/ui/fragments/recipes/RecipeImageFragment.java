package com.weberbox.pifire.ui.fragments.recipes;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.FragmentRecipeImageBinding;
import com.weberbox.pifire.model.remote.RecipesModel;
import com.weberbox.pifire.model.remote.RecipesModel.Asset;
import com.weberbox.pifire.model.remote.RecipesModel.RecipeDetails;
import com.weberbox.pifire.model.view.RecipesViewModel;
import com.weberbox.pifire.record.RecipeImageRecord;
import com.weberbox.pifire.recycler.adapter.RecipeImageAdapter;

import java.util.ArrayList;

import dev.chrisbanes.insetter.Insetter;

public class RecipeImageFragment extends Fragment {

    private FragmentRecipeImageBinding binding;
    private RecipeImageAdapter recipeImageAdapter;
    private String recipeFilename, recipeImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            recipeFilename = bundle.getString(Constants.INTENT_RECIPE_FILENAME, "");
            recipeImage = bundle.getString(Constants.INTENT_RECIPE_IMAGE, "");

        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeImageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postponeEnterTransition();

        if (recipeFilename == null || recipeImage == null) {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return;
        }

        ViewPager2 viewPager = binding.recipeImageViewpager;
        DotsIndicator indicator = binding.dotsIndicator;

        recipeImageAdapter = new RecipeImageAdapter(this, onItemClickListener);
        viewPager.setAdapter(recipeImageAdapter);
        viewPager.setOffscreenPageLimit(recipeImageAdapter.getItemCount() - 1);
        indicator.attachTo(viewPager);

        Insetter.builder()
                .margin(WindowInsetsCompat.Type.systemBars())
                .applyToView(indicator);

        if (getActivity() != null) {
            RecipesViewModel recipesViewModel = new ViewModelProvider(getActivity())
                    .get(RecipesViewModel.class);
            recipesViewModel.getRecipesData().observe(getViewLifecycleOwner(), recipesData -> {
                if (recipesData != null) {
                    updateUIWithData(recipesData);
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (getActivity() != null) {
                getActivity().getWindow().setNavigationBarContrastEnforced(false);
            }
        } else {
            if (getActivity() != null) {
                getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(
                        getActivity(), android.R.color.transparent));
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (getActivity() != null) {
                getActivity().getWindow().setNavigationBarContrastEnforced(true);
            }
        } else {
            if (getActivity() != null) {
                getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(
                        getActivity(), R.color.colorNavBarAlpha));
            }
        }
    }

    private void updateUIWithData(RecipesModel recipesModel) {
        for (RecipeDetails details : recipesModel.getRecipeDetails()) {
            if (details.getFilename().equalsIgnoreCase(recipeFilename)) {
                ArrayList<RecipeImageRecord> recipeImages = orderImageRecords(details);
                recipeImageAdapter.setRecipeImages(recipeImages);
            }
        }
    }

    @NonNull
    private ArrayList<RecipeImageRecord> orderImageRecords(RecipeDetails details) {
        ArrayList<RecipeImageRecord> imagesList = new ArrayList<>();

        for (Asset asset : details.getDetails().getAssets()) {
            if (asset.getFilename().equalsIgnoreCase(recipeImage)) {
                imagesList.add(0, new RecipeImageRecord(asset.getEncodedImage(),
                        asset.getFilename(), details.getFilename()));
            } else {
                imagesList.add(new RecipeImageRecord(asset.getEncodedImage(),
                        asset.getFilename(), details.getFilename()));
            }
        }
        return imagesList;
    }

    private final RecipeImageAdapter.OnItemClickListener onItemClickListener =
            (imageView, encodedImage, recipeImage) ->
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
}
