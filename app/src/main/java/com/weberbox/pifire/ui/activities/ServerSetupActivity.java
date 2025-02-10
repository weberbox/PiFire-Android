package com.weberbox.pifire.ui.activities;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.aceinteract.android.stepper.StepperNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.ActivityServerSetupBinding;
import com.weberbox.pifire.model.view.SetupViewModel;
import com.weberbox.pifire.ui.utils.AnimUtils;

public class ServerSetupActivity extends AppCompatActivity {

    private StepperNavigationView stepper;
    private AppBarConfiguration appBarConfiguration;
    private ActivityServerSetupBinding binding;
    private FloatingActionButton setupFab;
    private ProgressBar connectProgress;
    private int downX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityServerSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.setupToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(0, topInset, 0, bottomInset);
            return WindowInsetsCompat.CONSUMED;
        });

        getOnBackPressedDispatcher().addCallback(this, onBackCallback);

        setupFab = binding.fabSetup;
        connectProgress = binding.setupLayout.connectProgressbar;
        stepper = binding.setupLayout.setupStepper;

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_setup_welcome,
                R.id.nav_setup_address,
                R.id.nav_setup_push,
                R.id.nav_setup_finish)
                .build();

        NavController navController = Navigation.findNavController(this,
                R.id.server_setup_fragment);
        stepper.setupWithNavController(navController);

        NavigationUI.setupActionBarWithNavController(this, navController);

        SetupViewModel setupViewModel = new ViewModelProvider(this).get(SetupViewModel.class);

        setupFab.setOnClickListener(v -> setupViewModel.fabOnClick());

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_setup_scan_qr) {
                AnimUtils.rotateFabBackwards(setupFab);
            } else {
                AnimUtils.rotateFabForwards(setupFab);
            }
        });

    }

    public ProgressBar getProgressBar() {
        return connectProgress;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this,
                R.id.server_setup_fragment), appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private final OnBackPressedCallback onBackCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (stepper.getCurrentStep() == 0) {
                finish();
            } else {
                onSupportNavigateUp();
            }
        }
    };

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

