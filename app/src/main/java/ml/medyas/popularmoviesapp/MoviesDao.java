package ml.medyas.popularmoviesapp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MoviesDao {

    @Query("SELECT * from favourite_movies")
    LiveData<List<MoviesListClass>> loadFavMovies();

    @Query("SELECT * from favourite_movies where id= :id")
    MoviesListClass findMovie(String id);

    @Query("DELETE FROM favourite_movies WHERE id = :id")
    void deleteMovie(String id);

    @Insert
    void saveMovie(MoviesListClass movie);
}
