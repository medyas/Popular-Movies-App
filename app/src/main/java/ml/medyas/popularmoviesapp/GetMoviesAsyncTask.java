package ml.medyas.popularmoviesapp;


import android.os.AsyncTask;
import android.util.Log;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.android.core.Json;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.github.kittinunf.result.Result;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import kotlin.Triple;

public class GetMoviesAsyncTask extends AsyncTask<Void, Void, ArrayList<MoviesListClass>>{
    private String url = "";
    private GetMoviesAsyncTaskInterface listener;

    public GetMoviesAsyncTask() {

    }
    public GetMoviesAsyncTask(String url, GetMoviesAsyncTaskInterface mListener) {
        this.url = url;
        this.listener = mListener;
    }

    @Override
    protected ArrayList<MoviesListClass> doInBackground(Void... voids) {
        Log.d("task started", "AsyncTask started !");
        try {
            Triple<Request, Response, Result<String,FuelError>> data = Fuel.get(url).responseString();
            Request request = data.getFirst();
            Response response = data.getSecond();
            Result<String,FuelError> json = data.getThird();
            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<ArrayList<MoviesListClass>>(){}.getType();
            ArrayList<MoviesListClass> movies;
            try {
                movies = gson.fromJson(new JSONObject(json.get()).getJSONArray("results").toString(), listType);
            }
            catch(Exception io) {
                movies = gson.fromJson(new JSONObject(json.get()).toString(), listType);
            }
            return  movies;
        } catch (Exception networkError) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<MoviesListClass> m) {
        if(m == null) {
            listener.onPostExecute(null);
        }
        else {
            listener.onPostExecute(m);
        }

        super.onPostExecute(m);
    }


    interface GetMoviesAsyncTaskInterface {
        public void onPostExecute(ArrayList<MoviesListClass> m);
    }
}
