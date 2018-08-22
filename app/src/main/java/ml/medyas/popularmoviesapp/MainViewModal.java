package ml.medyas.popularmoviesapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainViewModal extends AndroidViewModel {
    private LiveData<List<MoviesListClass>> movies;

    public MainViewModal(@NonNull Application application) {
        super(application);
        MovieDatabase db = MovieDatabase.getsInstance(this.getApplication());
        Log.d("MainViewModal", "Getting movies list");
        movies = db.moviesDao().loadFavMovies();
    }

    public LiveData<List<MoviesListClass>> getMovies() {
        return movies;
    }
}
