package com.weberbox.pifire.ui.fragments.setup;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.FragmentSetupQrBinding;

import org.jetbrains.annotations.NotNull;

public class QRSetupFragment extends Fragment {

    private FragmentSetupQrBinding mBinding;
    private Snackbar mCameraPermissionSnack;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentSetupQrBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCameraPermissionSnack = Snackbar.make(view, R.string.setup_qr_camera_denied,
                Snackbar.LENGTH_LONG);

        Button haveQRCodeButton = mBinding.continueWithQrcode;
        haveQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() != null && ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                } else {
                    QRScanFragment qrScanFragment = QRScanFragment.getInstance();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.server_setup_fragment, qrScanFragment, QRScanFragment.TAG)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        Button doNotHaveQRCodeButton = mBinding.continueWithoutQrcode;
        doNotHaveQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    URLSetupFragment urlSetupFragment = URLSetupFragment.getInstance();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.server_setup_fragment, urlSetupFragment, URLSetupFragment.TAG)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted && getActivity() != null) {
                    QRScanFragment qrScanFragment = QRScanFragment.getInstance();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.server_setup_fragment, qrScanFragment, QRScanFragment.TAG)
                            .addToBackStack(null)
                            .commit();
                } else {
                    if(!mCameraPermissionSnack.isShown() && getActivity() != null) {
                        mCameraPermissionSnack.setBackgroundTintList(ColorStateList.valueOf(
                                getActivity().getColor(R.color.colorAccentRed)));
                        mCameraPermissionSnack.setTextColor(getActivity().getColor(R.color.colorWhite));
                        mCameraPermissionSnack.show();
                    }
                }
            });
}
