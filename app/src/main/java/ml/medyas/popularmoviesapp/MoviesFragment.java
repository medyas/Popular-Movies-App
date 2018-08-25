package ml.medyas.popularmoviesapp;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoviesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoviesFragment extends Fragment  implements GetMoviesAsyncTask.GetMoviesAsyncTaskInterface, MoviesAdapter.clickedItem{
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progress;
    @BindView(R.id.noResult)
    TextView noResult;

    private static final String API_KEY = BuildConfig.API_KEY;

    private MoviesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<MoviesListClass> movieList = new ArrayList<MoviesListClass>();
    private int page = 1;
    private int itemPosition = 0;
    private GetMoviesAsyncTask task;
    String sorted = "latest";
    private Context ctx;

    private final String MOVIE_POSITION = "moviePosition";
    private final String MOVIES = "movies";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM = "sort_category";


    private OnFragmentInteractionListener mListener;

    public MoviesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MoviesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoviesFragment newInstance(String param1) {
        MoviesFragment fragment = new MoviesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sorted = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, root);

        progress.setVisibility(View.VISIBLE);

        mRecyclerView.setHasFixedSize(false);

        int mNoOfColumns = Utility.calculateNoOfColumns(getActivity().getApplicationContext());
        mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), mNoOfColumns);

        /*if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), mNoOfColumns);
        }
        else{
            mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), mNoOfColumns);
        }*/
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an adapter (see also next example)
        mAdapter = new MoviesAdapter(movieList, getContext(), this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(getActivity().getApplicationContext(),"Loading ...", Toast.LENGTH_SHORT).show();
                    getMovies();
                }
            }
        });

        if(savedInstanceState == null) {
            getMovies();
        }
        else {
            sorted = savedInstanceState.getString("sorted");
            itemPosition = savedInstanceState.getInt(MOVIE_POSITION);
            ArrayList<MoviesListClass> m = savedInstanceState.getParcelableArrayList(MOVIES);
            movieList.addAll(m);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.getLayoutManager().scrollToPosition(itemPosition);
            progress.setVisibility(View.GONE);
        }

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES, movieList);
        outState.putInt(MOVIE_POSITION, itemPosition);
        outState.putString("sorted", sorted);
        super.onSaveInstanceState(outState);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = context;
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
        progress.setVisibility(View.VISIBLE);
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info != null && info.isConnected()) {
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
        Intent intent = new Intent(getActivity().getApplicationContext(), MovieDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("movie", movie);
        intent.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        } else {
            startActivity(intent);
        }
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
        void onFragmentInteraction(Uri uri);
    }
}
