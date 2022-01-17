package com.weberbox.pifire.ui.activities;

import static androidx.core.content.FileProvider.getUriForFile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.util.List;

public class ImagePickerActivity extends BaseActivity {

    public static String mFileName;
    private int mAspectRatioX = 1;
    private int mAspectRatioY = 1;
    private int mBitmapMaxWidth = 600;
    private int mBitmapMaxHeight = 600;
    private int mImageCompression = 80;

    private boolean mLockAspectRatio = true;
    private boolean mSetMaxWidthHeight = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, R.string.image_picker_no_intent, Toast.LENGTH_LONG).show();
            return;
        }

        mAspectRatioX = intent.getIntExtra(Constants.INTENT_ASPECT_RATIO_X, mAspectRatioX);
        mAspectRatioY = intent.getIntExtra(Constants.INTENT_ASPECT_RATIO_Y, mAspectRatioY);
        mImageCompression = intent.getIntExtra(Constants.INTENT_IMAGE_COMPRESSION_QUALITY, mImageCompression);
        mLockAspectRatio = intent.getBooleanExtra(Constants.INTENT_LOCK_ASPECT_RATIO, mLockAspectRatio);
        mSetMaxWidthHeight = intent.getBooleanExtra(Constants.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, mSetMaxWidthHeight);
        mBitmapMaxWidth = intent.getIntExtra(Constants.INTENT_BITMAP_MAX_WIDTH, mBitmapMaxWidth);
        mBitmapMaxHeight = intent.getIntExtra(Constants.INTENT_BITMAP_MAX_HEIGHT, mBitmapMaxHeight);

        int requestCode = intent.getIntExtra(Constants.INTENT_IMAGE_PICKER_OPTION, -1);
        if (requestCode == Constants.INTENT_REQUEST_IMAGE_CAPTURE) {
            takeCameraImage();
        } else {
            chooseImageFromGallery();
        }
    }

    private void takeCameraImage() {
        TedPermission.create()
                .setPermissions(Manifest.permission.CAMERA)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        mFileName = System.currentTimeMillis() + ".jpg";
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(mFileName));
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            requestImageCapture.launch(takePictureIntent);
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        setResultPermissions();
                    }
                })
                .check();
    }

    private void chooseImageFromGallery() {
        TedPermission.create()
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        mFileName = System.currentTimeMillis() + ".jpg";
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        requestGalleryImage.launch(pickPhoto);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        setResultPermissions();
                    }
                })
                .check();
    }

    private final ActivityResultLauncher<Intent> requestGalleryImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null && result.getResultCode() == Activity.RESULT_OK ) {
                    Intent data = result.getData();
                    if(data != null)  {
                        Uri imageUri = data.getData();
                        cropImage(imageUri);
                    }
                } else {
                    setResultCancelled();
                }
            });

    private final ActivityResultLauncher<Intent> requestImageCapture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null && result.getResultCode() == Activity.RESULT_OK ) {
                    cropImage(getCacheImagePath(mFileName));
                } else {
                    setResultCancelled();
                }
            });

    private final ActivityResultLauncher<Intent> uCropActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult() ,
            result -> {
                if (result != null) {
                    if (result.getResultCode() == RESULT_OK) {
                        handleUCropResult(result.getData());
                    } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                        setResultError();
                    } else {
                        setResultCancelled();
                    }
                }
            });

    private void cropImage(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getDestinationPath(), mFileName));
        uCropActivityResult.launch(getUCropIntent(ImagePickerActivity.this, sourceUri,
                destinationUri, getUCropOptions()));
    }

    public static Intent getUCropIntent(Context context, Uri mediaScannerUri, Uri destination,
                                        UCrop.Options options ) {
        Intent intent = new Intent();
        Bundle uCropBundle = options.getOptionBundle();
        uCropBundle.putParcelable(UCrop.EXTRA_INPUT_URI, mediaScannerUri);
        uCropBundle.putParcelable(UCrop.EXTRA_OUTPUT_URI, destination);

        intent.putExtras(options.getOptionBundle());
        intent.setClass(context , UCropActivity.class);
        return intent;
    }

    public UCrop.Options getUCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(mImageCompression);
        options.setToolbarTitle(getString(R.string.image_picker_toolbar_title));

        if (mLockAspectRatio)
            options.withAspectRatio(mAspectRatioX, mAspectRatioY);

        if (mSetMaxWidthHeight)
            options.withMaxResultSize(mBitmapMaxWidth, mBitmapMaxHeight);
        return options;
    }

    private void handleUCropResult(Intent data) {
        if (data == null) {
            setResultCancelled();
            return;
        }
        final Uri resultUri = UCrop.getOutput(data);
        setResultOk(resultUri);
    }

    private void setResultOk(Uri imagePath) {
        Intent intent = new Intent();
        intent.putExtra("path", imagePath);
        setResult(Activity.RESULT_OK, intent);
        mFileName = null;
        finish();
    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        mFileName = null;
        finish();
    }

    private void setResultPermissions() {
        Intent intent = new Intent();
        setResult(Constants.RESULT_PERMISSIONS, intent);
        mFileName = null;
        finish();
    }

    private void setResultError() {
        Intent intent = new Intent();
        setResult(UCrop.RESULT_ERROR, intent);
        mFileName = null;
        finish();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(ImagePickerActivity.this, getPackageName() +
                ".provider", image);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getDestinationPath() {
        File path = new File(getFilesDir(), "img");
        if (!path.exists()) path.mkdirs();
        return path;
    }
}