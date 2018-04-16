package farpost.co.github_search;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import farpost.co.github_search.adapters.RepoAdapter;
import farpost.co.github_search.model.Repo;
import farpost.co.github_search.model.ReposSearchResponse;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {

    private final String KEY_REPO_LIST = "KEY_REPO_LIST";
    private final String KEY_IS_SCREEN_JUST_ROTATED = "KEY_SCREEN_JUST_ROTATED";
    private final String KEY_CURRENT_PAGE = "KEY_CURRENT_PAGE";
    private static final String TAG = SearchActivity.class.getSimpleName();

    private RepoAdapter adapter = new RepoAdapter();
    private Subscription subscription;
    private int currentPage = 1;
    //while doing fetching
    private ProgressDialog mProgressDialog;
    /*a small kostyl here - because when we're turning the screen and if there is
    * any text inside the search box then onTextChanged is invoking anyway
    * I mean I could just use android:configChanges="keyboardHidden|orientation" */
    private boolean isScreenJustRotated = false;

    @BindView(R.id.edit_text_search) EditText search;
    @BindView(R.id.list_view_repos) ListView listView;
    @BindView(R.id.nothing_is_found) TextView textView;


    @OnTextChanged(R.id.edit_text_search) //todo: it is invoking when activity is recreating
    public void onTextChanged(CharSequence text) {

        if(!isScreenJustRotated) {
            Log.e(TAG, "onTextChanged");
            String searchQuery = text.toString();
            adapter.clearRepos();
            currentPage = 1; //to handle the case we scrolled before

            if (text.length() != 0)
                searchRepos(searchQuery, currentPage);
        }

        isScreenJustRotated = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //to make the newly created activity do not make search again
        outState.putBoolean(KEY_IS_SCREEN_JUST_ROTATED, true);
        outState.putInt(KEY_CURRENT_PAGE, currentPage);
        outState.putParcelableArrayList(KEY_REPO_LIST, adapter.getRepos());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ArrayList<Repo> repos = savedInstanceState.getParcelableArrayList(KEY_REPO_LIST);

        adapter.notifyDataSetChanged();
        adapter.setRepos(repos);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        if(savedInstanceState != null) {
            isScreenJustRotated = savedInstanceState.getBoolean(KEY_IS_SCREEN_JUST_ROTATED);
            currentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE);
        }

        if(!getLoggedUserName().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "Hello again, " + getLoggedUserName() + "!",
                    Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG, "logged user:" + getLoggedUserName());

        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() -
                        listView.getFooterViewsCount()) >= (adapter.getCount() - 1)) {
                    // when listView has hit the bottom
                    searchRepos(search.getText().toString(), ++currentPage);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

            });

    }

    private void searchRepos(final String query, final int page) {
        showDialog("searching for " + query);
        subscription = GitHubClient.getInstance()
                .searchRepos(query, page)
                .debounce(100, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReposSearchResponse>() {
                    @Override public void onCompleted() {}

                    @Override public void onError(Throwable e) {
                        e.printStackTrace();
                        //todo: what if next page is just empty after the scroll?

                        listView.setEmptyView(textView);
                        textView.setText("nothing is found on query: " + query);

                        hideDialog();
                        Log.d(TAG, "In onError()");
                    }

                    @Override public void onNext(ReposSearchResponse reposSearchResponse) {
                        Log.d(TAG, "In onNext() page: " + page);
                        hideDialog();
                        adapter.notifyDataSetChanged();
                        adapter.setRepos(reposSearchResponse.items);
                    }
                });
    }

    private String getLoggedUserName() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String keyUserInfo = getResources().getString(R.string.saved_user_info);

        return sharedPref.getString(keyUserInfo, "");
    }

    private void logoutUser() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit()
                .remove(getResources().getString(R.string.saved_user_info))
                .apply();

        Toast.makeText(getApplicationContext(),"Logout successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
        startActivity(intent);
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


    @Override protected void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        if(mProgressDialog != null) {
            mProgressDialog.cancel();
        }
//in case of clearing we're not able to restore the repos in onRestoreInstanceState
//        adapter.clearRepos();
        super.onDestroy();
    }

/////////////////menu shit
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!getLoggedUserName().isEmpty()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
