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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity implements MovieDetailFragment.OnFragmentInteractionListener{

    private MoviesListClass movie;
    private MovieDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new Explode());
            getWindow().setEnterTransition(new Explode());
        } else {
            // Swap without transition
        }
        setContentView(R.layout.activity_movie_detail);

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
            ft.add(R.id.frame_layout, frag, "movie detail fragment").commit();
        }

        mDb = MovieDatabase.getsInstance(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("movie", movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public MoviesListClass getMovie() {
        return movie;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void findMovie() {
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                 MoviesListClass m =  mDb.moviesDao().findMovie(movie.getId());
                 if(m == null) {
                     addToFavourite();
                 }
                 else {
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             Snackbar.make(findViewById(R.id.frame_layout), "Movie Already Exists !", Snackbar.LENGTH_LONG)
                                     .show();
                         }
                     });
                 }
            }
        });
    }

    void addToFavourite() {
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.moviesDao().saveMovie(movie);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(findViewById(R.id.frame_layout), "Movie Added to Favourite!", Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        deleteFav();
                                    }
                                })
                                .show();
                    }
                });
            }
        });
    }

    void deleteFav() {
        AppExecutors.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.moviesDao().deleteMovie(movie.getId());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(findViewById(R.id.frame_layout), "Movie Deleted from Favourite!", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }


    @Override
    public void addToFavourite(MoviesListClass movie) {
        findMovie();
    }

    @Override
    public void backPressed() {
        onBackPressed();
    }

    @Override
    public void deleteFavourite() {
        deleteFav();
    }
}

