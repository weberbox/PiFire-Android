package com.weberbox.pifire.updater;

import android.content.Context;
import androidx.annotation.NonNull;

import com.weberbox.pifire.updater.enums.AppUpdaterError;
import com.weberbox.pifire.updater.enums.UpdateFrom;
import com.weberbox.pifire.updater.objects.GitHub;
import com.weberbox.pifire.updater.objects.Update;

public class AppUpdaterUtils {
    private final Context mContext;
    private UpdateListener mUpdateListener;
    private AppUpdaterListener mAppUpdaterListener;
    private UpdateFrom mUpdateFrom;
    private GitHub mGitHub;
    private String mJSONUrl;
    private UtilsAsync.LatestAppVersion mLatestAppVersion;

    public interface UpdateListener {
        /**
         * onSuccess method called after it is successful
         * onFailed method called if it can't retrieve the latest version
         *
         * @param update object with the latest update information: version and url to download
         * @see com.weberbox.pifire.updater.objects.Update
         * @param isUpdateAvailable compare installed version with the latest one
         */
        void onSuccess(Update update, Boolean isUpdateAvailable);

        void onFailed(AppUpdaterError error);
    }

    public interface AppUpdaterListener {
        /**
         * onSuccess method called after it is successful
         * onFailed method called if it can't retrieve the latest version
         *
         * @param latestVersion     available in the provided source
         * @param isUpdateAvailable compare installed version with the latest one
         */
        void onSuccess(String latestVersion, Boolean isUpdateAvailable);

        void onFailed(AppUpdaterError error);
    }

    public AppUpdaterUtils(Context context) {
        mContext = context;
        mUpdateFrom = UpdateFrom.JSON;
    }

    /**
     * Set the source where the latest update can be found. Default: JSON.
     *
     * @param updateFrom source where the latest update is uploaded. If GITHUB is selected, 
     *                    .setGitHubAndRepo method is required.
     * @return this
     * @see com.weberbox.pifire.updater.enums.UpdateFrom
     * @see <a href="https://github.com/javiersantos/AppUpdater/wiki">Additional documentation</a>
     */
    public AppUpdaterUtils setUpdateFrom(UpdateFrom updateFrom) {
        mUpdateFrom = updateFrom;
        return this;
    }

    /**
     * Set the user and repo where the releases are uploaded. You must upload your updates as a 
     * release in order to work properly tagging them as vX.X.X or X.X.X.
     *
     * @param user GitHub user
     * @param repo GitHub repository
     * @return this
     */
    public AppUpdaterUtils setGitHubUserAndRepo(String user, String repo) {
        mGitHub = new GitHub(user, repo);
        return this;
    }

    /**
     * Set the url to the xml with the latest version info.
     *
     * @param jsonUrl file
     * @return this
     */
    public AppUpdaterUtils setUpdateJSON(@NonNull String jsonUrl) {
        mJSONUrl = jsonUrl;
        return this;
    }


    /**
     * Method to set the AppUpdaterListener for the AppUpdaterUtils actions
     *
     * @param appUpdaterListener the listener to be notified
     * @return this
     * @see com.weberbox.pifire.updater.AppUpdaterUtils.AppUpdaterListener
     * @deprecated
     */
    public AppUpdaterUtils withListener(AppUpdaterListener appUpdaterListener) {
        mAppUpdaterListener = appUpdaterListener;
        return this;
    }

    /**
     * Method to set the UpdateListener for the AppUpdaterUtils actions
     *
     * @param updateListener the listener to be notified
     * @return this
     * @see com.weberbox.pifire.updater.AppUpdaterUtils.UpdateListener
     */
    public AppUpdaterUtils withListener(UpdateListener updateListener) {
        mUpdateListener = updateListener;
        return this;
    }

    /**
     * Execute AppUpdaterUtils in background.
     */
    public void start() {
        mLatestAppVersion = new UtilsAsync.LatestAppVersion(mContext, true, mUpdateFrom,
                mGitHub, mJSONUrl, new AppUpdater.LibraryListener() {
            @Override
            public void onSuccess(Update update) {
                Update installedUpdate = new Update(UtilsLibrary.getAppInstalledVersion(mContext), 
                        UtilsLibrary.getAppInstalledVersionCode(mContext));

                if (mUpdateListener != null) {
                    mUpdateListener.onSuccess(update, UtilsLibrary.isUpdateAvailable(installedUpdate, 
                            update));
                } else if (mAppUpdaterListener != null) {
                    mAppUpdaterListener.onSuccess(update.getLatestVersion(), 
                            UtilsLibrary.isUpdateAvailable(installedUpdate, update));
                } else {
                    throw new RuntimeException("You must provide a listener for the AppUpdaterUtils");
                }
            }

            @Override
            public void onFailed(AppUpdaterError error) {
                if (mUpdateListener != null) {
                    mUpdateListener.onFailed(error);
                } else if (mAppUpdaterListener != null) {
                    mAppUpdaterListener.onFailed(error);
                } else {
                    throw new RuntimeException("You must provide a listener for the AppUpdaterUtils");
                }
            }
        });

        mLatestAppVersion.execute();
    }

    /**
     * Stops the execution of AppUpdater.
     */
    public void stop() {
        if (mLatestAppVersion != null && !mLatestAppVersion.isCancelled()) {
            mLatestAppVersion.cancel(true);
        }
    }
}