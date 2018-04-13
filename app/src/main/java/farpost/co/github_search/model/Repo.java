package farpost.co.github_search.model;


public class Repo {

    public final int id;
    public final String name;
    public final RepoOwner owner;
    public final String description;
    public final String language;
    public final int stargazersCount;

    public Repo(int id, String name, RepoOwner owner, String description, String language, int stargazersCount) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.language = language;
        this.stargazersCount = stargazersCount;
    }
}