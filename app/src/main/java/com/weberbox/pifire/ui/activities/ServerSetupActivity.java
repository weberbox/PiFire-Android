package com.weberbox.pifire.ui.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.ActivityServerSetupBinding;

public class ServerSetupActivity extends AppCompatActivity {
    private static final String TAG = ServerSetupActivity.class.getSimpleName();

    private ActivityServerSetupBinding mBinding;

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
}

