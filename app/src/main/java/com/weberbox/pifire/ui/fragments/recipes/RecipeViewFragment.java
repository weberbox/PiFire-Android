package com.weberbox.pifire.ui.fragments.recipes;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView.OnScrollChangeListener;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.database.RecipeDatabase;
import com.weberbox.pifire.databinding.FragmentRecipeViewBinding;
import com.weberbox.pifire.model.local.RecipesModel;
import com.weberbox.pifire.model.local.RecipesModel.RecipeItems;
import com.weberbox.pifire.recycler.adapter.RecipeViewAdapter;
import com.weberbox.pifire.recycler.manager.ScrollDisableLayoutManager;
import com.weberbox.pifire.ui.activities.RecipeActivity;
import com.weberbox.pifire.ui.dialogs.ImageViewDialog;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.ImageTransition;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.utils.RecipeExportUtils;
import com.weberbox.pifire.utils.StringUtils;
import com.weberbox.pifire.utils.TimeUtils;
import com.weberbox.pifire.utils.executors.AppExecutors;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class RecipeViewFragment extends Fragment {

    private FragmentRecipeViewBinding binding;
    private RecipeViewAdapter instructionsAdapter, ingredientsAdapter;
    private RecipeDatabase recipeDB;
    private RecipesModel recipe;
    private ExtendedFloatingActionButton fabActions;
    private FloatingActionButton fabEdit, fabPrint, fabShare;
    private LinearLayout ingredientsContainer, instructionsContainer, notesContainer;
    private RatingBar recipeRating;
    private TextView recipeName, recipeTime, recipeDifficulty, recipeCreated, recipeModified;
    private TextView recipeNotes;
    private ImageView recipeImage;
    private WebView webViewReference;
    private View fabClickCatcher;
    private int recipeId;

    private boolean fabClicked = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            recipeId = bundle.getInt(Constants.INTENT_RECIPE_ID, -1);
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackCallback);

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
        fabActions = binding.recipeViewFab.fabRecipeActions;
        fabEdit = binding.recipeViewFab.fabEditRecipe;
        fabPrint = binding.recipeViewFab.fabPrintRecipe;
        fabShare = binding.recipeViewFab.fabShareRecipe;
        fabClickCatcher = binding.fabClickCatcher;

        fabActions.shrink();

        binding.rvScrollView.setOnScrollChangeListener(scrollListener);

        RecyclerView instructionsRecycler = binding.rvInstructionsRecycler;
        RecyclerView ingredientsRecycler = binding.rvIngredientsRecycler;

        instructionsAdapter = new RecipeViewAdapter(new ArrayList<>());
        instructionsRecycler.setAdapter(instructionsAdapter);
        instructionsRecycler.setLayoutManager(new ScrollDisableLayoutManager(requireActivity()));

        ingredientsAdapter = new RecipeViewAdapter(new ArrayList<>());
        ingredientsRecycler.setAdapter(ingredientsAdapter);
        ingredientsRecycler.setLayoutManager(new ScrollDisableLayoutManager(requireActivity()));

        recipeRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (recipe != null && recipeId != -1 && fromUser) {
                recipe.setRating(rating);
                updateRecipe();
            }
        });

        recipeImage.setOnClickListener(v -> {
            if (getActivity() != null && recipe != null && recipe.getImage() != null) {
                ImageViewDialog dialog = new ImageViewDialog(getActivity(), recipe.getImage());
                dialog.showDialog();
            }
        });

        fabActions.setOnClickListener(v -> fabActionsClicked());

        fabEdit.setOnClickListener(v -> {
            fabActions.addOnShrinkAnimationListener(listener);
            fabActionsClicked();
        });

        fabPrint.setOnClickListener(v -> {
            fabActionsClicked();
            if (recipe != null && recipe.getId() != -1) {
                printRecipe();
            }
        });

        fabShare.setOnClickListener(v -> {
            fabActionsClicked();
            if (recipe != null && recipe.getId() != -1) {
                shareRecipe();
            }
        });

        fabClickCatcher.setOnClickListener(v -> fabActionsClicked());

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
            ((RecipeActivity) getActivity()).setActionBarTitle(R.string.recipes_action_view);
        }
    }

    private void openRecipeEditFragment() {
        RecipeEditFragment fragment = new RecipeEditFragment();
        fragment.setSharedElementEnterTransition(new ImageTransition());
        fragment.setEnterTransition(new AutoTransition());
        setExitTransition(new AutoTransition());
        fragment.setSharedElementReturnTransition(new ImageTransition());

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.INTENT_RECIPE_ID, recipe.getId());
        fragment.setArguments(bundle);

        final FragmentManager fm = requireActivity().getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .addSharedElement(recipeImage, "recipe_image")
                .commit();
    }

    private void updateUIWithData(RecipesModel recipe) {
        String name = recipe.getName();
        Float rating = recipe.getRating();
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

        loadRecipeImage(image);

        if (rating != null) recipeRating.setRating(rating);
        if (time != null) recipeTime.setText(TimeUtils.parseRecipeTime(time));
        if (difficulty != null) recipeDifficulty.setText(StringUtils.getDifficultyText(difficulty));
        if (created != null) recipeCreated.setText(created);
        if (modified != null) recipeModified.setText(modified);

        if (ingredients != null && !ingredients.isEmpty()) {
            Type collectionType = new TypeToken<List<RecipeItems>>() {
            }.getType();
            List<RecipeItems> list = new Gson().fromJson(ingredients, collectionType);
            ingredientsAdapter.setRecipeItems(list);
        } else {
            ingredientsContainer.setVisibility(View.GONE);
        }

        if (instructions != null && !instructions.isEmpty()) {
            Type collectionType = new TypeToken<List<RecipeItems>>() {
            }.getType();
            List<RecipeItems> list = new Gson().fromJson(instructions, collectionType);
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
        if (recipeDB != null) {
            AppExecutors.getInstance().diskIO().execute(() ->
                    recipeDB.recipeDao().update(recipe));
        }
    }

    private void loadRecipeImage(String uri) {
        Glide.with(this)
                .load(uri != null ? Uri.parse(uri) : R.drawable.ic_recipe_placeholder)
                .placeholder(R.drawable.ic_recipe_placeholder)
                .error(R.drawable.ic_recipe_placeholder_error)
                .transform(new RoundedCorners(ViewUtils.dpToPx(10)))
                .transition(DrawableTransitionOptions.withCrossFade())
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

    private final OnBackPressedCallback onBackCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (fabClicked) {
                fabActionsClicked();
            } else {
                this.setEnabled(false);
                requireActivity().onBackPressed();
            }
        }
    };

    private void printRecipe() {
        WebView webView = new WebView(getActivity());
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                createWebPrintJob(view);
                webViewReference = null;
            }
        });

        RecipeExportUtils recipeExportUtils = new RecipeExportUtils(requireActivity(), recipe);

        webView.getSettings().setAllowFileAccess(true);
        webView.loadDataWithBaseURL("file:///android_asset",
                recipeExportUtils.getRecipeHTML(), "text/html; charset=utf-8",
                "UTF-8", "");

        webViewReference = webView;
    }

    private void createWebPrintJob(WebView webView) {
        PrintManager printManager = (PrintManager) requireActivity()
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = recipe.getName();

        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());

    }

    private void shareRecipe() {
        RecipeExportUtils recipeExportUtils = new RecipeExportUtils(requireActivity(), recipe);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, recipeExportUtils.getRecipeString());
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void fabActionsClicked() {
        if (!fabClicked) {
            AnimUtils.fabFromBottomAnim(fabEdit);
            AnimUtils.fabFromBottomAnim(fabPrint);
            AnimUtils.fabFromBottomAnim(fabShare);
            AnimUtils.fadeInAnimation(fabClickCatcher, 300);
            fabClickCatcher.setClickable(true);
            fabActions.extend();
        } else {
            AnimUtils.fabToBottomAnim(fabEdit);
            AnimUtils.fabToBottomAnim(fabPrint);
            AnimUtils.fabToBottomAnim(fabShare);
            AnimUtils.fadeOutAnimation(fabClickCatcher, 300);
            fabClickCatcher.setClickable(false);
            fabActions.shrink();
        }
        fabClicked = !fabClicked;
    }

    private final OnScrollChangeListener scrollListener = (v, scrollX, scrollY, oldScrollX,
                                                           oldScrollY) -> {
        if (scrollY < oldScrollY) {
            if (!fabActions.isShown()) {
                AnimUtils.fabShowAnimation(fabActions);
            }
        }

        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
            if (fabActions.isShown()) {
                AnimUtils.fabHideAnimation(fabActions);
                if (fabClicked) {
                    fabActionsClicked();
                }
            }
        }
    };

    private final Animator.AnimatorListener listener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            fabActions.removeOnShrinkAnimationListener(listener);
            openRecipeEditFragment();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };
}
