package com.example.movies.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.movies.popularmovies.objects.MovieObject;
import com.squareup.picasso.Picasso;

public class DetailMovieActivity extends AppCompatActivity {

    private static final String LOG_TAG = "DetailMovie";

    ImageView headerImageView;
    ImageView posterImageView;
    TextView titleTextView;
    TextView releaseDateTextView;
    TextView userRatingTextView;
    TextView descriptionTextView;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        Intent i = getIntent();
        MovieObject movieObject = i.getParcelableExtra(getString(R.string.intent_movie_object));
        //Log.d(LOG_TAG, movieObject.toString());

        headerImageView = findViewById(R.id.header_im);
        posterImageView = findViewById(R.id.movie_poster_im);
        titleTextView = findViewById(R.id.title_tv);
        releaseDateTextView = findViewById(R.id.release_date_tv);
        userRatingTextView = findViewById(R.id.user_rating_tv);
        descriptionTextView = findViewById(R.id.description_tv);
        ratingBar = findViewById(R.id.ratingBar);

        if (movieObject != null) {
            String title = movieObject.getTitle() != null ? movieObject.getTitle() : "";
            setTitle(title);
            titleTextView.setText(movieObject.getTitle());

            if (movieObject.getBackdropPath() != null) {
                Picasso.with(this).load(getString(R.string.headerImagePath) + movieObject.getBackdropPath()).into(headerImageView);
            }
            if (movieObject.getPosterPath() != null) {
                Picasso.with(this).load(getString(R.string.posterImagePath) + movieObject.getPosterPath()).into(posterImageView);
            }

            if (movieObject.getReleaseDate() != null) {
                releaseDateTextView.setText(movieObject.getReleaseDate());
            }

            if (movieObject.getVoteAverage() != null) {
                String voteAverage = String.valueOf(movieObject.getVoteAverage()) + getString(R.string.divide_ten);
                userRatingTextView.setText(voteAverage);
                float ratingStars = (float) (movieObject.getVoteAverage() / 2);
                ratingBar.setRating(ratingStars);
            }

            if (movieObject.getOverview() != null) {
                descriptionTextView.setText(movieObject.getOverview());
            }
        }
    }
}
