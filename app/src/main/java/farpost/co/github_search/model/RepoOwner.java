package farpost.co.github_search.model;


public class RepoOwner {
    public final String login;
    public final int id;
    public final String avatar_url;

    public RepoOwner(String login, int id, String avatar_url) {
        this.login = login;
        this.id = id;
        this.avatar_url = avatar_url;
    }
}
