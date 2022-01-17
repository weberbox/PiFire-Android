package com.weberbox.pifire.updater;

import android.annotation.SuppressLint;
import android.content.Context;

import com.weberbox.pifire.ui.dialogs.ProgressBarDialog;
import com.weberbox.pifire.updater.enums.AppUpdaterError;
import com.weberbox.pifire.updater.enums.UpdateFrom;
import com.weberbox.pifire.updater.objects.GitHub;
import com.weberbox.pifire.updater.objects.Update;
import com.weberbox.pifire.utils.async.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class UtilsAsync {

    public static class LatestAppVersion extends AsyncTask<Void, Void, Update> {
        private final WeakReference<Context> mContextRef;
        private final LibraryPreferences mLibraryPreferences;
        private final Boolean mForceCheck;
        private final UpdateFrom mUpdateFrom;
        private final GitHub mGitHub;
        private final String mJsonUrl;
        private final AppUpdater.LibraryListener mListener;

        public LatestAppVersion(Context context, Boolean forceCheck, UpdateFrom updateFrom,
                                GitHub gitHub, String jsonUrl, AppUpdater.LibraryListener listener) {
            mContextRef = new WeakReference<>(context);
            mLibraryPreferences = new LibraryPreferences(context);
            mForceCheck = forceCheck;
            mUpdateFrom = updateFrom;
            mGitHub = gitHub;
            mJsonUrl = jsonUrl;
            mListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Context context = mContextRef.get();
            if (context == null || mListener == null) {
                cancel();
            } else if (UtilsLibrary.isNetworkAvailable(context)) {
                if (!mForceCheck && !mLibraryPreferences.getAppUpdaterShow()) {
                    cancel();
                } else {
                    if (mUpdateFrom == UpdateFrom.GITHUB && !GitHub.isGitHubValid(mGitHub)) {
                        mListener.onFailed(AppUpdaterError.GITHUB_USER_REPO_INVALID);
                        cancel();
                    } else if (mUpdateFrom == UpdateFrom.JSON && (mJsonUrl == null ||
                            !UtilsLibrary.isStringValidUrl(mJsonUrl))) {
                        mListener.onFailed(AppUpdaterError.JSON_URL_MALFORMED);
                        cancel();
                    }
                }
            } else {
                mListener.onFailed(AppUpdaterError.NETWORK_NOT_AVAILABLE);
                cancel();
            }
        }

        @Override
        protected void onPostExecute(Update update) {
            super.onPostExecute(update);

            if (mListener != null) {
                if (UtilsLibrary.isStringAVersion(update.getLatestVersion())) {
                    mListener.onSuccess(update);
                }
                else {
                    mListener.onFailed(AppUpdaterError.JSON_URL_MALFORMED);
                }
            }
        }

        @Override
        protected void onBackgroundError(Exception e) {

        }

        @Override
        protected Update doInBackground(Void unused) {
            try {
                if (mUpdateFrom == UpdateFrom.JSON) {
                    Update update = UtilsLibrary.getLatestAppVersion(mJsonUrl);
                    if (update != null) {
                        return update;
                    } else {
                        AppUpdaterError error = AppUpdaterError.JSON_ERROR;

                        if (mListener != null) {
                            mListener.onFailed(error);
                        }
                        cancel();
                        return null;
                    }
                } else {
                    Context context = mContextRef.get();
                    if (context != null) {
                        return UtilsLibrary.getLatestAppVersionHttp(mGitHub);
                    } else {
                        cancel();
                        return null;
                    }
                }
            } catch (Exception ex) {
                cancel();
                return null;
            }
        }
    }

    public static class DownloadNewVersion extends AsyncTask<String, Long[], Boolean> {
        private final WeakReference<Context> mContextRef;
        private final AppUpdater.DownloadListener mListener;
        private final String mFileName;
        private final String mApiUrl;
        private final Update mUpdate;
        private File mUpdateFile;
        private ProgressBarDialog mProgressDialog;

        public DownloadNewVersion(Context context, Update update, String fileName, String apiUrl,
                                  AppUpdater.DownloadListener listener) {
            mContextRef = new WeakReference<>(context);
            mFileName = fileName;
            mUpdate = update;
            mListener = listener;
            mApiUrl = apiUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Context context = mContextRef.get();
            if (context == null || mListener == null) {
                cancel();
            } else if (UtilsLibrary.isNetworkAvailable(context)) {
                if (!UtilsLibrary.isStringValidUrl(mApiUrl)) {
                    mListener.onFailed(AppUpdaterError.JSON_URL_MALFORMED);
                    cancel();
                } else {
                    if (mProgressDialog == null) {
                        mProgressDialog = new ProgressBarDialog(context);
                        mProgressDialog.showDialog();
                        mProgressDialog.setCancelable(false);
                    }
                }
            } else {
                mListener.onFailed(AppUpdaterError.NETWORK_NOT_AVAILABLE);
                cancel();
            }
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onProgress(Long[] values) {
            super.onProgress(values);

            if (values[1] < 0) {
                mProgressDialog.setIndeterminate(true);
            } else {
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(values[1].intValue());
                mProgressDialog.setProgress(values[0].intValue());
                mProgressDialog.setMessage(String.format("%d / %d ", values[0], values[1]));
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (mProgressDialog.isShowing() && mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

            if (result != null && !result) {
                mListener.onFailed(AppUpdaterError.DOWNLOAD_ERROR);
            } else {
                if (mUpdateFile != null && mUpdateFile.exists()) {
                    Context context = mContextRef.get();
                    UtilsLibrary.OpenDownloadedFile(context, context.getCacheDir().getPath(),
                            mFileName);
                }
            }
        }

        @Override
        protected void onBackgroundError(Exception e) {

        }

        @Override
        protected Boolean doInBackground(String arg0) {
            Context context = mContextRef.get();
            OkHttpClient client = new OkHttpClient();

            String downloadUrl = new ParserJSON(mApiUrl).parseGitHubJSON(mUpdate);

            if (downloadUrl == null) {
                return false;
            }

            try {
                URL url = new URL(downloadUrl);
                Call call = client.newCall(new Request.Builder().url(url).get().build());
                Response response = call.execute();
                if (response.code() == 200 || response.code() == 201) {

                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        Timber.d(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    InputStream inputStream = null;
                    try {
                        assert response.body() != null;
                        inputStream = response.body().byteStream();

                        byte[] buff = new byte[1024 * 4];
                        long downloaded = 0;
                        long target = response.body().contentLength();
                        mUpdateFile = new File(context.getCacheDir(), mFileName);
                        OutputStream output = new FileOutputStream(mUpdateFile);

                        publishProgress(new Long[]{0L, target});
                        while (true) {
                            int read = inputStream.read(buff);

                            if (read == -1) {
                                break;
                            }
                            output.write(buff, 0, read);
                            //write buff
                            downloaded += read;
                            publishProgress(new Long[]{downloaded, target});
                            if (isCancelled()) {
                                return false;
                            }
                        }

                        output.flush();
                        output.close();

                        return downloaded == target;
                    } catch (IOException | NullPointerException e) {
                        Timber.w(e, "Error Downloading");
                        return false;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    }
                } else {
                    return false;
                }
            } catch (IOException e) {
                Timber.w(e, "Error Downloading");
                return false;
            }
        }
    }
}
