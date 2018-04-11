package farpost.co.github_search.model;


import java.util.List;

public class ReposSearchResponse {
    public final int total_count;
    public final boolean incomplete_results;
    public final List<Repo> items;
//    todo: add Metadata with each repo - List<RepoItem> - where Repo is inside
//    todo: add owner model


    public ReposSearchResponse(int total_count, boolean incomplete_results, List<Repo> items) {
        this.total_count = total_count;
        this.incomplete_results = incomplete_results;
        this.items = items;
    }
}
