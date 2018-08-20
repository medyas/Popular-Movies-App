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
import android.text.Html;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class MovieDetailActivity extends AppCompatActivity implements MovieDetailFragment.OnFragmentInteractionListener{
    private MoviesListClass movie;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if(savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            movie = bundle.getParcelable("movie");
        }
        else {
            movie = savedInstanceState.getParcelable("movie");
        }

        getSupportActionBar().setTitle(movie.getTitle());

        if (savedInstanceState == null) {
            Bundle b = new Bundle();
            b.putParcelable("movie", movie);
            MovieDetailFragment frag = new MovieDetailFragment();
            frag.setArguments(b);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.layout, frag, "movie detail fragment").commit();
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int mItem = item.getItemId();
        switch (mItem) {
            case R.id.menu_detail_fav:
                addToFavourite();
                return true;
            case R.id.menu_detail_share:
                requestRead();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void addToFavourite() {
        Snackbar.make(findViewById(R.id.frame_layout), movie.getTitle()+" Added to Favourite!", Snackbar.LENGTH_LONG)
        .setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        })
        .show();
    }

    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            shareMovie();
        }
    }

    public void shareMovie() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        BitmapDrawable drawable = (BitmapDrawable) ((ImageView) findViewById(R.id.movie_poster)).getDrawable();
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), drawable.getBitmap(), movie.getTitle(), null);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(bitmapPath));
        shareIntent.putExtra(Intent.EXTRA_TITLE, movie.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new StringBuilder()
                .append("<p>"+movie.getOverview()+"</p>")
                .toString()));
        startActivity(Intent.createChooser(shareIntent, "Share Movie using"));
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}

