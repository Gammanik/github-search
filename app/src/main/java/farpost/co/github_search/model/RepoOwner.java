package farpost.co.github_search.model;


import android.os.Parcel;
import android.os.Parcelable;

public class RepoOwner implements Parcelable{
    public final String login;
    public final String avatar_url;

    public RepoOwner(String login, String avatar_url) {
        this.login = login;
        this.avatar_url = avatar_url;
    }

    //todo: could I leave the constructor private?
    public RepoOwner(Parcel in) {
        login = in.readString();
        avatar_url = in.readString();
    }

    public static final Creator<RepoOwner> CREATOR = new Creator<RepoOwner>() {
        @Override
        public RepoOwner createFromParcel(Parcel in) {
            return new RepoOwner(in);
        }

        @Override
        public RepoOwner[] newArray(int size) {
            return new RepoOwner[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(login);
        parcel.writeString(avatar_url);
    }
}
