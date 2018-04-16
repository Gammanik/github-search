package farpost.co.github_search;


import farpost.co.github_search.model.AccessToken;
import farpost.co.github_search.model.ReposSearchResponse;
import farpost.co.github_search.model.User;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.Single;

public interface GitHubService {
    @GET("search/repositories")
    Observable<ReposSearchResponse> searchRepositories(@Query("q") String q,
                                                       @Query("page") int page);

    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    Single<AccessToken> getAccessToken(
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("code") String code
    );

    @GET("user")
    Single<User> getLoggedUser(@Query("access_token") String accessToken);

}