package farpost.co.github_search;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final ListView listView = (ListView) findViewById(R.id.list_view_repos);
        listView.setAdapter(adapter);

        Button clickButton = (Button) findViewById(R.id.button_search);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("SearchActivity", "clicked!");
                searchRepos("game");
            }
        });
    }

    private void searchRepos(String query) {
        subscription = GitHubClient.getInstance()
                .searchRepos(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReposSearchResponse>() {
                    @Override public void onCompleted() {
                        Log.d(TAG, "In onCompleted()");
                    }

                    @Override public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d(TAG, "In onError()");
                    }

                    @Override public void onNext(ReposSearchResponse reposSearchResponse) {
                        Log.d(TAG, "In onNext()");
                        Log.d(TAG, reposSearchResponse.items.get(1).name);
                        adapter.setRepos(reposSearchResponse.items);
                    }
                });
    }


    @Override protected void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

}
