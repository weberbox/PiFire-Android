package com.weberbox.pifire.ui.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.ActivityServerSetupBinding;

public class ServerSetupActivity extends AppCompatActivity {

    private ActivityServerSetupBinding mBinding;
    private int mDownX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityServerSetupBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setSupportActionBar(mBinding.setupToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.setup_label);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager mgr = getFragmentManager();
        if (mgr.getBackStackEntryCount() > 0) {
            mgr.popBackStack();
        } else {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager mgr = getFragmentManager();
        if (mgr.getBackStackEntryCount() > 0) {
            mgr.popBackStack();
        } else {
            super.onBackPressed();
        }
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

