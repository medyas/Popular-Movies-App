package ml.medyas.popularmoviesapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.github.kittinunf.result.Result;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Triple;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MovieDetailFragment extends Fragment {
    public static final String HTTP_WWW_YOUTUBE_COM_WATCH_V = "http://www.youtube.com/watch?v=";
    @BindView(R.id.imageView) ImageView bigImage;
    @BindView(R.id.movie_poster) ImageView poster;
    @BindView(R.id.movie_title) TextView title;
    @BindView(R.id.movie_overview) TextView overview;
    @BindView(R.id.movie_year) TextView year;
    @BindView(R.id.movie_pop) TextView pop;
    @BindView(R.id.movie_vote) TextView vote;
    @BindView(R.id.movie_p) TextView p;
    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.review_author) TextView author;
    @BindView(R.id.review_content) TextView content;
    @BindView(R.id.next_review) ImageView nextReview;
    @BindView(R.id.error_msg) TextView msg;
    @BindView(R.id.back_button) ImageView backButton;
    @BindView(R.id.share_button) ImageView shareMovie;
    @BindView(R.id.play_trailer) ImageView playTrailer;
    @BindView(R.id.del_fav) ImageView delete_fav;
    @BindView(R.id.fab_fav) FloatingActionButton favourite;

    private static final String API_KEY = BuildConfig.API_KEY;

    private String videoKey[];
    private String videoName[];
    private boolean gotTrailers = false;

    private ArrayList<ReviewClass> reviewsList = new ArrayList<ReviewClass>();
    private int reviewPosition = 0;
    private boolean taskFinished = false;
    private MoviesListClass movie;
    Bitmap img;
    boolean toggle = false;

    private OnFragmentInteractionListener mListener;

    public MovieDetailFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        movie = getArguments().getParcelable("movie");
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, root);
        content.setMovementMethod(new ScrollingMovementMethod());

        if(savedInstanceState != null) {
            videoKey = new String[savedInstanceState.getStringArray("videoKey").length];
            videoKey = savedInstanceState.getStringArray("videoKey");
            videoName = new String[savedInstanceState.getStringArray("videoTrailers").length];
            videoName =  savedInstanceState.getStringArray("videoTrailers");
            reviewPosition = savedInstanceState.getInt("reviewPosition");
            reviewsList.addAll(savedInstanceState.<ReviewClass>getParcelableArrayList("reviewsList"));
            tabs.getTabAt(savedInstanceState.getInt("tab")).select();
            showSelected((savedInstanceState.getInt("tab") == 0 ? 1: 0));
            gotTrailers = true;
            if(reviewsList.isEmpty()) {
                taskFinished = true;
                msg.setText("No data to display!");
            }
            else {
                fillReviews();
            }

        }
        else {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if(info != null && info.isConnected()) {
                new asyncGetVid(getString(R.string.movieVid, movie.getId(), API_KEY)).execute();
                new asyncGetReviews(getString(R.string.reviewUrl, movie.getId(), API_KEY)).execute();
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(), "No internet connection available!", Toast.LENGTH_LONG);
            }
        }

        Picasso.get().load(getString(R.string.imageUrlOrg) + movie.getBackdrop_path())
                .placeholder(R.drawable.ic_local_movies_black_24dp)
                .error(R.drawable.ic_local_movies_black_24dp)
                .into(bigImage);

        Picasso.get().load(getString(R.string.imageUrl) + movie.getPoster_path())
                .placeholder(R.drawable.ic_local_movies_black_24dp)
                .error(R.drawable.ic_local_movies_black_24dp)
                .into(poster, new Callback() {
            @Override
            public void onSuccess() {
                BitmapDrawable drawable = (BitmapDrawable) poster.getDrawable();
                img = drawable.getBitmap();
            }

            @Override
            public void onError(Exception e) {

            }
        });

        title.setText(movie.getOriginal_title());
        overview.setText(movie.getOverview());
        year.setText(movie.getRelease_date().substring(0, 4));
        pop.setText(movie.getVote_count());
        vote.setText(movie.getVote_average() + "/10");
        p.setText(movie.getPopularity());

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                msg.setVisibility(View.GONE);
                showSelected(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        nextReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!reviewsList.isEmpty()) {
                    if(reviewsList.size()-1>= reviewPosition+1) {
                        reviewPosition++;
                        fillReviews();
                    }
                    else {
                        reviewPosition = 0;
                        fillReviews();
                    }
                }
            }
        });


        playTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gotTrailers) {
                    showDialog();
                }
                else {
                    Toast.makeText(getActivity(), "Still Loading Trailers...", Toast.LENGTH_LONG);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.backPressed();
            }
        });

        shareMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRead();
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addToFavourite(movie);
            }
        });

        delete_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.deleteFavourite();
            }
        });

        if(toggle) {
            delete_fav.setVisibility(View.GONE);
        }
        else {
            favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
        }

        return root;
    }

    private void showDialog() {
        AlertDialog builderSingle = new AlertDialog.Builder(getActivity())
                .setTitle("Select Trailer")
                .setIcon(R.drawable.ic_local_movies_black_24dp)
                .setItems(videoName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                        watchInYoutube(videoKey[selectedIndex]);
                    }
                })
                .create();
        builderSingle.show();
    }

    void showSelected(int p) {
        if (p == 0) {
            overview.setVisibility(View.GONE);
            if(!reviewsList.isEmpty()) {
                author.setVisibility(View.VISIBLE);
                content.setVisibility(View.VISIBLE);
                nextReview.setVisibility(View.VISIBLE);
            }
            else {
                msg.setVisibility(View.VISIBLE);
                if(taskFinished) {
                    msg.setText(getResources().getString(R.string.no_thing_to_display));
                }
                else {
                    msg.setText(getResources().getString(R.string.getting_data));
                }
            }
        } else {
            overview.setVisibility(View.VISIBLE);
            author.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
            nextReview.setVisibility(View.GONE);
        }
    }

    void fillReviews() {
        author.setText(reviewsList.get(reviewPosition).getAuthor());
        content.setText(reviewsList.get(reviewPosition).getContent());
    }

    void watchInYoutube(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(HTTP_WWW_YOUTUBE_COM_WATCH_V + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    void watchInBrowser(String id) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(HTTP_WWW_YOUTUBE_COM_WATCH_V + id));
        startActivity(webIntent);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() instanceof MovieDetailActivity) {
            toggle = true;
        }
        else {
            toggle = false;
        }
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("tab", tabs.getSelectedTabPosition());
        outState.putStringArray("videoKey", videoKey);
        outState.putStringArray("videoTrailers", videoName);
        outState.putInt("reviewPosition", reviewPosition);
        outState.putParcelableArrayList("reviewsList", reviewsList);
        super.onSaveInstanceState(outState);
    }

    public void requestRead() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            shareMovie();
        }
    }

    public void shareMovie() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        BitmapDrawable drawable = (BitmapDrawable) poster.getDrawable();
        String bitmapPath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), drawable.getBitmap(), movie.getTitle(), null);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(bitmapPath));
        shareIntent.putExtra(Intent.EXTRA_TITLE, movie.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new StringBuilder()
                .append("<p>"+movie.getTitle()+"</p>")
                .append("<p>"+movie.getOverview()+"</p>")
                .append("<p><b>Movie Video Trailer    http://www.youtube.com/watch?v="+videoKey+"</b></p>")
                .toString()));
        startActivity(Intent.createChooser(shareIntent, "Share Movie using"));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void addToFavourite(MoviesListClass movie);
        void backPressed();
        void deleteFavourite();
    }


    class asyncGetVid extends AsyncTask<Void, Void, String> {
        private String url;

        asyncGetVid(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.d("task started", "asyncGetVid started !");
            try {
                Triple<Request, Response, Result<String,FuelError>> data = Fuel.get(url).responseString();
                Request request = data.getFirst();
                Response response = data.getSecond();
                Result<String,FuelError> json = data.getThird();
                return json.get();
            } catch (Exception networkError) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String str) {
            try {
                JSONObject temp = new JSONObject(str);
                JSONArray json = temp.optJSONArray("results");
                int length = json.length();
                videoName = new String[length];
                videoKey  = new String[length];

                for (int i = 0; i < length; i++) {
                    videoKey[i] = json.optJSONObject(i).optString("key");
                    videoName[i] = json.optJSONObject(i).optString("name");
                }
                gotTrailers = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    class asyncGetReviews extends AsyncTask<Void, Void, ArrayList<ReviewClass>> {
        private String url;

        asyncGetReviews(String url) {
            this.url = url;
        }

        @Override
        protected ArrayList<ReviewClass> doInBackground(Void... voids) {
            Log.d("task started", "asyncGetReviews started !");
            try {
                Triple<Request, Response, Result<String,FuelError>> data = Fuel.get(url).responseString();
                Request request = data.getFirst();
                Response response = data.getSecond();
                Result<String,FuelError> json = data.getThird();
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<ArrayList<ReviewClass>>(){}.getType();
                ArrayList<ReviewClass> reviewsList;
                reviewsList = gson.fromJson(new JSONObject(json.get()).getJSONArray("results").toString(), listType);
                return  reviewsList;
            } catch (Exception networkError) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<ReviewClass> r) {
            Log.d("task finished", "asyncGetReviews finished !");
            taskFinished = true;
            if(!r.isEmpty()) {
                reviewsList = r;
                fillReviews();
            }
        }
    }
}


