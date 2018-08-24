package ml.medyas.popularmoviesapp;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteMovieDetailActivity extends AppCompatActivity implements MovieDetailFragment.OnFragmentInteractionListener{
    private MoviesListClass movie;
    private MovieDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Fade());
        } else {
            // Swap without transition
        }
        setContentView(R.layout.activity_favourite_movie_detail);
        ButterKnife.bind(this);

        if(savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            movie = bundle.getParcelable("movie");
        }
        else {
            movie = savedInstanceState.getParcelable("movie");
        }

        if (savedInstanceState == null) {
            Bundle b = new Bundle();
            b.putParcelable("movie", movie);
            MovieDetailFragment frag = new MovieDetailFragment();
            frag.setArguments(b);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.layout, frag, "movie detail fragment").commit();
        }

        mDb = MovieDatabase.getsInstance(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie", movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movie = savedInstanceState.getParcelable("movie");
    }

    @Override
    public void addToFavourite(MoviesListClass movie) {

    }

    @Override
    public void backPressed() {
        onBackPressed();
    }

    @Override
    public void deleteFavourite() {
        deleteFav();
    }

    void deleteFav() {
        Toast.makeText(this, "Movie Deleted from Favourite!", Toast.LENGTH_LONG)
            .show();
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.moviesDao().deleteMovie(movie.getId());
                finish();
            }
        });
    }
}
