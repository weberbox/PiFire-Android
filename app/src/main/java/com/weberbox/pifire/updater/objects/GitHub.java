package com.weberbox.pifire.updater.objects;

public class GitHub {
    private String mGitHubUser;
    private String mGitHubRepo;

    public GitHub(String gitHubUser, String gitHubRepo) {
        mGitHubUser = gitHubUser;
        mGitHubRepo = gitHubRepo;
    }

    public String getGitHubUser() {
        return mGitHubUser;
    }

    public void setGitHubUser(String user) {
        mGitHubUser = user;
    }

    public String getGitHubRepo() {
        return mGitHubRepo;
    }

    public void setGitHubRepo(String repo) {
        mGitHubRepo = repo;
    }

    public static Boolean isGitHubValid(GitHub gitHub) {
        return gitHub != null && gitHub.getGitHubUser().length() != 0 &&
                gitHub.getGitHubRepo().length() != 0;
    }

}
