package ml.medyas.popularmoviesapp;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private ArrayList<MoviesListClass> mMovieList;
    private Context context;
    private clickedItem mListener;

    interface clickedItem {
        void clickedItemPosition(int position, MoviesListClass movie);
    }

    MoviesAdapter(ArrayList<MoviesListClass> mData, Context context, clickedItem listener) {
        mMovieList = mData;
        this.context = context;
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        RelativeLayout layout;
        TextView year;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image_view);
            year = itemView.findViewById(R.id.release_year);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.clickedItemPosition(getAdapterPosition(), mMovieList.get(getAdapterPosition()));

                }
            });
        }
    }


    @NonNull
    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_item_layout, viewGroup, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        MoviesListClass movie = mMovieList.get(viewHolder.getAdapterPosition());
        viewHolder.year.setText(movie.getRelease_date().substring(0, 4));
        Picasso.get().load(context.getResources().getString(R.string.imageUrl)+movie.getPoster_path())
                .placeholder(R.drawable.ic_local_movies_black_24dp)
                .error(R.drawable.ic_local_movies_black_24dp)
                .into(viewHolder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

}
