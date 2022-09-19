package com.weberbox.pifire.updater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.updater.enums.Duration;
import com.weberbox.pifire.updater.objects.GitHub;
import com.weberbox.pifire.updater.objects.Update;
import com.weberbox.pifire.updater.objects.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

class UtilsLibrary {

    private static final String GITHUB_URL = "https://github.com/";
    private static final String GITHUB_TAG_RELEASE = "/tree/";

    public static String getAppName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    public static String getAppInstalledVersion() {
        String version = BuildConfig.VERSION_NAME;
        Timber.d("Installed App Version: %s", version);
        return version;
    }

    public static Integer getAppInstalledVersionCode() {
        int versionCode = BuildConfig.VERSION_CODE;
        Timber.d("Installed App VersionCode: %s", versionCode);
        return versionCode;
    }

    public static Boolean isUpdateAvailable(Update installedVersion, Update latestVersion) {
        if (latestVersion.getLatestVersionCode() != null && latestVersion.getLatestVersionCode() > 0) {
            Timber.d("Latest Version: %s", latestVersion.getLatestVersion());
            Timber.d("Latest VersionCode: %s", latestVersion.getLatestVersionCode());
            return latestVersion.getLatestVersionCode() > installedVersion.getLatestVersionCode();
        } else {
            if (!TextUtils.equals(installedVersion.getLatestVersion(), "0.0.0.0") &&
                    !TextUtils.equals(latestVersion.getLatestVersion(), "0.0.0.0")) {
                try {
                    final Version installed = new Version(installedVersion.getLatestVersion());
                    final Version latest = new Version(latestVersion.getLatestVersion());
                    return installed.compareTo(latest) < 0;
                } catch (Exception e) {
                    Timber.w(e, "Failed comparing versions");
                    return false;
                }
            } else return false;
        }
    }

    public static Boolean isStringAVersion(String version) {
        return version.matches(".*\\d+.*");
    }

    public static Boolean isStringValidUrl(String s) {
        boolean res = false;
        try {
            new URL(s);
            res = true;
        } catch (MalformedURLException e) {
            Timber.w(e, "Url is not valid");
        }
        return res;
    }

    public static Boolean getDurationEnumToBoolean(Duration duration) {
        return duration == Duration.INDEFINITE;
    }

    private static URL getUpdateURL(GitHub gitHub) {
        String res = GITHUB_URL + gitHub.getGitHubUser() + "/" + gitHub.getGitHubRepo()
                + "/releases/latest";

        try {
            return new URL(res);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    public static Update getLatestAppVersionHttp(GitHub gitHub) {
        boolean isAvailable = false;
        String source = "";
        OkHttpClient client = new OkHttpClient();
        URL url = getUpdateURL(gitHub);
        Request request = new Request.Builder()
                .url(url)
                .build();
        ResponseBody body = null;

        try {
            Response response = client.newCall(request).execute();
            body = response.body();
            BufferedReader reader = null;
            if (body != null) {
                reader = new BufferedReader(new InputStreamReader(body.byteStream(),
                        StandardCharsets.UTF_8));
            }
            StringBuilder str = new StringBuilder();

            String line;
            if (reader != null) {
                while ((line = reader.readLine()) != null) {
                    if (line.contains(GITHUB_TAG_RELEASE)) {
                        str.append(line);
                        isAvailable = true;
                    }
                }
            }

            if (str.length() == 0) {
                Timber.d("Cannot retrieve latest version. Is it configured properly?");
            }

            if (body != null) {
                body.close();
            }
            source = str.toString();
        } catch (FileNotFoundException e) {
            Timber.w(e, "App wasn't found in the provided source");
        } catch (IOException e) {
            Timber.w(e, "Error reading response");
        } finally {
            if (body != null) {
                body.close();
            }
        }

        final String version = getVersion(isAvailable, source);
        final URL updateUrl = getUpdateURL(gitHub);

        return new Update(version, updateUrl);
    }

    private static String getVersion(Boolean isAvailable, String source) {
        String version = "0.0.0.0";
        if (isAvailable) {
            String[] splitGitHub = source.split(GITHUB_TAG_RELEASE);
            if (splitGitHub.length > 1) {
                splitGitHub = splitGitHub[1].split("(\")");
                version = splitGitHub[0].trim();
                if (version.startsWith("v")) {
                    splitGitHub = version.split("(v)", 2);
                    version = splitGitHub[1].trim();
                }
            }
        }
        return version;
    }

    public static Boolean getRequiredUpdate(Update update, Integer currentVersion) {
        ArrayList<Integer> forcedVersions = update.getForceUpdateVersionCodes();
        if (update.getForceUpdate()) {
            if (forcedVersions != null) {
                for (int i = 0; i < forcedVersions.size(); ++i) {
                    if (currentVersion.equals(forcedVersions.get(i))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Update getLatestAppVersion(String url) {
        return new ParserJSON(url).parse();
    }

    public static Intent intentToUpdate(URL url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
    }

    public static void getAppUpdate(Context context, Update update) {
        if (Prefs.getBoolean(context.getString(R.string.prefs_app_updater_download),
                context.getResources().getBoolean(R.bool.def_app_updater_download))) {
            new UtilsAsync.DownloadNewVersion(context, update, Constants.UPDATE_FILENAME,
                    context.getString(R.string.def_github_api_url),
                    error -> {
                        Timber.d("App Download Error %s", error);
                        if (context instanceof Activity) {
                            UtilsDisplay.showUpdateFailedAlert((Activity) context,
                                    context.getResources().getString(
                                            R.string.updater_update_download_failed),
                                    false).show();
                        } else {
                            Toast.makeText(context, R.string.updater_update_download_failed,
                                    Toast.LENGTH_LONG).show();
                        }
                    }).execute();
        } else {
            Intent intent = intentToUpdate(update.getUrlToDownload());
            context.startActivity(intent);
        }
    }

    public static Boolean isAbleToShow(Integer successfulChecks, Integer showEvery) {
        return successfulChecks % showEvery == 0;
    }

    @SuppressLint("MissingPermission")
    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return actNw != null && (
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    public static void OpenDownloadedFile(Context context, String location, String filename) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(getUriFromFile(context, location, filename),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    private static Uri getUriFromFile(Context context, String location, String fileName) {
        return FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider",
                new File(location + "/" + fileName));
    }

}
