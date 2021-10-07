package com.weberbox.pifire.updater;

import com.weberbox.pifire.updater.objects.Update;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import timber.log.Timber;

class ParserJSON {

    private final URL mJsonUrl;
    private static final String KEY_LATEST_VERSION = "latestVersion";
    private static final String KEY_LATEST_VERSION_CODE = "latestVersionCode";
    private static final String KEY_RELEASE_NOTES = "releaseNotes";
    private static final String KEY_FORCE_UPDATE = "forceUpdate";
    private static final String KEY_FORCE_UPDATE_VERSION_CODES = "forceUpdateVersionCodes";
    private static final String KEY_URL = "url";
    private static final String KEY_ASSETS = "assets";
    private static final String KEY_TAG_NAME = "tag_name";
    private static final String KEY_CONTENT_TYPE = "content_type";
    private static final String KEY_BROWSER_DOWNLOAD_URL = "browser_download_url";

    public ParserJSON(String url) {
        try {
            mJsonUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    public Update parse(){
        try {
            JSONObject json = readJsonFromUrl();
            Update update = new Update();
            update.setLatestVersion(json.getString(KEY_LATEST_VERSION).trim());
            update.setLatestVersionCode(json.optInt(KEY_LATEST_VERSION_CODE));
            update.setForceUpdate(json.getBoolean(KEY_FORCE_UPDATE));
            JSONArray forceUpdateArr = json.optJSONArray(KEY_FORCE_UPDATE_VERSION_CODES);
            if (forceUpdateArr != null) {
                ArrayList<Integer> forcedArray = new ArrayList<>();
                for (int i = 0; i < forceUpdateArr.length(); ++i) {
                    forcedArray.add(forceUpdateArr.getInt(i));
                }
                update.setForceUpdateVersionCodes(forcedArray);
            }
            JSONArray releaseArr = json.optJSONArray(KEY_RELEASE_NOTES);
            if (releaseArr != null) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < releaseArr.length(); ++i) {
                    builder.append(releaseArr.getString(i).trim());
                    if (i != releaseArr.length() - 1)
                        builder.append(System.getProperty("line.separator"));
                }
                update.setReleaseNotes(builder.toString());
            }
            URL url = new URL(json.getString(KEY_URL).trim());
            update.setUrlToDownload(url);
            return update;
        } catch (ConnectException e) {
            Timber.w(e,"There was a Connection issue");
        } catch (IOException e) {
            Timber.w(e,"The server is down or there isn't an active Internet connection");
        } catch (JSONException e) {
            Timber.w(e, "The JSON updater file is mal-formatted. AppUpdate can't check for updates");
        } catch (NullPointerException e) {
            Timber.w(e,"JSON NullPointerException");
        }

        return null;
    }

    public String parseGitHubJSON(Update update) {
        String version;
        String[] splitGitHub;
        try {
            JSONArray jsonArray = readJsonArrayFromUrl();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                JSONArray assets = json.optJSONArray(KEY_ASSETS);
                String releaseTag = json.getString(KEY_TAG_NAME);
                Timber.d("Release Tag %s", releaseTag);
                if (releaseTag.startsWith("v")) {
                    splitGitHub = releaseTag.split("(v)", 2);
                    version = splitGitHub[1].trim();
                } else {
                    version = releaseTag;
                }
                if (version.equals(update.getLatestVersion())) {
                    if (assets != null) {
                        for (int j = 0; j < assets.length(); j++) {
                            JSONObject asset = assets.getJSONObject(j);
                            if (asset.getString(KEY_CONTENT_TYPE)
                                    .equals("application/vnd.android.package-archive")) {
                                String downloadURL = asset.getString(KEY_BROWSER_DOWNLOAD_URL).trim();
                                Timber.d("Download URL %s", downloadURL);
                                return downloadURL;
                            }
                        }
                    }
                }
            }
        } catch (ConnectException e) {
            Timber.w(e,"There was a Connection issue");
        } catch (IOException e) {
            Timber.w(e,"The server is down or there isn't an active Internet connection");
        } catch (JSONException e) {
            Timber.w(e, "The JSON file is mal-formatted");
        } catch (NullPointerException e) {
            Timber.w(e,"JSON NullPointerException");
        }
        return null;
    }


    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private JSONObject readJsonFromUrl() throws IOException, JSONException {
        try (InputStream is = mJsonUrl.openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    private JSONArray readJsonArrayFromUrl() throws IOException, JSONException {
        try (InputStream is = mJsonUrl.openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONArray(jsonText);
        }
    }

}
