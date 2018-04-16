package farpost.co.github_search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import farpost.co.github_search.env.AppSettings;
import farpost.co.github_search.model.AccessToken;
import farpost.co.github_search.model.User;
import rx.Observer;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = AuthActivity.class.getSimpleName();
    private Subscription subscription;

    @BindView(R.id.button_github) Button buttonGitHub;

    @OnClick(R.id.button_github)
    void onClick() {
        Log.e(TAG, "onClick");

        if(!isUserLoggedIn()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppSettings.GITHUB_AUTHORIZE_URL.toString()));
            startActivity(intent);

            Log.d(TAG, AppSettings.GITHUB_AUTHORIZE_URL.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity);
        ButterKnife.bind(this);

        if(isUserLoggedIn()) {
            Log.d(TAG, "userLoggedIn");
            //todo: start SearchActivity
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if(uri != null && uri.toString().startsWith(AppSettings.REDIRECT_URL)) {
            Log.e(TAG, uri.toString());

            String code = uri.getQueryParameter("code");
            getAccessToken(AppSettings.CLIENT_ID, AppSettings.CLIENT_SECRET, code);
        }
    }

    private void getAccessToken(final String clientId, final String clientSecret, final String code) {
        GitHubClient.getInstance()
                .getAccessToken(clientId, clientSecret, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<AccessToken>() {

                    @Override
                    public void onSuccess(AccessToken accessToken) {
                        Log.d(TAG, "got an access token: " + accessToken.getAccessToken());
                        getLoggedUser(accessToken.getAccessToken());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Sorry, your access token is invalid",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getLoggedUser(final String accessToken) {
        GitHubClient.getInstance()
                .getLoggedUser(accessToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<User>() {
                    @Override
                    public void onSuccess(User user) {
                        //todo: start SearchActivity
                        Log.d(TAG, user.name);
//                        saveUserInfo(user.name);
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }
                });

    }

    private boolean isUserLoggedIn() {
        return false;
    }

    private void saveUserInfo(String name) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_user_info), name);
        editor.apply();
    }

}
