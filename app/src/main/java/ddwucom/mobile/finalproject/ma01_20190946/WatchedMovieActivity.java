package ddwucom.mobile.finalproject.ma01_20190946;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class WatchedMovieActivity extends AppCompatActivity {
    public static final String TAG = "WatchedMovieActivityTag";

    private DrawerLayout mDrawerLayout;
    private Context context = this;

    MovieAdapter adapter;
    ArrayList<NaverMovieDto> resultList;
    ListView listView;

    MovieDBManager movieDBManager;

    EditText et_search;
    ImageView btn_search;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watched_movie);

        et_search = findViewById(R.id.watched_et_search);
        btn_search = findViewById(R.id.watched_btn_search);

        movieDBManager = new MovieDBManager(this);
        Drawable drawble = getResources().getDrawable(R.drawable.moviary);
        Bitmap bitmap = ((BitmapDrawable)drawble).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 40, 50, true));

        Toolbar toolbar = (Toolbar)findViewById(R.id.watched_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // ?????? title ?????????
        actionBar.setDisplayHomeAsUpEnabled(true); // ???????????? ?????? ?????????
        actionBar.setHomeAsUpIndicator(newdrawable); //???????????? ?????? ????????? ??????

        mDrawerLayout = (DrawerLayout) findViewById(R.id.watchedLayout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.watched_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Navigation ????????? ?????????
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();
/*
                if(id == R.id.menu_box){
                    Toast.makeText(context, "???????????????", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WatchedMovieActivity.this, BoxOfficeActivity.class);
                    startActivity(intent);
//                    WatchedMovieActivity.this.finish();
                }*/
                if(id == R.id.menu_movies){
                    Toast.makeText(context, "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.menu_search){
                    Toast.makeText(context, "??????", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WatchedMovieActivity.this, MainActivity.class);
                    startActivity(intent);
//                    WatchedMovieActivity.this.finish();
                }
                else if(id == R.id.menu_favorite){
                    Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WatchedMovieActivity.this, FavoriteMovieActivity.class);
                    startActivity(intent);
//                    WatchedMovieActivity.this.finish();
                }
                else if(id == R.id.menu_theater) {
                    Toast.makeText(context, "?????? ????????? ??????", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WatchedMovieActivity.this, NearByTheaterActivity.class);
                    startActivity(intent);
//                    WatchedMovieActivity.this.finish();
                }

                return true;
            }
        });

        listView = findViewById(R.id.watached_listview);
        resultList = new ArrayList<NaverMovieDto>();
        adapter = new MovieAdapter(this, R.layout.listview_movie, resultList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WatchedMovieActivity.this, DetailWatchedMovieActivity.class);
                intent.putExtra("movie", resultList.get(position));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(WatchedMovieActivity.this);
                builder.setTitle("???????????? ??????")
                        .setMessage("?????? " + resultList.get(pos).getTitle() + "???(???) ?????????????????????????")
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(movieDBManager.removeMovieFromWatched(resultList.get(pos).get_id())) {
                                    Toast.makeText(WatchedMovieActivity.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                                    resultList.clear();
                                    resultList.addAll(movieDBManager.getAllMovieFromWatched());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(WatchedMovieActivity.this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("??????", null)
                        .setCancelable(false)
                        .show();
                return true;
            }
        });

        getAllMovies();
    }
    public void getAllMovies() {
        resultList.clear();
        resultList.addAll(movieDBManager.getAllMovieFromWatched());
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        resultList.clear();
//        resultList.addAll(movieDBManager.getAllMovieFromWatched());
//        adapter.notifyDataSetChanged();
        getAllMovies();
    }
    // ?????? ????????? ?????? ???????????? ??????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // ?????? ?????? ?????? ????????? ???
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.watched_btn_search:
                if (et_search.getText().toString().equals("") || et_search.getText().toString() == null) {
                    Toast.makeText(this, "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
                else {
                    resultList.clear();
                    resultList.addAll(movieDBManager.searchMovieByTitle(et_search.getText().toString()));
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.watched_refresh:
                getAllMovies();
                break;
        }
    }
}
