package com.weberbox.pifire.update.updater.enums;

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
    JSON_ERROR,

    /**
     * Update check Error
     */
    UPDATER_ERROR,

    /**
     * Update Download Error
     */
    DOWNLOAD_ERROR,

    /**
     * Update Download Canceled
     */
    DOWNLOAD_CANCELED

}
