package com.weberbox.pifire.updater;

import android.content.Context;
import android.os.AsyncTask;

import com.weberbox.pifire.updater.enums.AppUpdaterError;
import com.weberbox.pifire.updater.enums.UpdateFrom;
import com.weberbox.pifire.updater.objects.GitHub;
import com.weberbox.pifire.updater.objects.Update;

import java.lang.ref.WeakReference;

class UtilsAsync {

    static class LatestAppVersion extends AsyncTask<Void, Void, Update> {
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
                cancel(true);
            } else if (UtilsLibrary.isNetworkAvailable(context)) {
                if (!mForceCheck && !mLibraryPreferences.getAppUpdaterShow()) {
                    cancel(true);
                } else {
                    if (mUpdateFrom == UpdateFrom.GITHUB && !GitHub.isGitHubValid(mGitHub)) {
                        mListener.onFailed(AppUpdaterError.GITHUB_USER_REPO_INVALID);
                        cancel(true);
                    } else if (mUpdateFrom == UpdateFrom.JSON && (mJsonUrl == null ||
                            !UtilsLibrary.isStringAnUrl(mJsonUrl))) {
                        mListener.onFailed(AppUpdaterError.JSON_URL_MALFORMED);
                        cancel(true);
                    }
                }
            } else {
                mListener.onFailed(AppUpdaterError.NETWORK_NOT_AVAILABLE);
                cancel(true);
            }
        }

        @Override
        protected Update doInBackground(Void... voids) {
            try {
                if (mUpdateFrom == UpdateFrom.JSON) {
                    Update update = UtilsLibrary.getLatestAppVersion(mUpdateFrom, mJsonUrl);
                    if (update != null) {
                        return update;
                    } else {
                        AppUpdaterError error = AppUpdaterError.JSON_ERROR;

                        if (mListener != null) {
                            mListener.onFailed(error);
                        }
                        cancel(true);
                        return null;
                    }
                } else {
                    Context context = mContextRef.get();
                    if (context != null) {
                        return UtilsLibrary.getLatestAppVersionHttp(context, mUpdateFrom, mGitHub);
                    } else {
                        cancel(true);
                        return null;
                    }
                }
            } catch (Exception ex) {
                cancel(true);
                return null;
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
    }

}
