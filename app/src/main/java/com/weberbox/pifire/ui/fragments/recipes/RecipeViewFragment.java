package com.weberbox.pifire.ui.fragments.recipes;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView.OnScrollChangeListener;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.utils.executors.AppExecutors;
import com.weberbox.pifire.database.RecipeDatabase;
import com.weberbox.pifire.databinding.FragmentRecipeViewBinding;
import com.weberbox.pifire.model.local.RecipesModel;
import com.weberbox.pifire.model.local.RecipesModel.RecipeItems;
import com.weberbox.pifire.recycler.adapter.RecipeViewAdapter;
import com.weberbox.pifire.ui.activities.RecipeActivity;
import com.weberbox.pifire.ui.dialogs.ImageViewDialog;
import com.weberbox.pifire.utils.StringUtils;
import com.weberbox.pifire.utils.TimeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecipeViewFragment extends Fragment {

    private FragmentRecipeViewBinding binding;
    private RecipeViewAdapter instructionsAdapter;
    private RecipeViewAdapter ingredientsAdapter;
    private RecipeDatabase recipeDB;
    private RecipesModel recipe;
    private FloatingActionButton floatingActionButton;
    private LinearLayout ingredientsContainer;
    private LinearLayout instructionsContainer;
    private LinearLayout notesContainer;
    private RatingBar recipeRating;
    private TextView recipeName;
    private TextView recipeTime;
    private TextView recipeDifficulty;
    private TextView recipeCreated;
    private TextView recipeModified;
    private TextView recipeNotes;
    private ImageView recipeImage;
    private int recipeId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            recipeId = bundle.getInt(Constants.INTENT_RECIPE_ID, -1);
        }

        if (getActivity() != null && getActivity().getApplicationContext() != null) {
            recipeDB = RecipeDatabase.getInstance(getActivity().getApplicationContext());
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipeImage = binding.rvImage;
        recipeName = binding.rvName;
        recipeRating = binding.rvRating;
        recipeTime = binding.rvTime;
        recipeDifficulty = binding.rvDifficulty;
        recipeCreated = binding.rvCreated;
        recipeModified = binding.rvModified;
        ingredientsContainer = binding.rvIngredientsContainer;
        instructionsContainer = binding.rvInstructionsContainer;
        notesContainer = binding.rvNotesContainer;
        recipeNotes = binding.rvNotes;

        floatingActionButton = binding.fabEditRecipe;

        binding.rvScrollView.setOnScrollChangeListener(scrollListener);

        RecyclerView instructionsRecycler = binding.rvInstructionsRecycler;
        RecyclerView ingredientsRecycler = binding.rvIngredientsRecycler;

        instructionsAdapter = new RecipeViewAdapter(new ArrayList<>());
        instructionsRecycler.setAdapter(instructionsAdapter);
        instructionsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        ingredientsAdapter = new RecipeViewAdapter(new ArrayList<>());
        ingredientsRecycler.setAdapter(ingredientsAdapter);
        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        recipeRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (recipe != null && recipeId != -1 && fromUser) {
                recipe.setRating(String.valueOf(rating));
                updateRecipe();
            }
        });

        recipeImage.setOnClickListener(v -> {
            if (getActivity() != null && recipe != null && recipe.getImage() != null) {
                ImageViewDialog dialog = new ImageViewDialog(getActivity(), recipe.getImage());
                dialog.showDialog();
            }
        });

        floatingActionButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                RecipeEditFragment fragment = new RecipeEditFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.INTENT_RECIPE_ID, recipe.getId());
                fragment.setArguments(bundle);
                final FragmentManager fm = getActivity().getSupportFragmentManager();
                final FragmentTransaction ft = fm.beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(android.R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        if (recipeDB != null && recipeId != -1) {
            AppExecutors.getInstance().diskIO().execute(() -> {
                RecipesModel recipe = recipeDB.recipeDao().loadRecipeById(recipeId);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> updateUIWithData(recipe));
                }
            });
        } else {
            showErrorAlert();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        forceScreenOn();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        clearForceScreenOn();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((RecipeActivity) getActivity()).setActionBarTitle(R.string.recipes_action_title);
        }
    }

    private void updateUIWithData(RecipesModel recipe) {
        String name = recipe.getName();
        String rating = recipe.getRating();
        Long time = recipe.getTime();
        Integer difficulty = recipe.getDifficulty();
        String created = recipe.getCreated();
        String modified = recipe.getModified();
        String ingredients = recipe.getIngredients();
        String instructions = recipe.getInstructions();
        String notes = recipe.getNotes();
        String image = recipe.getImage();

        this.recipe = recipe;

        recipeName.setText(name);

        if (image != null) loadRecipeImage(Uri.parse(image));
        if (rating != null) recipeRating.setRating(Float.parseFloat(rating));
        if (time != null) recipeTime.setText(TimeUtils.parseRecipeTime(time));
        if (difficulty != null) recipeDifficulty.setText(StringUtils.getDifficultyText(difficulty));
        if (created != null) recipeCreated.setText(created);
        if (modified != null) recipeModified.setText(modified);

        if (ingredients != null && !ingredients.isEmpty()) {
            Type collectionType = new TypeToken<List<RecipeItems>>(){}.getType();
            List<RecipeItems> list  = new Gson().fromJson(ingredients, collectionType);
            ingredientsAdapter.setRecipeItems(list);
        } else {
            ingredientsContainer.setVisibility(View.GONE);
        }

        if (instructions != null && !instructions.isEmpty()) {
            Type collectionType = new TypeToken<List<RecipeItems>>(){}.getType();
            List<RecipeItems> list  = new Gson().fromJson(instructions, collectionType);
            instructionsAdapter.setRecipeItems(list);
        } else {
            instructionsContainer.setVisibility(View.GONE);
        }

        if (notes != null && !notes.isEmpty()) {
            recipeNotes.setText(notes);
        } else {
            notesContainer.setVisibility(View.GONE);
        }
    }

    private void forceScreenOn() {
        if (getActivity() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void clearForceScreenOn() {
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void updateRecipe() {
        if (recipeDB !=  null) {
            AppExecutors.getInstance().diskIO().execute(() ->
                    recipeDB.recipeDao().update(recipe));
        }
    }

    private void loadRecipeImage(Uri uri) {
        Glide.with(this)
                .load(uri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_recipe_placeholder)
                .error(R.drawable.ic_recipe_placeholder_error)
                .into(recipeImage);
    }

    private void showErrorAlert() {
        if (getActivity() != null) {
            Alerter.create(getActivity())
                    .setText(R.string.recipes_load_error)
                    .setIcon(R.drawable.ic_error)
                    .setBackgroundColorRes(R.color.colorAccentRed)
                    .enableSwipeToDismiss()
                    .setTextAppearance(R.style.Text14Aller)
                    .enableInfiniteDuration(true)
                    .setIconSize(R.dimen.alerter_icon_size_small)
                    .setOnHideListener(() -> getActivity().onBackPressed())
                    .show();
        }
    }

    private final OnScrollChangeListener scrollListener = (v, scrollX, scrollY,
                                                           oldScrollX, oldScrollY) -> {
        if (scrollY < oldScrollY) {
            if (!floatingActionButton.isShown()) {
                floatingActionButton.show();
            }
        }

        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
            if (floatingActionButton.isShown()) {
                floatingActionButton.hide();
            }
        }
    };
}
