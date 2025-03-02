package com.weberbox.pifire.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.MainActivity;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.databinding.ActivityRecipesBinding;
import com.weberbox.pifire.model.local.RecipeActionsModel;
import com.weberbox.pifire.model.remote.ControlDataModel;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.view.RecipesViewModel;
import com.weberbox.pifire.ui.dialogs.MaterialDialogText;
import com.weberbox.pifire.ui.fragments.recipes.RecipeListFragment;
import com.weberbox.pifire.ui.fragments.recipes.RecipeViewFragment;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.HTTPUtils;
import com.weberbox.pifire.utils.NetworkUtils;

import java.io.IOException;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import timber.log.Timber;

public class RecipeActivity extends BaseActivity {

    private ActivityRecipesBinding binding;
    private RecipesViewModel recipesViewModel;
    private int downX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_left,
                    android.R.anim.fade_out);
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.slide_in_right,
                    R.anim.slide_out_right);
        }

        binding = ActivityRecipesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Insetter.builder()
                .padding(WindowInsetsCompat.Type.systemBars(), Side.TOP)
                .applyToView(binding.appBar);

        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recipesViewModel = new ViewModelProvider(this).get(RecipesViewModel.class);

        recipesViewModel.getOnRecipeDelete().observe(this, this::deleteRecipe);
        recipesViewModel.getOnRunRecipe().observe(this, this::runRecipe);
        recipesViewModel.getOnApiCallFailed().observe(this, this::showFailedAlerter);
        recipesViewModel.getOnNoNetwork().observe(this, unused -> showNoNetworkDialog());
        recipesViewModel.getToolbarTitle().observe(this, title -> {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
        });

        Intent intent = getIntent();
        int fragment = intent.getIntExtra(Constants.INTENT_RECIPE_FRAGMENT, 0);
        String recipeFilename = intent.getStringExtra(Constants.INTENT_RECIPE_FILENAME);
        Integer recipeStep = intent.getIntExtra((Constants.INTENT_RECIPE_STEP), -1);

        ViewCompat.setOnApplyWindowInsetsListener(binding.customStatusGuard, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.getLayoutParams().height = insets.top;
            v.setLayoutParams(v.getLayoutParams());
            return windowInsets;
        });

        if (savedInstanceState == null) {
            startRecipeFragment(fragment, recipeFilename, recipeStep);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        // This seems like a hack, not sure how else to fix in yet in API 35+. We setup a custom
        // view above the status bar and set the alpha to 0.0. Then set the height of the view in
        // onCreate to the size of the actual status bar. Then fade the view in and out along with
        // the Action Bar. Otherwise the normal status guard is black and flashes in and out
        binding.customStatusGuard.animate().alpha(1f);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        // Same as above in onActionModeStarted but fade it out.
        binding.customStatusGuard.animate().alpha(0f);
    }

    private void startRecipeFragment(int fragment, String recipeFilename, Integer recipeStep) {
        switch (fragment) {
            case Constants.FRAG_ALL_RECIPES:
                launchFragment(new RecipeListFragment(), recipeFilename, recipeStep);
                break;
            case Constants.FRAG_VIEW_RECIPE:
                launchFragment(new RecipeViewFragment(), recipeFilename, recipeStep);
                break;
            case Constants.FRAG_EDIT_RECIPE:
                // Maybe in the future
                break;
        }
    }

    private void launchFragment(Fragment fragment, String recipeFilename, Integer recipeStep) {
        if (recipeFilename != null && !recipeFilename.isBlank()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.INTENT_RECIPE_FILENAME, recipeFilename);
            if (recipeStep != -1) {
                bundle.putInt(Constants.INTENT_RECIPE_STEP, recipeStep);
            }
            fragment.setArguments(bundle);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void deleteRecipe(String filename) {
        if (NetworkUtils.isNetworkAvailable(getApplication())) {
            String url = Prefs.getString(getString(R.string.prefs_server_address)) +
                    ServerConstants.URL_RECIPE_DELETE;
            String json = new Gson().toJson(new RecipeActionsModel()
                    .withDeleteFile(true)
                    .withFilename(filename));
            HTTPUtils.createHttpPost(this, url, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Timber.d(e, "onFailure");
                    showFailedAlerter(getString(R.string.http_on_failure));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response)
                        throws IOException {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            ServerResponseModel responseModel = ServerResponseModel.parseJSON(
                                    response.body().string());
                            if (responseModel.getResult().equalsIgnoreCase("success")) {
                                showSuccessfulDeleteAlerter();
                                recipesViewModel.retrieveRecipes();
                            } else {
                                Timber.d("Response Error %s", response);
                                showFailedAlerter(getString(R.string.recipes_remove_failed));
                            }
                        } else {
                            showFailedAlerter(getString(R.string.http_response_null));
                        }
                    } else {
                        showFailedAlerter(getString(R.string.http_unsuccessful,
                                String.valueOf(response.code()),
                                HTTPUtils.getReasonPhrase(response.code())));
                    }
                    response.close();
                }
            });
        } else {
            showNoNetworkDialog();
        }
    }

    private void runRecipe(String filename) {
        if (NetworkUtils.isNetworkAvailable(getApplication())) {
            String url = Prefs.getString(getString(R.string.prefs_server_address)) +
                    ServerConstants.URL_API_CONTROL;
            String json = new Gson().toJson(new ControlDataModel()
                    .withUpdated(true)
                    .withMode(ServerConstants.G_MODE_RECIPE)
                    .withRecipe(new ControlDataModel.Recipe()
                            .withFileName(ServerConstants.FOLDER_RECIPES + filename)));
            HTTPUtils.createHttpPost(this, url, json, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Timber.d(e, "onFailure");
                    showFailedAlerter(getString(R.string.http_on_failure));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response)
                        throws IOException {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            ServerResponseModel responseModel = ServerResponseModel.parseJSON(
                                    response.body().string());
                            if (responseModel.getResult().equalsIgnoreCase("success")) {
                                Intent intent = new Intent(RecipeActivity.this,
                                        MainActivity.class);
                                intent.putExtra(Constants.INTENT_SCROLL_DASH, true);
                                startActivity(intent);
                                finish();
                            } else {
                                Timber.d("Response Error %s", response);
                                showFailedAlerter(getString(R.string.recipes_run_failed));
                            }
                        } else {
                            showFailedAlerter(getString(R.string.http_response_null));
                        }
                    } else {
                        showFailedAlerter(getString(R.string.http_unsuccessful,
                                String.valueOf(response.code()),
                                HTTPUtils.getReasonPhrase(response.code())));
                    }
                    response.close();
                }
            });
        } else {
            showNoNetworkDialog();
        }
    }

    private void showSuccessfulDeleteAlerter() {
        this.runOnUiThread(() ->
                Alerter.create(this)
                        .setText(R.string.recipes_removed)
                        .setIcon(R.drawable.ic_delete)
                        .setBackgroundColorRes(R.color.colorPrimaryLighter)
                        .setDuration(3000)
                        .setTextAppearance(R.style.Text14AllerBold)
                        .setIconSize(R.dimen.alerter_icon_size_small)
                        .show());
    }

    private void showFailedAlerter(String error) {
        this.runOnUiThread(() ->
                AlertUtils.createErrorAlert(this, error, 5000));
    }

    private void showNoNetworkDialog() {
        runOnUiThread(() -> {
            MaterialDialogText dialog = new MaterialDialogText.Builder(this)
                    .setTitle(getString(R.string.dialog_no_network_title))
                    .setMessage(getString(R.string.dialog_dialog_no_network_title_message))
                    .setPositiveButton(getString(android.R.string.ok),
                            (dialogInterface, which) -> {
                                finish();
                                dialogInterface.dismiss();
                            })
                    .build();
            dialog.show();
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) event.getRawX();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                if (Math.abs(downX - x) > 5) {
                    return super.dispatchTouchEvent(event);
                }
                final int reducePx = 25;
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                outRect.inset(reducePx, reducePx);
                if (!outRect.contains(x, y)) {
                    v.clearFocus();
                    boolean touchTargetIsEditText = false;
                    for (View vi : v.getRootView().getTouchables()) {
                        if (vi instanceof EditText) {
                            Rect clickedViewRect = new Rect();
                            vi.getGlobalVisibleRect(clickedViewRect);
                            clickedViewRect.inset(reducePx, reducePx);
                            if (clickedViewRect.contains(x, y)) {
                                touchTargetIsEditText = true;
                                break;
                            }
                        }
                    }
                    if (!touchTargetIsEditText) {
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
