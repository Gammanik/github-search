package farpost.co.github_search;


import farpost.co.github_search.model.ReposSearchResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface GitHubService {
    @GET("search/repositories")
    Observable<ReposSearchResponse> searchRepositories(@Query("q") String q, @Query("page") int page);
}