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
import com.weberbox.pifire.database.AppExecutors;
import com.weberbox.pifire.database.RecipeDatabase;
import com.weberbox.pifire.databinding.FragmentRecipeViewBinding;
import com.weberbox.pifire.model.local.RecipesModel;
import com.weberbox.pifire.model.local.RecipesModel.RecipeItems;
import com.weberbox.pifire.recycler.adapter.RecipeViewAdapter;
import com.weberbox.pifire.ui.activities.RecipeActivity;
import com.weberbox.pifire.ui.dialogs.ImageViewDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecipeViewFragment extends Fragment {

    private FragmentRecipeViewBinding mBinding;
    private RecipeViewAdapter mInstructionsAdapter;
    private RecipeViewAdapter mIngredientsAdapter;
    private RecipeDatabase mDb;
    private RecipesModel mRecipe;
    private FloatingActionButton mFloatingActionButton;
    private LinearLayout mIngredientsContainer;
    private LinearLayout mInstructionsContainer;
    private LinearLayout mNotesContainer;
    private RatingBar mRecipeRating;
    private TextView mRecipeName;
    private TextView mRecipeTime;
    private TextView mRecipeDifficulty;
    private TextView mRecipeCreated;
    private TextView mRecipeModified;
    private TextView mRecipeNotes;
    private ImageView mRecipeImage;
    private int mRecipeId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mRecipeId = bundle.getInt(Constants.INTENT_RECIPE_ID, -1);
        }

        if (getActivity() != null && getActivity().getApplicationContext() != null) {
            mDb = RecipeDatabase.getInstance(getActivity().getApplicationContext());
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentRecipeViewBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecipeImage = mBinding.rvImage;
        mRecipeName = mBinding.rvName;
        mRecipeRating = mBinding.rvRating;
        mRecipeTime = mBinding.rvTime;
        mRecipeDifficulty = mBinding.rvDifficulty;
        mRecipeCreated = mBinding.rvCreated;
        mRecipeModified = mBinding.rvModified;
        mIngredientsContainer = mBinding.rvIngredientsContainer;
        mInstructionsContainer = mBinding.rvInstructionsContainer;
        mNotesContainer = mBinding.rvNotesContainer;
        mRecipeNotes = mBinding.rvNotes;

        mFloatingActionButton = mBinding.fabEditRecipe;

        mBinding.rvScrollView.setOnScrollChangeListener(scrollListener);

        RecyclerView instructionsRecycler = mBinding.rvInstructionsRecycler;
        RecyclerView ingredientsRecycler = mBinding.rvIngredientsRecycler;

        mInstructionsAdapter = new RecipeViewAdapter(new ArrayList<>());
        instructionsRecycler.setAdapter(mInstructionsAdapter);
        instructionsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mIngredientsAdapter = new RecipeViewAdapter(new ArrayList<>());
        ingredientsRecycler.setAdapter(mIngredientsAdapter);
        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mRecipeRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (mRecipe != null && mRecipeId != -1 && fromUser) {
                mRecipe.setRating(String.valueOf(rating));
                updateRecipe();
            }
        });

        mRecipeImage.setOnClickListener(v -> {
            if (getActivity() != null && mRecipe != null && mRecipe.getImage() != null) {
                ImageViewDialog dialog = new ImageViewDialog(getActivity(), mRecipe.getImage());
                dialog.showDialog();
            }
        });

        mFloatingActionButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                RecipeEditFragment fragment = new RecipeEditFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.INTENT_RECIPE_ID, mRecipe.getId());
                fragment.setArguments(bundle);
                final FragmentManager fm = getActivity().getSupportFragmentManager();
                final FragmentTransaction ft = fm.beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(android.R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        if (mDb != null && mRecipeId != -1) {
            AppExecutors.getInstance().diskIO().execute(() -> {
                RecipesModel recipe = mDb.recipeDao().loadRecipeById(mRecipeId);
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
        mBinding = null;
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
        String time = recipe.getTime();
        String difficulty = recipe.getDifficulty();
        String created = recipe.getCreated();
        String modified = recipe.getModified();
        String ingredients = recipe.getIngredients();
        String instructions = recipe.getInstructions();
        String notes = recipe.getNotes();
        String image = recipe.getImage();

        mRecipe = recipe;

        mRecipeName.setText(name);

        if (image != null) loadRecipeImage(Uri.parse(image));
        if (rating != null) mRecipeRating.setRating(Float.parseFloat(rating));
        if (time != null) mRecipeTime.setText(time);
        if (difficulty != null) mRecipeDifficulty.setText(difficulty);
        if (created != null) mRecipeCreated.setText(created);
        if (modified != null) mRecipeModified.setText(modified);

        if (ingredients != null && !ingredients.isEmpty()) {
            Type collectionType = new TypeToken<List<RecipeItems>>(){}.getType();
            List<RecipeItems> list  = new Gson().fromJson(ingredients, collectionType);
            mIngredientsAdapter.setRecipeItems(list);
        } else {
            mIngredientsContainer.setVisibility(View.GONE);
        }

        if (instructions != null && !instructions.isEmpty()) {
            Type collectionType = new TypeToken<List<RecipeItems>>(){}.getType();
            List<RecipeItems> list  = new Gson().fromJson(instructions, collectionType);
            mInstructionsAdapter.setRecipeItems(list);
        } else {
            mInstructionsContainer.setVisibility(View.GONE);
        }

        if (notes != null && !notes.isEmpty()) {
            mRecipeNotes.setText(notes);
        } else {
            mNotesContainer.setVisibility(View.GONE);
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
        if (mDb !=  null) {
            AppExecutors.getInstance().diskIO().execute(() ->
                    mDb.recipeDao().update(mRecipe));
        }
    }

    private void loadRecipeImage(Uri uri) {
        Glide.with(this)
                .load(uri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_recipe_placeholder)
                .error(R.drawable.ic_recipe_placeholder_error)
                .into(mRecipeImage);
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
            if (!mFloatingActionButton.isShown()) {
                mFloatingActionButton.show();
            }
        }

        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
            if (mFloatingActionButton.isShown()) {
                mFloatingActionButton.hide();
            }
        }
    };
}
