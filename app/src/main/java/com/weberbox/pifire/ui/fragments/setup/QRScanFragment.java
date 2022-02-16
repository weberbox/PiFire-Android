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

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.FragmentSetupQrScanBinding;
import com.weberbox.pifire.model.view.SetupViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QRScanFragment extends Fragment {

    private FragmentSetupQrScanBinding binding;
    private CompoundBarcodeView barcodeView;
    private SetupViewModel setupViewModel;
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSetupQrScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        barcodeView = binding.barcodeScanner;
        barcodeView.getBarcodeView().getCameraSettings().setAutoFocusEnabled(true);
        barcodeView.getBarcodeView().getCameraSettings().setContinuousFocusEnabled(true);
        barcodeView.setStatusText(null);
        barcodeView.decodeContinuous(barCodeCallback);

        setupViewModel = new ViewModelProvider(requireActivity()).get(SetupViewModel.class);
        setupViewModel.getFabEvent().observe(getViewLifecycleOwner(), unused -> navigateBack());
    }

    @Override
    public void onResume() {
        barcodeView.resume();
        forceScreenOn();
        super.onResume();
    }

    @Override
    public void onPause() {
        barcodeView.pause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearForceScreenOn();
        binding = null;
    }

    private void navigateBack() {
        navController.popBackStack(R.id.nav_setup_address, true);
        navController.navigate(R.id.nav_setup_address);
    }

    private final BarcodeCallback barCodeCallback = new BarcodeCallback() {

        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                setupViewModel.setQRData(result.getText());
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
