package com.weberbox.pifire.ui.fragments.setup;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.FragmentSetupBeginBinding;
import com.weberbox.pifire.utils.AlertUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetupBeginFragment extends Fragment {

    private FragmentSetupBeginBinding mBinding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentSetupBeginBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button haveQRCodeButton = mBinding.continueWithQrcode;
        haveQRCodeButton.setOnClickListener(view1 -> requestPermissionCamera());

        Button doNotHaveQRCodeButton = mBinding.continueWithoutQrcode;
        doNotHaveQRCodeButton.setOnClickListener(view12 -> {
            if (getActivity() != null) {
                URLSetupFragment urlSetupFragment = URLSetupFragment.getInstance();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.server_setup_fragment, urlSetupFragment,
                                URLSetupFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void requestPermissionCamera() {
        TedPermission.create()
                .setPermissions(Manifest.permission.CAMERA)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (getActivity() != null) {
                            QRScanFragment qrScanFragment = QRScanFragment.getInstance();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .add(R.id.server_setup_fragment, qrScanFragment,
                                            QRScanFragment.class.getSimpleName())
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        AlertUtils.createErrorAlert(getActivity(), R.string.app_permissions_denied,
                                false);
                    }
                })
                .check();
    }
}
