package farpost.co.github_search;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import farpost.co.github_search.env.AppSettings;
import farpost.co.github_search.model.AccessToken;
import farpost.co.github_search.model.User;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GitHubAuthActivity extends AppCompatActivity {

    private static final String TAG = AuthActivity.class.getSimpleName();
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_git_hub_auth);

        if(!isUserLoggedIn() && !isUserDataFetching()) {
            //redirect to gitHub webView
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppSettings.GITHUB_AUTHORIZE_URL.toString()));
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if(uri != null && uri.toString().startsWith(AppSettings.REDIRECT_URL)) {
            String code = uri.getQueryParameter("code");
            getAccessToken(AppSettings.CLIENT_ID, AppSettings.CLIENT_SECRET, code);
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String keyUserInfo = getResources().getString(R.string.saved_user_info);
        String userInfo = sharedPref.getString(keyUserInfo, "");

        return !userInfo.isEmpty();
    }

    private boolean isUserDataFetching() {
        return getIntent().getData() != null;
    }


    private void getAccessToken(final String clientId, final String clientSecret, final String code) {
        showDialog("fetching user info");
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
                        saveUserInfo(user.name);
                        hideDialog();

                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }
                });
    }

    private void saveUserInfo(String name) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_user_info), name);
        //no .apply() here to make sure it's saved before another activity will read it
        editor.commit();
    }

    private void showDialog(String message) { //put them in a view logic
        if(mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    private void hideDialog() {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
