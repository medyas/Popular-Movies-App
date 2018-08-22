package ml.medyas.popularmoviesapp;

import android.app.ActivityOptions;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteActivity extends AppCompatActivity implements MoviesAdapter.clickedItem{
    @BindView(R.id.fav_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.fav_progressBar)
    ProgressBar progress;
    @BindView(R.id.fav_noResult)
    TextView noResult;

    private MoviesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<MoviesListClass> movieList = new ArrayList<MoviesListClass>();
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
        setContentView(R.layout.activity_favourite);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Favourite Movies");

        mRecyclerView.setHasFixedSize(false);


        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new GridLayoutManager(this, 3);
        }
        else{
            mLayoutManager = new GridLayoutManager(this, 5);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new MoviesAdapter(movieList, this);
        mRecyclerView.setAdapter(mAdapter);

        /*if(savedInstanceState == null) {
            getMovies();
        }
        else {
            ArrayList<MoviesListClass> m = savedInstanceState.getParcelableArrayList("movies");
            movieList = m;
            mAdapter.notifyDataSetChanged();
            progress.setVisibility(View.GONE);
        }*/

        mDb = MovieDatabase.getsInstance(this);
        getMovies();
    }

    void getMovies() {
        //LiveData<ArrayList<MoviesListClass>> movies =  mDb.moviesDao().loadFavMovies();
        //progress.setVisibility(View.VISIBLE);
        MainViewModal viewModal = ViewModelProviders.of(this).get(MainViewModal.class);
        viewModal.getMovies().observe(this, new Observer<List<MoviesListClass>>() {
            @Override
            public void onChanged(@Nullable List<MoviesListClass> moviesListClasses) {
                movieList.clear();
                movieList.addAll(new ArrayList<MoviesListClass>(moviesListClasses));
                mAdapter.notifyDataSetChanged();
                progress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void clickedItemPosition(int position, MoviesListClass movie) {
        Intent intent = new Intent(this, FavouriteMovieDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("movie", movie);
        intent.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

}
