package com.weberbox.pifire.ui.fragments.setup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.FragmentSetupQrScanBinding;
import com.weberbox.pifire.model.view.SetupViewModel;
import com.weberbox.pifire.ui.utils.AnimUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QRScanFragment extends Fragment {

    private FragmentSetupQrScanBinding mBinding;
    private CompoundBarcodeView mBarcodeView;
    private SetupViewModel mSetupViewModel;
    private NavController mNavController;
    private FloatingActionButton mSetupFab;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentSetupQrScanBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNavController = Navigation.findNavController(view);

        mBarcodeView = mBinding.barcodeScanner;
        mBarcodeView.getBarcodeView().getCameraSettings().setAutoFocusEnabled(true);
        mBarcodeView.getBarcodeView().getCameraSettings().setContinuousFocusEnabled(true);
        mBarcodeView.setStatusText(null);
        mBarcodeView.decodeContinuous(mBarCodeCallback);

        mSetupViewModel = new ViewModelProvider(requireActivity()).get(SetupViewModel.class);
        mSetupViewModel.getFab().observe(getViewLifecycleOwner(), setupFab -> {
            AnimUtils.rotateFabBackwards(setupFab);
            mSetupFab = setupFab;
            setupFab.setOnClickListener(v -> navigateBack());
        });
    }

    @Override
    public void onResume() {
        mBarcodeView.resume();
        forceScreenOn();
        super.onResume();
    }

    @Override
    public void onPause() {
        mBarcodeView.pause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearForceScreenOn();
        mBinding = null;
        if (mSetupFab != null) {
            AnimUtils.rotateFabForwards(mSetupFab);
        }
    }

    private void navigateBack() {
        mNavController.popBackStack(R.id.nav_setup_address, true);
        mNavController.navigate(R.id.nav_setup_address);
    }

    private final BarcodeCallback mBarCodeCallback = new BarcodeCallback() {

        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                mSetupViewModel.setQRData(result.getText());
               navigateBack();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

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
}
