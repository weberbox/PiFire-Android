package com.weberbox.pifire.update.updater.objects;

@SuppressWarnings("unused")
public class GitHub {
    private String gitHubUser;
    private String gitHubRepo;

    public GitHub(String gitHubUser, String gitHubRepo) {
        this.gitHubUser = gitHubUser;
        this.gitHubRepo = gitHubRepo;
    }

    public String getGitHubUser() {
        return gitHubUser;
    }

    public void setGitHubUser(String user) {
        gitHubUser = user;
    }

    public String getGitHubRepo() {
        return gitHubRepo;
    }

    public void setGitHubRepo(String repo) {
        gitHubRepo = repo;
    }

    public static Boolean isGitHubValid(GitHub gitHub) {
        return gitHub != null && gitHub.getGitHubUser().length() != 0 &&
                gitHub.getGitHubRepo().length() != 0;
    }

}
