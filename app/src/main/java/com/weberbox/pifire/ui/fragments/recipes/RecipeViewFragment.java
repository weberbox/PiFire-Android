package com.weberbox.pifire.ui.fragments.recipes;

import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.core.widget.NestedScrollView.OnScrollChangeListener;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.skydoves.androidveil.VeilLayout;
import com.skydoves.androidveil.VeilRecyclerFrameView;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.FragmentRecipeViewBinding;
import com.weberbox.pifire.model.remote.RecipesModel;
import com.weberbox.pifire.model.remote.RecipesModel.Asset;
import com.weberbox.pifire.model.remote.RecipesModel.Details;
import com.weberbox.pifire.model.remote.RecipesModel.MetaData;
import com.weberbox.pifire.model.remote.RecipesModel.Recipe;
import com.weberbox.pifire.model.remote.RecipesModel.RecipeDetails;
import com.weberbox.pifire.model.view.RecipesViewModel;
import com.weberbox.pifire.record.RecipeImageRecord;
import com.weberbox.pifire.recycler.adapter.RecipeImageAdapter;
import com.weberbox.pifire.recycler.adapter.RecipeImageAdapter.OnItemClickListener;
import com.weberbox.pifire.recycler.adapter.RecipeIngredientsAdapter;
import com.weberbox.pifire.recycler.adapter.RecipeInstructionsAdapter;
import com.weberbox.pifire.recycler.adapter.RecipeStepsAdapter;
import com.weberbox.pifire.recycler.manager.ScrollDisableLayoutManager;
import com.weberbox.pifire.ui.dialogs.BottomButtonDialog;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.RecipeExportUtils;
import com.weberbox.pifire.utils.TimeUtils;

import java.util.ArrayList;

import dev.chrisbanes.insetter.Insetter;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

@SuppressWarnings("unused")
public class RecipeViewFragment extends Fragment {

    private FragmentRecipeViewBinding binding;
    private RecipesViewModel recipesViewModel;
    private RecipeImageAdapter imagesAdapter;
    private RecipeIngredientsAdapter ingredientsAdapter;
    private RecipeInstructionsAdapter instructionsAdapter;
    private RecipeStepsAdapter recipeStepsAdapter;
    private ExtendedFloatingActionButton fabActions;
    private FloatingActionButton fabRunRecipe, fabPrintRecipe, fabShareRecipe, fabDeleteRecipe;
    private LinearLayout ingredientsContainer, instructionsContainer, notesContainer;
    private MaterialRatingBar recipeRating;
    private ConstraintLayout imagesRecyclerHolder;
    private CircularProgressIndicator progressIndicator;
    private VeilLayout recipeAboutVeil, recipeDescriptionVeil;
    private VeilRecyclerFrameView recipeIngredientsVeil, recipeInstructionsVeil, recipeStepsVeil;
    private TextView recipeAuthor, recipePrepTime, recipeCookTime, recipeDifficulty;
    private TextView recipeProbes, recipeDescription;
    private CardView recipeDescriptionHolder, recipeIngredientsHolder, recipeInstructionsHolder;
    private CardView recipeStepsHolder;
    private NestedScrollView scrollView;
    private WebView webViewReference;
    private View fabClickCatcher;
    private String recipeFilename;
    private Details recipeDetails;
    private int currentStep;

