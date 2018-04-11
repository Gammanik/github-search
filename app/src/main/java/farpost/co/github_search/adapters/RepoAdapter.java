package farpost.co.github_search.adapters;


import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import farpost.co.github_search.R;
import farpost.co.github_search.model.Repo;

public class RepoAdapter extends BaseAdapter {

    private List<Repo> gitHubRepos = new ArrayList<>();

    @Override public int getCount() {
        return gitHubRepos.size();
    }

    @Override public Repo getItem(int position) {
        if (position < 0 || position >= gitHubRepos.size()) {
            return null;
        } else {
            return gitHubRepos.get(position);
        }
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        final View view = (convertView != null ? convertView : createView(parent));
        final RepoViewHolder viewHolder = (RepoViewHolder) view.getTag();
        viewHolder.setRepo(getItem(position));
        return view;
    }

    public void setRepos(@Nullable List<Repo> repos) {
        if (repos == null) {
            return;
        }
        gitHubRepos.clear();
        gitHubRepos.addAll(repos);
        notifyDataSetChanged();
    }

    private View createView(ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.item_repo, parent, false);
        final RepoViewHolder viewHolder = new RepoViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    private static class RepoViewHolder {

        private TextView textRepoName;
        private TextView textRepoDescription;
        private TextView textLanguage;
        private TextView textStars;

        public RepoViewHolder(View view) {
            textRepoName = (TextView) view.findViewById(R.id.text_repo_name);
            textRepoDescription = (TextView) view.findViewById(R.id.text_repo_description);
            textLanguage = (TextView) view.findViewById(R.id.text_language);
            textStars = (TextView) view.findViewById(R.id.text_stars);
        }

        public void setRepo(Repo repo) {
//            todo: just description and avatar
            textRepoName.setText(repo.name);
            textRepoDescription.setText(repo.description);
            textLanguage.setText("Language: " + repo.language);
            textStars.setText("Stars: " + repo.stargazersCount);
        }
    }
}