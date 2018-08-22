package ml.medyas.popularmoviesapp;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GetMoviesAsyncTask.GetMoviesAsyncTaskInterface, MoviesAdapter.clickedItem{
    @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.progressBar) ProgressBar progress;
    @BindView(R.id.noResult) TextView noResult;

    private static final String API_KEY = BuildConfig.API_KEY;

    private MoviesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<MoviesListClass> movieList = new ArrayList<MoviesListClass>();
    private int page = 1;
    private int sortNumber = 1;
    private int itemPosition = 0;
    private GetMoviesAsyncTask task;

    private final String MOVIE_POSITION = "moviePosition";
    private final String MOVIES = "movies";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        progress.setVisibility(View.VISIBLE);

        mRecyclerView.setHasFixedSize(false);

        getSupportActionBar().setTitle("Now Playing");


        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new GridLayoutManager(this, 3);
        }
        else{
            mLayoutManager = new GridLayoutManager(this, 5);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an adapter (see also next example)
        mAdapter = new MoviesAdapter(movieList, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(getApplicationContext(),"Loading ...", Toast.LENGTH_SHORT).show();
                    getMovies();
                }
            }
        });

        if(savedInstanceState == null) {
            getMovies();
        }
        else {
            itemPosition = savedInstanceState.getInt(MOVIE_POSITION);
            ArrayList<MoviesListClass> m = savedInstanceState.getParcelableArrayList(MOVIES);
            movieList.addAll(m);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.getLayoutManager().scrollToPosition(itemPosition);
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES, movieList);
        outState.putInt(MOVIE_POSITION, itemPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int mItem = item.getItemId();
        switch(mItem) {
            case R.id.menu_main_search:
                Log.d("search clicked", "clicked search menu item");
                return true;
            case R.id.menu_main_sort:
                /*
            Solution found in https://stackoverflow.com/a/19116705/8738574
         */
                AlertDialog.Builder mySortAlertDialog = new AlertDialog.Builder(this);
                mySortAlertDialog.setTitle("Sort By :");
                String[] r = {"Latest", "Now Playing", "Popular", "Top Rated", "Upcoming"};
                mySortAlertDialog.setSingleChoiceItems(r, sortNumber, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        page = (sortNumber != ((AlertDialog)dialogInterface).getListView().getCheckedItemPosition())? 1: page;
                        sortNumber = ((AlertDialog)dialogInterface).getListView().getCheckedItemPosition();
                        getMovies();
                        dialogInterface.dismiss();
                    }
                });
                mySortAlertDialog.create().show();
                return true;
            case R.id.menu_main_fav:
                Intent intent = new Intent(this, FavouriteActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent,
                            ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                } else {
                    startActivity(intent);
                }
                return true;
            case R.id.menu_main_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostExecute(ArrayList<MoviesListClass> m) {
        progress.setVisibility(View.GONE);
        if(m == null) {
            Log.d("task finished", "null object !");
            movieList.clear();
            mAdapter.notifyDataSetChanged();
            noResult.setVisibility(View.VISIBLE);
        }
        else {
            if(page == 1 ){
                try {
                    movieList.clear();
                    mAdapter.notifyDataSetChanged();
                }
                catch(NullPointerException e) {

                }

            }
            try {
                movieList.addAll(m);
                mAdapter.notifyDataSetChanged();
            }
            catch (NullPointerException e) {

            }

            page +=1;
            Log.d("task finished", "Done with the data");
        }
    }

    private void getMovies() {
        noResult.setVisibility(View.GONE);
        String sorted = "";
        progress.setVisibility(View.VISIBLE);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info != null && info.isConnected()) {
            switch (sortNumber) {
                case 0:
                    sorted = getString(R.string.latest);
                    getSupportActionBar().setTitle("Latest");
                    break;
                case 1:
                    sorted = getString(R.string.playing);
                    getSupportActionBar().setTitle("Now Playing");
                    break;
                case 2:
                    sorted = getString(R.string.popular);
                    getSupportActionBar().setTitle("Popular");
                    break;
                case 3:
                    sorted = getString(R.string.top);
                    getSupportActionBar().setTitle("Top Rated");
                    break;
                case 4:
                    sorted = getString(R.string.upcoming);
                    getSupportActionBar().setTitle("Upcoming");
                    break;
            }
            if(task != null)
                task.cancel(true);

            task = new GetMoviesAsyncTask(getString(R.string.movieDB, sorted, API_KEY)+((sorted.equals("latest") == false)?"&page="+String.valueOf(page):""), this );
            task.execute();
        }
        else {
            Log.d("NetError", "Network Error - No Internet Access");
            progress.setVisibility(View.GONE);
        }
    }


    @Override
    public void clickedItemPosition(int position, MoviesListClass movie) {
        itemPosition = position;
        Intent intent = new Intent(this, MovieDetailActivity.class);
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