    private boolean fabClicked = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            recipeFilename = bundle.getString(Constants.INTENT_RECIPE_FILENAME, "");
            currentStep = bundle.getInt(Constants.INTENT_RECIPE_STEP, -1);
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackCallback);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipesViewModel = new ViewModelProvider(requireActivity()).get(RecipesViewModel.class);

        scrollView = binding.recipesScrollView;
        recipeAuthor = binding.recipesAboutCv.recipesAuthor;
        recipeRating = binding.recipesAboutCv.recipesRating;
        recipePrepTime = binding.recipesAboutCv.recipesPrepTime;
        recipeCookTime = binding.recipesAboutCv.recipesCookTime;
        recipeDifficulty = binding.recipesAboutCv.recipesDifficulty;
        recipeProbes = binding.recipesAboutCv.recipesProbes;
        recipeDescription = binding.recipesDescriptionCv.recipesDescription;
        recipeDescriptionHolder = binding.recipesDescriptionCv.getRoot();
        recipeIngredientsHolder = binding.recipesIngredientsCv.getRoot();
        recipeInstructionsHolder = binding.recipesInstructionsCv.getRoot();
        recipeStepsHolder = binding.recipesStepsCv.getRoot();
        imagesRecyclerHolder = binding.rvRecyclerContainer;
        fabActions = binding.recipeViewFab.fabRecipeActions;
        fabRunRecipe = binding.recipeViewFab.fabRunRecipe;
        fabDeleteRecipe = binding.recipeViewFab.fabDeleteRecipe;
        fabPrintRecipe = binding.recipeViewFab.fabPrintRecipe;
        fabShareRecipe = binding.recipeViewFab.fabShareRecipe;
        fabClickCatcher = binding.fabClickCatcher;
        progressIndicator = binding.recipeImageLoading;
        recipeAboutVeil = binding.recipesAboutCv.recipesAboutHolder;
        recipeDescriptionVeil = binding.recipesDescriptionCv.recipesDescriptionHolder;
        recipeIngredientsVeil = binding.recipesIngredientsCv.recipesIngredientsRv;
        recipeInstructionsVeil = binding.recipesInstructionsCv.recipesInstructionsRv;
        recipeStepsVeil = binding.recipesStepsCv.recipesStepsRecycler;

        Insetter.builder()
                .padding(WindowInsetsCompat.Type.navigationBars())
                .applyToView(binding.rvMainContainer);
        Insetter.builder()
                .margin(WindowInsetsCompat.Type.systemBars())
                .applyToView(binding.recipeViewFab.getRoot());

        fabActions.shrink();

        binding.recipesScrollView.setOnScrollChangeListener(scrollListener);

        RecyclerView imagesRecycler = binding.recipesImageRecycler;
        VeilRecyclerFrameView ingredientsRv = recipeIngredientsVeil;
        VeilRecyclerFrameView instructionsRv = recipeInstructionsVeil;
        VeilRecyclerFrameView stepsRv = recipeStepsVeil;

        imagesAdapter = new RecipeImageAdapter(this, onItemClickListener);
        imagesRecycler.setAdapter(imagesAdapter);

        ingredientsAdapter = new RecipeIngredientsAdapter(true);
        ingredientsRv.setAdapter(ingredientsAdapter);
        ingredientsRv.setLayoutManager(new ScrollDisableLayoutManager(requireActivity()));

        instructionsAdapter = new RecipeInstructionsAdapter(true);
        instructionsRv.setAdapter(instructionsAdapter);
        instructionsRv.setLayoutManager(new ScrollDisableLayoutManager(requireActivity()));

        recipeStepsAdapter = new RecipeStepsAdapter();
        stepsRv.setAdapter(recipeStepsAdapter);
        stepsRv.setLayoutManager(new ScrollDisableLayoutManager(requireActivity()));

        recipeRating.setIsIndicator(true);

        fabActions.setOnClickListener(v -> fabActionsClicked());

        fabRunRecipe.setOnClickListener(v -> {
            fabActionsClicked();
            if (recipeFilename != null) {
                recipesViewModel.setOnRunRecipe(recipeFilename);
            }
        });

        fabDeleteRecipe.setOnClickListener(v -> {
            fabActionsClicked();
            BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                    .setAutoDismiss(true)
                    .setTitle(getString(R.string.dialog_confirm_action))
                    .setMessage(getString(R.string.pellets_delete_content))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, which) -> {
                    })
                    .setPositiveButtonWithColor(getString(R.string.delete),
                            R.color.dialog_positive_button_color_red, (dialogInterface, which) -> {
                                requireActivity().getOnBackPressedDispatcher().onBackPressed();
                                if (recipeFilename != null) {
                                    recipesViewModel.setOnRecipeDelete(recipeFilename);
                                    recipesViewModel.retrieveRecipes();
                                }
                            })
                    .build();
            dialog.show();
        });

        fabPrintRecipe.setOnClickListener(v -> {
            fabActionsClicked();
            if (recipeFilename != null) {
                printRecipe();
            }
        });

        fabShareRecipe.setOnClickListener(v -> {
            fabActionsClicked();
            if (recipeFilename != null) {
                shareRecipe();
            }
        });

        fabClickCatcher.setOnClickListener(v -> fabActionsClicked());

        if (getActivity() != null) {
            RecipesViewModel recipesViewModel = new ViewModelProvider(getActivity())
                    .get(RecipesViewModel.class);
            recipesViewModel.getRecipesData().observe(getViewLifecycleOwner(), recipesData -> {
                if (recipesData != null) {
                    updateUIWithData(recipesData);
                }
            });
        }

        if (recipeFilename == null) {
            Alerter.create(requireActivity())
                    .setText(getString(R.string.recipes_filename_error))
                    .setIcon(R.drawable.ic_error)
                    .setBackgroundColorRes(R.color.colorAccentRed)
                    .setTextAppearance(R.style.Text14AllerBold)
                    .setDismissable(false)
                    .enableInfiniteDuration(true)
                    .setIconSize(R.dimen.alerter_icon_size_small)
                    .addButton(getString(R.string.exit), R.style.AlerterButton, v -> {
                        requireActivity().getOnBackPressedDispatcher().onBackPressed();
                        Alerter.hide();
                    })
                    .show();
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

    private void updateUIWithData(RecipesModel recipesModel) {
        for (RecipeDetails details : recipesModel.getRecipeDetails()) {
            if (details.getFilename().equalsIgnoreCase(recipeFilename)) {
                recipeDetails = details.getDetails();
                Recipe recipe = details.getDetails().getRecipe();
                MetaData metaData = details.getDetails().getMetadata();
                String description = metaData.getDescription();
                int time = metaData.getCookTime();

                recipesViewModel.setToolbarTitle(metaData.getTitle());

                ArrayList<RecipeImageRecord> imagesList = orderImageRecords(details, metaData);

                if (imagesList.isEmpty()) {
                    imagesRecyclerHolder.setVisibility(View.GONE);
                } else {
                    imagesAdapter.setRecipeImages(imagesList);
                    progressIndicator.setVisibility(View.GONE);
                }

                if (recipe.getIngredients().isEmpty()) {
                    recipeIngredientsHolder.setVisibility(View.GONE);
                } else {
                    ingredientsAdapter.setRecipeIngredients(recipe.getIngredients());
                }

                if (recipe.getInstructions().isEmpty()) {
                    recipeInstructionsHolder.setVisibility(View.GONE);
                } else {
                    instructionsAdapter.setRecipeInstructions(recipe.getInstructions());
                }

                recipeStepsAdapter.setRecipeSteps(recipe.getSteps(), metaData.getUnits());

                if (!metaData.getAuthor().isBlank()) recipeAuthor.setText(metaData.getAuthor());
                recipeRating.setRating((float) metaData.getRating());
                recipePrepTime.setText(TimeUtils.formatMinutes(metaData.getPrepTime()));
                recipeCookTime.setText(TimeUtils.formatMinutes(metaData.getCookTime()));
                recipeDifficulty.setText(metaData.getDifficulty());
                recipeProbes.setText(String.valueOf(metaData.getFoodProbes()));
                if (metaData.getDescription().isBlank()) {
                    recipeDescriptionHolder.setVisibility(View.GONE);
                } else {
                    recipeDescription.setText(metaData.getDescription());
                }

                if (currentStep != -1) {
                    recipeStepsAdapter.setStepSelected(currentStep);
                    scrollView.postDelayed(() -> {
                        int targetViewY = recipeStepsHolder.getTop();
                        scrollView.smoothScrollTo(0, targetViewY);
                    }, 500);
                }

                recipeAboutVeil.unVeil();
                recipeDescriptionVeil.unVeil();
                recipeIngredientsVeil.unVeil();
                recipeInstructionsVeil.unVeil();
                recipeStepsVeil.unVeil();

            }
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
                    .setOnHideListener(() ->
                            getActivity().getOnBackPressedDispatcher().onBackPressed())
                    .show();
        }
    }

    private void showSuccessfulDeleteAlerter() {
        if (getActivity() != null) {
            requireActivity().runOnUiThread(() ->
                    Alerter.create(getActivity())
                            .setText(R.string.recipes_removed)
                            .setIcon(R.drawable.ic_delete)
                            .setBackgroundColorRes(R.color.colorPrimaryLighter)
                            .setDuration(3000)
                            .setTextAppearance(R.style.Text14AllerBold)
                            .setIconSize(R.dimen.alerter_icon_size_small)
                            .show());
        }
    }

    private void showFailedAlerter(String error) {
        if (getActivity() != null) {
            requireActivity().runOnUiThread(() ->
                    AlertUtils.createErrorAlert(getActivity(), error, true));
        }
    }

    private void printRecipe() {
        WebView webView = new WebView(requireActivity());
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

        RecipeExportUtils exportUtils = new RecipeExportUtils(requireActivity(), recipeDetails);

        webView.getSettings().setAllowFileAccess(true);
        webView.loadDataWithBaseURL("file:///android_asset",
                exportUtils.getRecipeHTML(), "text/html; charset=utf-8",
                "UTF-8", "");

        webViewReference = webView;
    }

    private void createWebPrintJob(WebView webView) {
        PrintManager printManager = (PrintManager) requireActivity()
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = recipeFilename;

        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());

    }

    private void shareRecipe() {
        RecipeExportUtils exportUtils = new RecipeExportUtils(requireActivity(), recipeDetails);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, exportUtils.getRecipeString());
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void fabActionsClicked() {
        if (!fabClicked) {
            AnimUtils.fabFromBottomAnim(fabRunRecipe);
            AnimUtils.fabFromBottomAnim(fabPrintRecipe);
            AnimUtils.fabFromBottomAnim(fabShareRecipe);
            AnimUtils.fabFromBottomAnim(fabDeleteRecipe);
            AnimUtils.fadeInAnimation(fabClickCatcher, 300);
            fabClickCatcher.setClickable(true);
            fabActions.extend();
        } else {
            AnimUtils.fabToBottomAnim(fabRunRecipe);
            AnimUtils.fabToBottomAnim(fabPrintRecipe);
            AnimUtils.fabToBottomAnim(fabShareRecipe);
            AnimUtils.fabToBottomAnim(fabDeleteRecipe);
            AnimUtils.fadeOutAnimation(fabClickCatcher, 300);
            fabClickCatcher.setClickable(false);
            fabActions.shrink();
        }
        fabClicked = !fabClicked;
    }

    private final OnItemClickListener onItemClickListener = (imageView, filename, recipeImage) -> {
        Fragment fragment = getFragment(filename, recipeImage);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.fragment_fade_enter,
                        R.animator.fragment_fade_exit,
                        R.animator.fragment_fade_enter,
                        R.animator.fragment_fade_exit)
                .replace(android.R.id.content, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    };

    private final OnBackPressedCallback onBackCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (fabClicked) {
                fabActionsClicked();
            } else {
                this.setEnabled(false);
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        }
    };

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

    @NonNull
    private Fragment getFragment(String filename, String recipeImage) {
        Fragment fragment = new RecipeImageFragment();
        if (!recipeFilename.isBlank() && !recipeImage.isBlank()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.INTENT_RECIPE_FILENAME, filename);
            bundle.putString(Constants.INTENT_RECIPE_IMAGE, recipeImage);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @NonNull
    private ArrayList<RecipeImageRecord> orderImageRecords(RecipeDetails details,
                                                           MetaData metaData) {
        ArrayList<RecipeImageRecord> imagesList = new ArrayList<>();

        for (Asset asset : details.getDetails().getAssets()) {
            if (asset.getFilename().equalsIgnoreCase(metaData.getImage())) {
                imagesList.add(0, new RecipeImageRecord(asset.getEncodedImage(),
                        asset.getFilename(), details.getFilename()));
            } else {
                imagesList.add(new RecipeImageRecord(asset.getEncodedImage(),
                        asset.getFilename(), details.getFilename()));
            }
        }
        return imagesList;
    }

}