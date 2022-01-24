package com.weberbox.pifire.ui.activities;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.aceinteract.android.stepper.StepperNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.databinding.ActivityServerSetupBinding;
import com.weberbox.pifire.model.view.SetupViewModel;

public class ServerSetupActivity extends AppCompatActivity {

    private StepperNavigationView mStepper;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityServerSetupBinding mBinding;
    private int mDownX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityServerSetupBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setSupportActionBar(mBinding.setupToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton setupFab = mBinding.fabSetup;

        mStepper = mBinding.setupLayout.setupStepper;

        if (AppConfig.USE_ONESIGNAL) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_setup_welcome,
                    R.id.nav_setup_address,
                    R.id.nav_setup_push,
                    R.id.nav_setup_finish)
                    .build();
        } else {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_setup_welcome,
                    R.id.nav_setup_address,
                    R.id.nav_setup_finish)
                    .build();
        }

        NavController navController = Navigation.findNavController(this,
                R.id.server_setup_fragment);
        mStepper.setupWithNavController(navController);

        NavigationUI.setupActionBarWithNavController(this, navController);

        SetupViewModel mViewModel = new ViewModelProvider(this).get(SetupViewModel.class);

        mViewModel.setFab(setupFab);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }

    @Override
    public void onBackPressed() {
        if (mStepper.getCurrentStep() == 0) {
            super.onBackPressed();
        } else {
            onSupportNavigateUp();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this,
                R.id.server_setup_fragment), mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = (int) event.getRawX();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                if (Math.abs(mDownX - x) > 5) {
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

