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

import timber.log.Timber;

class ParserJSON {

    private final URL mJsonUrl;
    private static final String KEY_LATEST_VERSION = "latestVersion";
    private static final String KEY_LATEST_VERSION_CODE = "latestVersionCode";
    private static final String KEY_RELEASE_NOTES = "releaseNotes";
    private static final String KEY_FORCE_UPDATE = "forceUpdate";
    private static final String KEY_FORCE_UPDATE_VERSIONS = "forceUpdateVersions";
    private static final String KEY_URL = "url";

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
            update.setForceUpdateVersion(json.getInt(KEY_FORCE_UPDATE_VERSIONS));
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

}
