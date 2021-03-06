package ddwucom.mobile.finalproject.ma01_20190946;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetailMovieActivity extends AppCompatActivity {

    ImageView poster;
    TextView title;
    TextView subTitle;
    TextView pubDate;
    TextView director;
    TextView actor;
    RatingBar ratingBar;
    Button hyperBtn;
    Button saveBtn;
    Button favoriteBtn;
    Button searchBtn;

    NaverMovieDto movie;

    private NaverNetworkManager networkManager = null;
    private ImageFileManager imageFileManager = null;

    AlertDialog.Builder builder;
    AlertDialog add_dialog;
    View custom_add_dialog;

    MovieDBManager movieDBManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        imageFileManager = new ImageFileManager(this);
        networkManager = new NaverNetworkManager(this);
        movieDBManager = new MovieDBManager(this);

        movie = (NaverMovieDto)getIntent().getSerializableExtra("movie");

        poster = findViewById(R.id.detail_poster);
        title = findViewById(R.id.detail_title);
        subTitle = findViewById(R.id.detail_subtitle);
        pubDate = findViewById(R.id.detail_pub);
        director = findViewById(R.id.detail_director);
        actor = findViewById(R.id.detail_actor);
        ratingBar = findViewById(R.id.detail_rating);
        hyperBtn = findViewById(R.id.detail_hyper);
        saveBtn = findViewById(R.id.detail_savebtn);
        favoriteBtn = findViewById(R.id.detail_favoritebtn);
        searchBtn = findViewById(R.id.detail_searchBtn);

        if(movie.getImageLink() == null) {
            poster.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Bitmap savedBitmap = imageFileManager.getBitmapFromTemporary(movie.getImageLink());
            if (savedBitmap != null) {
                poster.setImageBitmap(savedBitmap);
            } else { //null?????? ??????????????? ?????? ???????????? ???????????? ?????? ????????? ??????!
                //????????? (??????????????? ????????? ?????? ???????????? ?????? ???????????? ??????)
                poster.setImageResource(R.mipmap.ic_launcher);
                poster.setImageBitmap(savedBitmap);
                new GetImageAsyncTask().execute(movie.getImageLink());
            }
        }

        title.setText(movie.getTitle());
        subTitle.setText(movie.getSubTitle());
        pubDate.setText(String.valueOf(movie.getPubDate()));
        director.setText(movie.getDirector());
        actor.setText(movie.getActor());
        ratingBar.setRating(movie.getUserRating());

    }
    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        String imageAddress;

        @Override
        protected Bitmap doInBackground(String... params) {
            imageAddress = params[0];
            Bitmap result = null;
            //1??? ?????? ??????
            result = networkManager.downloadImage(imageAddress);

            return result;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            /*?????????????????? ?????? ?????? ????????? ????????? (null??? ????????????) ImageFileManager ??? ???????????? ?????????????????? ??????
             * ???????????? bitmap ??? ??????????????? ??????*/
            if (bitmap != null) {
                poster.setImageBitmap(bitmap);
                imageFileManager.saveBitmapToTemporary(bitmap, imageAddress);
            }
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_savebtn:
                builder = new AlertDialog.Builder(DetailMovieActivity.this);
                custom_add_dialog = View.inflate(DetailMovieActivity.this, R.layout.dialog_review_movie, null);

                EditText et_rating = custom_add_dialog.findViewById(R.id.add_rating);
                EditText et_review = custom_add_dialog.findViewById(R.id.add_review);
                Button btn_save = custom_add_dialog.findViewById(R.id.add_savebtn);
                Button btn_close = custom_add_dialog.findViewById(R.id.add_closebtn);

                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (et_rating.getText().toString().equals("")) {
                            Toast.makeText(DetailMovieActivity.this, "????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        }
                        else if (et_review.getText().toString().equals("")) {
                            Toast.makeText(DetailMovieActivity.this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            movie.setPersonalRating(Float.valueOf(et_rating.getText().toString()));
                            movie.setReview(et_review.getText().toString());
                            // WATCHED??? ??????
                            boolean result = movieDBManager.insertMovieToWatched(movie);
                            if (result) {
                                Toast.makeText(DetailMovieActivity.this,"????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                Toast.makeText(DetailMovieActivity.this, "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add_dialog.dismiss();
                    }
                });

                builder.setView(custom_add_dialog);
                add_dialog = builder.create();
                add_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                add_dialog.show();

                break;
            case R.id.detail_favoritebtn:
                boolean result = movieDBManager.insertMovieToFavorite(movie);
                if (result) {
                    Toast.makeText(DetailMovieActivity.this,"????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(DetailMovieActivity.this, "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.detail_hyper:
                Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(movie.getHyperLink()));
                startActivity(intent);
                break;
            case R.id.detail_searchBtn:
                Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
                searchIntent.putExtra(SearchManager.QUERY, "?????? " + movie.getTitle());

                if (searchIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(searchIntent);
                } else {
                    String msg = "??? ??????????????? ????????? ??? ????????????.";
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
}
