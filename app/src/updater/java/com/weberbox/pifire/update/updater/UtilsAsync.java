package com.weberbox.pifire.update.updater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.weberbox.pifire.R;
import com.weberbox.pifire.ui.dialogs.ProgressDialog;
import com.weberbox.pifire.update.updater.enums.AppUpdaterError;
import com.weberbox.pifire.update.updater.enums.UpdateFrom;
import com.weberbox.pifire.update.updater.objects.GitHub;
import com.weberbox.pifire.update.updater.objects.Update;
import com.weberbox.pifire.utils.async.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class UtilsAsync {

    public static class LatestAppVersion extends AsyncTask<Void, Void, Update> {
        private final WeakReference<Context> contextRef;
        private final LibraryPreferences libraryPreferences;
        private final Boolean forceCheck;
        private final UpdateFrom updateFrom;
        private final GitHub gitHub;
        private final String jsonUrl;
        private final AppUpdater.LibraryListener listener;

        public LatestAppVersion(Context context, Boolean forceCheck, UpdateFrom updateFrom,
                                GitHub gitHub, String jsonUrl, AppUpdater.LibraryListener listener) {
            contextRef = new WeakReference<>(context);
            libraryPreferences = new LibraryPreferences(context);
            this.forceCheck = forceCheck;
            this.updateFrom = updateFrom;
            this.gitHub = gitHub;
            this.jsonUrl = jsonUrl;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Context context = contextRef.get();
            if (context == null || listener == null) {
                cancel();
            } else if (UtilsLibrary.isNetworkAvailable(context)) {
                if (!forceCheck && !libraryPreferences.getAppUpdaterShow()) {
                    cancel();
                } else {
                    if (updateFrom == UpdateFrom.GITHUB && !GitHub.isGitHubValid(gitHub)) {
                        listener.onFailed(AppUpdaterError.GITHUB_USER_REPO_INVALID);
                        cancel();
                    } else if (updateFrom == UpdateFrom.JSON && (jsonUrl == null ||
                            !UtilsLibrary.isStringValidUrl(jsonUrl))) {
                        listener.onFailed(AppUpdaterError.JSON_URL_MALFORMED);
                        cancel();
                    }
                }
            } else {
                listener.onFailed(AppUpdaterError.NETWORK_NOT_AVAILABLE);
                cancel();
            }
        }

        @Override
        protected void onPostExecute(Update update) {
            super.onPostExecute(update);

            if (listener != null) {
                if (!isCancelled()) {
                    if (UtilsLibrary.isStringAVersion(update.getLatestVersion())) {
                        listener.onSuccess(update);
                    } else {
                        listener.onFailed(AppUpdaterError.UPDATER_ERROR);
                    }
                } else {
                    listener.onFailed(AppUpdaterError.JSON_URL_MALFORMED);
                }
            }
        }

        @Override
        protected void onBackgroundError(Exception e) {
            Timber.w(e, "App Update check failed");
            if (listener != null) {
                listener.onFailed(AppUpdaterError.UPDATER_ERROR);
            }
        }

        @Override
        protected Update doInBackground(Void unused) {
            try {
                if (updateFrom == UpdateFrom.JSON) {
                    Update update = UtilsLibrary.getLatestAppVersion(jsonUrl);
                    if (update != null) {
                        return update;
                    } else {
                        AppUpdaterError error = AppUpdaterError.JSON_ERROR;

                        if (listener != null) {
                            listener.onFailed(error);
                        }
                        cancel();
                        return null;
                    }
                } else {
                    Context context = contextRef.get();
                    if (context != null) {
                        return UtilsLibrary.getLatestAppVersionHttp(gitHub);
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

    public static class DownloadNewVersion extends AsyncTask<String, Integer, Boolean> {
        private final WeakReference<Context> contextRef;
        private final AppUpdater.DownloadListener listener;
        private final String fileName;
        private final String apiUrl;
        private final Update update;
        private File updateFile;
        private ProgressDialog progressDialog;

        public DownloadNewVersion(Context context, Update update, String fileName, String apiUrl,
                                  AppUpdater.DownloadListener listener) {
            contextRef = new WeakReference<>(context);
            this.fileName = fileName;
            this.update = update;
            this.listener = listener;
            this.apiUrl = apiUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Context context = contextRef.get();
            if (context == null || listener == null) {
                cancel();
            } else if (UtilsLibrary.isNetworkAvailable(context)) {
                if (!UtilsLibrary.isStringValidUrl(apiUrl)) {
                    listener.onFailed(AppUpdaterError.JSON_URL_MALFORMED);
                    cancel();
                } else {
                    if (progressDialog == null) {
                        if (context instanceof Activity) {
                            progressDialog = new ProgressDialog.Builder((Activity) context)
                                    .setTitle(context.getString(R.string.downloading))
                                    .setMessage("")
                                    .setCancelable(false)
                                    .build();
                            progressDialog.show();
                        }
                    }
                }
            } else {
                listener.onFailed(AppUpdaterError.NETWORK_NOT_AVAILABLE);
                cancel();
            }
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onProgress(Integer progress) {
            super.onProgress(progress);

            if (progressDialog != null) {
                if (progress < 0) {
                    progressDialog.getProgressIndicator().setIndeterminate(true);
                } else {
                    progressDialog.getProgressIndicator().setIndeterminate(false);
                    progressDialog.getProgressIndicator().setMax(100);
                    progressDialog.getProgressIndicator().setProgress(progress);
                    progressDialog.getProgressMessage().setText(String.format(Locale.US,
                            "%d / %d", progress, 100));
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            if (listener != null) {
                if (!isCancelled()) {
                    if (result != null && !result) {
                        listener.onFailed(AppUpdaterError.DOWNLOAD_ERROR);
                    } else {
                        if (updateFile != null && updateFile.exists()) {
                            Context context = contextRef.get();
                            UtilsLibrary.OpenDownloadedFile(context,
                                    context.getCacheDir().getPath(), fileName);
                        }
                    }
                } else {
                    listener.onFailed(AppUpdaterError.DOWNLOAD_CANCELED);
                }
            }
        }

        @Override
        protected void onBackgroundError(Exception e) {
            if (listener != null) {
                listener.onFailed(AppUpdaterError.DOWNLOAD_ERROR);
            }
        }

        @Override
        protected Boolean doInBackground(String arg0) {
            Context context = contextRef.get();
            OkHttpClient client = new OkHttpClient();

            String downloadUrl = new ParserJSON(apiUrl).parseGitHubJSON(update);

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
                        ResponseBody responseBody = response.body();
                        assert responseBody!= null;
                        inputStream = responseBody.byteStream();

                        byte[] buff = new byte[1024 * 4];
                        long downloaded = 0;
                        long target = responseBody.contentLength();
                        updateFile = new File(context.getCacheDir(), fileName);
                        OutputStream output = new FileOutputStream(updateFile);

                        publishProgress(0);
                        while (true) {
                            int read = inputStream.read(buff);

                            if (read == -1) {
                                break;
                            }
                            output.write(buff, 0, read);
                            //write buff
                            downloaded += read;
                            if (target > 0) {
                                publishProgress((int) (downloaded * 100 / target));
                            }
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
