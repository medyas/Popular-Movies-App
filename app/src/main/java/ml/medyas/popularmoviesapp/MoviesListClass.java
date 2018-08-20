package ml.medyas.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

public class MoviesListClass implements Parcelable {

    public MoviesListClass(String poster_path, String adult, String overview, String release_date, String gener_ids, String id, String original_title, String original_language, String title, String backdrop_path, String popularity, String vote_count, String video, String vote_average) {
        this.poster_path = poster_path;
        this.adult = adult;
        this.overview = overview;
        this.release_date = release_date;
        this.gener_ids = gener_ids;
        this.id = id;
        this.original_title = original_title;
        this.original_language = original_language;
        this.title = title;
        this.backdrop_path = backdrop_path;
        this.popularity = popularity;
        this.vote_count = vote_count;
        this.video = video;
        this.vote_average = vote_average;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String isAdult() {
        return adult;
    }

    public void setAdult(String adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getGener_ids() {
        return gener_ids;
    }

    public void setGener_ids(String gener_ids) {
        this.gener_ids = gener_ids;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public String isVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public static Creator<MoviesListClass> getCREATOR() {
        return CREATOR;
    }


    private String poster_path;
    private String adult;
    private String overview;
    private String release_date;
    private String gener_ids;
    private String id;
    private String original_title;
    private String original_language;
    private String title;
    private String backdrop_path;
    private String popularity;
    private String vote_count;
    private String video;
    private String vote_average;


    protected MoviesListClass(Parcel in) {
        poster_path = in.readString();
        adult = in.readString();
        overview = in.readString();
        release_date = in.readString();
        gener_ids = in.readString();
        id = in.readString();
        original_title = in.readString();
        original_language = in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        popularity = in.readString();
        vote_count = in.readString();
        video = in.readString();
        vote_average = in.readString();
    }

    public static final Creator<MoviesListClass> CREATOR = new Creator<MoviesListClass>() {
        @Override
        public MoviesListClass createFromParcel(Parcel in) {
            return new MoviesListClass(in);
        }

        @Override
        public MoviesListClass[] newArray(int size) {
            return new MoviesListClass[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(poster_path);
        parcel.writeString(adult);
        parcel.writeString(overview);
        parcel.writeString(release_date);
        parcel.writeString(gener_ids);
        parcel.writeString(id);
        parcel.writeString(original_title);
        parcel.writeString(original_language);
        parcel.writeString(title);
        parcel.writeString(backdrop_path);
        parcel.writeString(popularity);
        parcel.writeString(vote_count);
        parcel.writeString(video);
        parcel.writeString(vote_average);
    }
}
