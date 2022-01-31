package com.weberbox.pifire.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.ui.fragments.recipes.RecipeEditFragment;
import com.weberbox.pifire.ui.fragments.recipes.RecipeViewFragment;

public class RecipeActivity extends BaseActivity {

    private int downX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        int fragment = intent.getIntExtra(Constants.INTENT_RECIPE_FRAGMENT, 0);
        int recipeId = intent.getIntExtra(Constants.INTENT_RECIPE_ID, -1);

        if (savedInstanceState == null) {
            startRecipeFragment(fragment, recipeId);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void startRecipeFragment(int fragment, int recipeId) {
        switch (fragment) {
            case Constants.FRAG_VIEW_RECIPE:
                launchFragment(new RecipeViewFragment(), recipeId);
                break;
            case Constants.FRAG_EDIT_RECIPE:
                launchFragment(new RecipeEditFragment(), recipeId);
                break;
        }
    }

    private void launchFragment(Fragment fragment, int recipeId) {
        if (recipeId != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.INTENT_RECIPE_ID, recipeId);
            fragment.setArguments(bundle);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }

    public void setActionBarTitle(int title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
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
