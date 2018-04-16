package farpost.co.github_search.env;

import okhttp3.HttpUrl;

//usually I would add this class to .ginignore
public final class AppSettings {

    public static final String CLIENT_ID = "4025c9d79a896c9ff5f4";
    public static final String CLIENT_SECRET = "132f88a3af1ba6b50c6f7346da8e52455f2febeb";
    public static final String REDIRECT_URL = "farpost-github-search://callback";

    public static final HttpUrl GITHUB_AUTHORIZE_URL = new HttpUrl.Builder()
            .scheme("https")
            .host("github.com")
            .addPathSegment("login")
            .addPathSegment("oauth")
            .addPathSegment("authorize")
            .addQueryParameter("client_id", AppSettings.CLIENT_ID)
            .addQueryParameter("scope", "user")
            .addQueryParameter("redirect_uri", AppSettings.REDIRECT_URL)
            .build();
}
