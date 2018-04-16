package farpost.co.github_search.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Repo implements Parcelable{

    public final int id;
    public final String name;
    public final RepoOwner owner;
    public final String description;
    public final String language;
    public final int stargazersCount;

    public Repo(int id, String name, RepoOwner owner, String description, String language, int stargazersCount) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.language = language;
        this.stargazersCount = stargazersCount;
    }

    public Repo(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.language = in.readString();
        this.stargazersCount = in.readInt();

        this.owner = in.readParcelable(getClass().getClassLoader()); //todo: not sure about getClass() which one should I get??
    }

    public static final Creator<Repo> CREATOR = new Creator<Repo>() {
        @Override
        public Repo createFromParcel(Parcel in) {
            return new Repo(in);
        }

        @Override
        public Repo[] newArray(int size) {
            return new Repo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.d("REPO", "writing class " + name);

        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(language);
        parcel.writeInt(stargazersCount);

        parcel.writeParcelable(owner, i);
    }
}