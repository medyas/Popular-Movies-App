package ml.medyas.popularmoviesapp;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;


@Database(entities = {MoviesListClass.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {
    private static final String Log_Tag = MovieDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "favourite_movies";
    private static MovieDatabase sInstance;

    public static MovieDatabase getsInstance(Context context) {
        if(sInstance == null) {
            synchronized (LOCK) {
                Log.d(Log_Tag, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MovieDatabase.class, MovieDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(Log_Tag, "Getting the database instance");
        return sInstance;
    }

    public abstract MoviesDao moviesDao();

}
