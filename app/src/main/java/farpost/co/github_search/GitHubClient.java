package farpost.co.github_search;

import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import farpost.co.github_search.model.AccessToken;
import farpost.co.github_search.model.ReposSearchResponse;
import farpost.co.github_search.model.User;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Single;

public class GitHubClient {

    private static final String GITHUB_BASE_URL = "https://api.github.com/";
    private static final String GITHUB_BASE_URL_WEB = "https://github.com/";

    private static GitHubClient instance;
    private GitHubService gitHubService;
    private GitHubService gitHubServiceWeb;

    private GitHubClient() {
        final Gson gson =
                new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(GITHUB_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        gitHubService = retrofit.create(GitHubService.class);


        final Retrofit retrofit_web = new Retrofit.Builder().baseUrl(GITHUB_BASE_URL_WEB)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        gitHubServiceWeb = retrofit_web.create(GitHubService.class);
    }

    public static GitHubClient getInstance() {
        if (instance == null) {
            instance = new GitHubClient();
        }
        return instance;
    }

    public Observable<ReposSearchResponse> searchRepos(@NonNull String userName, int page) {
        return gitHubService.searchRepositories(userName, page);
    }

    public Single<AccessToken> getAccessToken(String clientId, String clientSecret, String code) {
        return gitHubServiceWeb.getAccessToken(clientId, clientSecret, code);
    }

    public Single<User> getLoggedUser(String accessToken) {
        return gitHubService.getLoggedUser(accessToken);
    }
}