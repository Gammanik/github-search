package farpost.co.github_search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = AuthActivity.class.getSimpleName();

    @BindView(R.id.button_github) Button buttonGitHub;
    @BindView(R.id.button_anon) Button buttonAnonymous;

    @OnClick(R.id.button_github)
    void onClickGitHub() {
        if(!isUserLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), GitHubAuthActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.button_anon)
    void onClickAnon() {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity);
        ButterKnife.bind(this);

        if(isUserLoggedIn()) {
            Log.e(TAG, "userLoggedIn");
            //start SearchActivity if we're logged?
            //but it's not written in the task
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String keyUserInfo = getResources().getString(R.string.saved_user_info);
        String userInfo = sharedPref.getString(keyUserInfo, "");

        return !userInfo.isEmpty();
    }

}
