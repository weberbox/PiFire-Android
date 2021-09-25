package com.weberbox.pifire.updater.enums;

public enum AppUpdaterError {
    /**
     * GitHub user or repo is empty
     */
    GITHUB_USER_REPO_INVALID,

    /**
     * No Internet connection available
     */
    NETWORK_NOT_AVAILABLE,

    /**
     * URL for the JSON file is not valid
     */
    JSON_URL_MALFORMED,

    /**
     * JSON file is invalid or is down
     */
    JSON_ERROR


}
