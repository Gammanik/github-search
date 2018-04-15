package farpost.co.github_search;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import farpost.co.github_search.adapters.RepoAdapter;
import farpost.co.github_search.model.ReposSearchResponse;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = SearchActivity.class.getSimpleName();
    private RepoAdapter adapter = new RepoAdapter();
    private Subscription subscription;
    private int currentPage = 1;
    //while doing fetching
    private ProgressDialog mProgressDialog;

    @BindView(R.id.edit_text_search) EditText search;
    @BindView(R.id.list_view_repos) ListView listView;
    @BindView(R.id.nothing_is_found) TextView textView;


    @OnTextChanged(R.id.edit_text_search)
    public void onTextChanged(CharSequence text) {
        String searchQuery = text.toString();
        adapter.clearRepos();
        adapter.notifyDataSetChanged();
        currentPage = 1; //to handle the case we scrolled before

        if(text.length() != 0)
            searchRepos(searchQuery, currentPage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() -
                        listView.getFooterViewsCount()) >= (adapter.getCount() - 1)) {

                    // Now your listview has hit the bottom
                    searchRepos("gameejrherlljrkj!!!!!", ++currentPage);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

            });

    }

    private void searchRepos(final String query, int page) {
        showDialog("searching for " + query);
        subscription = GitHubClient.getInstance()
                .searchRepos(query, page)
                .debounce(100, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReposSearchResponse>() {
                    @Override public void onCompleted() {
                        Log.d(TAG, "In onCompleted()");
                    }

                    @Override public void onError(Throwable e) {
                        e.printStackTrace();
                        //todo: what if next page is just empty after the scroll?

                        listView.setEmptyView(textView);
                        textView.setText("nothing is found on query: " + query);

                        hideDialog();
                        Log.d(TAG, "In onError()");
                    }

                    @Override public void onNext(ReposSearchResponse reposSearchResponse) {
                        Log.d(TAG, "In onNext()");
                        hideDialog();
                        adapter.setRepos(reposSearchResponse.items);
                    }
                });
    }

    private void showDialog(String message) {
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
        adapter.clearRepos();
        super.onDestroy();
    }

}
