package com.weberbox.pifire.ui.activities;

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
import androidx.core.content.FileProvider;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.util.List;

public class ImagePickerActivity extends BaseActivity {

    private String fileName;
    private int aspectRatioX = 1;
    private int aspectRatioY = 1;
    private int bitmapMaxWidth = 600;
    private int bitmapMaxHeight = 600;
    private int imageCompression = 80;

    private boolean lockAspectRatio = true;
    private boolean setMaxWidthHeight = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, R.string.image_picker_no_intent, Toast.LENGTH_LONG).show();
            return;
        }

        fileName = intent.getStringExtra(INTENT_IMAGE_FILENAME);
        aspectRatioX = intent.getIntExtra(INTENT_ASPECT_RATIO_X, aspectRatioX);
        aspectRatioY = intent.getIntExtra(INTENT_ASPECT_RATIO_Y, aspectRatioY);
        imageCompression = intent.getIntExtra(INTENT_COMPRESSION_QUALITY, imageCompression);
        lockAspectRatio = intent.getBooleanExtra(INTENT_LOCK_ASPECT_RATIO, lockAspectRatio);
        setMaxWidthHeight = intent.getBooleanExtra(INTENT_BITMAP_MAX_WIDTH_HEIGHT, setMaxWidthHeight);
        bitmapMaxWidth = intent.getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth);
        bitmapMaxHeight = intent.getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight);

        int requestCode = intent.getIntExtra(INTENT_PICKER_OPTION, -1);
        if (requestCode == INTENT_REQUEST_IMAGE_CAPTURE) {
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
                        if (fileName == null) {
                            fileName = System.currentTimeMillis() + ".jpg";
                        }
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                getCacheImagePath(fileName));
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
                        if (fileName == null) {
                            fileName = System.currentTimeMillis() + ".jpg";
                        }
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
                if (result != null && result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
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
                if (result != null && result.getResultCode() == Activity.RESULT_OK) {
                    cropImage(getCacheImagePath(fileName));
                } else {
                    setResultCancelled();
                }
            });

    private final ActivityResultLauncher<Intent> uCropActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
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
        Uri destinationUri = Uri.fromFile(new File(getDestinationPath(), fileName));
        uCropActivityResult.launch(getUCropIntent(ImagePickerActivity.this, sourceUri,
                destinationUri, getUCropOptions()));
    }

    public static Intent getUCropIntent(Context context, Uri mediaScannerUri, Uri destination,
                                        UCrop.Options options) {
        Intent intent = new Intent();
        Bundle uCropBundle = options.getOptionBundle();
        uCropBundle.putParcelable(UCrop.EXTRA_INPUT_URI, mediaScannerUri);
        uCropBundle.putParcelable(UCrop.EXTRA_OUTPUT_URI, destination);

        intent.putExtras(options.getOptionBundle());
        intent.setClass(context, UCropActivity.class);
        return intent;
    }

    public UCrop.Options getUCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(imageCompression);
        options.setToolbarTitle(getString(R.string.image_picker_toolbar_title));

        if (lockAspectRatio)
            options.withAspectRatio(aspectRatioX, aspectRatioY);

        if (setMaxWidthHeight)
            options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight);
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
        finish();
    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private void setResultPermissions() {
        Intent intent = new Intent();
        setResult(Constants.RESULT_PERMISSIONS, intent);
        finish();
    }

    private void setResultError() {
        Intent intent = new Intent();
        setResult(UCrop.RESULT_ERROR, intent);
        finish();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return FileProvider.getUriForFile(ImagePickerActivity.this, getPackageName() +
                ".provider", image);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getDestinationPath() {
        File path = new File(getFilesDir(), "img");
        if (!path.exists()) path.mkdirs();
        return path;
    }

    private static final String INTENT_PICKER_OPTION = "image_picker_option";
    private static final String INTENT_IMAGE_FILENAME = "image_filename";
    private static final String INTENT_ASPECT_RATIO_X = "aspect_ratio_x";
    private static final String INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y";
    private static final String INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio";
    private static final String INTENT_COMPRESSION_QUALITY = "compression_quality";
    private static final String INTENT_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height";
    private static final String INTENT_BITMAP_MAX_WIDTH = "max_width";
    private static final String INTENT_BITMAP_MAX_HEIGHT = "max_height";
    private static final int INTENT_REQUEST_IMAGE_CAPTURE = 0;

    @SuppressWarnings("unused")
    public static class ImageOptionBuilder {
        private final Bundle mExtras;

        private ImageOptionBuilder() {
            mExtras = new Bundle();
        }

        /**
         * Builder for {@link ImagePickerActivity}.
         */
        public static ImageOptionBuilder getBuilder() {
            return new ImageOptionBuilder();
        }

        /**
         * Set capture image from camera
         *
         * @return this, for chaining.
         */
        public ImageOptionBuilder setImageCapture() {
            mExtras.putInt(INTENT_PICKER_OPTION, 0);
            return this;
        }

        /**
         * Set load image from gallery
         *
         * @return this, for chaining.
         */
        public ImageOptionBuilder setImageGallery() {
            mExtras.putInt(INTENT_PICKER_OPTION, 1);
            return this;
        }

        /**
         * Sets the filename that will be used when image is stored
         *
         * @param name sets the name of file
         * @return this, for chaining.
         */
        public ImageOptionBuilder setFilename(String name) {
            mExtras.putString(INTENT_IMAGE_FILENAME, name);
            return this;
        }

        /**
         * Sets the allowed X ratio for image cropping eg x1:y1 x4:y3 x16:y9
         *
         * @param ratioX sets X ratio for the image
         * @return this, for chaining.
         */
        public ImageOptionBuilder setAspectRatioX(int ratioX) {
            mExtras.putInt(INTENT_ASPECT_RATIO_X, ratioX);
            return this;
        }

        /**
         * Sets the allowed Y ratio for image cropping eg x1:y1 x4:y3 x16:y9
         *
         * @param ratioY sets Y ratio for the image
         * @return this, for chaining.
         */
        public ImageOptionBuilder setAspectRatioY(int ratioY) {
            mExtras.putInt(INTENT_ASPECT_RATIO_Y, ratioY);
            return this;
        }

        /**
         * Sets if the aspect ratio is locked when cropping
         *
         * @param locked will lock the aspect ratio
         * @return this, for chaining.
         */
        public ImageOptionBuilder setAspectRatioLocked(boolean locked) {
            mExtras.putBoolean(INTENT_LOCK_ASPECT_RATIO, locked);
            return this;
        }

        /**
         * Sets the output image compression quality
         *
         * @param quality the compression quality
         * @return this, for chaining.
         */
        public ImageOptionBuilder setCompressionQuality(int quality) {
            mExtras.putInt(INTENT_COMPRESSION_QUALITY, quality);
            return this;
        }

        /**
         * Sets if the Bitmap has a locked Width and Height
         *
         * @param enabled true for locked and false to unlock
         * @return this, for chaining.
         */
        public ImageOptionBuilder setBitmapMaxWidthHeight(boolean enabled) {
            mExtras.putBoolean(INTENT_BITMAP_MAX_WIDTH_HEIGHT, enabled);
            return this;
        }

        /**
         * Sets the max Bitmap width for the output file
         *
         * @param width the max bitmap width
         * @return this, for chaining.
         */
        public ImageOptionBuilder setBitmapMaxWidth(int width) {
            mExtras.putInt(INTENT_BITMAP_MAX_WIDTH, width);
            return this;
        }

        /**
         * Sets the max Bitmap height for the output file
         *
         * @param height the max bitmap height
         * @return this, for chaining.
         */
        public ImageOptionBuilder setBitmapMaxHeight(int height) {
            mExtras.putInt(INTENT_BITMAP_MAX_HEIGHT, height);
            return this;
        }

        /**
         * Builds the intent.
         */
        public Intent build(Context ctx) {
            Intent i = new Intent(ctx, ImagePickerActivity.class);
            i.putExtras(mExtras);
            return i;
        }
    }
}