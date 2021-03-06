package ddwucom.mobile.finalproject.ma01_20190946;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivityTag";
    final static int DETAIL_ACTIVITY_CODE = 100;
    private final static int MY_PERMISSIONS_REQ_LOC = 200;

    private DrawerLayout mDrawerLayout;
    private Context context = this;

    String query;
    String apiAddress;
    EditText et_search;
    ImageView btn_search;

    MovieAdapter adapter;

    ArrayList<NaverMovieDto> resultList;
    ListView listView;
    NaverMovieXmlParser parser;
    NaverNetworkManager networkManager;
    ImageFileManager imageFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Drawable drawble = getResources().getDrawable(R.drawable.moviary);
        Bitmap bitmap = ((BitmapDrawable)drawble).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 40, 50, true));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // ?????? title ?????????
        actionBar.setDisplayHomeAsUpEnabled(true); // ???????????? ?????? ?????????
        actionBar.setHomeAsUpIndicator(newdrawable); //???????????? ?????? ????????? ??????

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // Navigation ????????? ?????????
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
/*
                if(id == R.id.menu_box){
                    Toast.makeText(context, "???????????????", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, BoxOfficeActivity.class);
                    startActivity(intent);
//                    MainActivity.this.finish();

                }*/
                if(id == R.id.menu_movies){
                    Toast.makeText(context, "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, WatchedMovieActivity.class);
                    startActivity(intent);
//                    MainActivity.this.finish();
                }
                else if(id == R.id.menu_search){
                    Toast.makeText(context, "??????", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.menu_favorite){
                    Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, FavoriteMovieActivity.class);
                    startActivity(intent);
//                    MainActivity.this.finish();
                }
                else if(id == R.id.menu_theater) {
                    Toast.makeText(context, "?????? ????????? ??????", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, NearByTheaterActivity.class);
                    startActivity(intent);
//                    MainActivity.this.finish();
                }

                return true;
            }
        });

        et_search = findViewById(R.id.et_search);
        btn_search = findViewById(R.id.btn_search);
        listView = findViewById(R.id.listview);

        resultList = new ArrayList();
//        adapter = new MyMovieAdapter(this, R.layout.listview_movie, movieList);
        adapter = new MovieAdapter(this, R.layout.listview_movie, resultList);
        listView.setAdapter(adapter);
//        apiAddress = "https://api.themoviedb.org/3/search/movie?api_key=7585d0a9fd60f897846775ad25e13099&language=ko-KR&page=1&query=";
        apiAddress = getResources().getString(R.string.api_url);
        parser = new NaverMovieXmlParser();
        networkManager = new NaverNetworkManager(this);
        networkManager.setClientId(getResources().getString(R.string.client_id));
        networkManager.setClientSecret(getResources().getString(R.string.client_secret));
        imageFileManager = new ImageFileManager(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NaverMovieDto movie = adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, DetailMovieActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });

        checkPermission();
    }
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // FINE??? ????????? ?????? COARSE??? ??????????????? ?????????
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // ????????? ?????? ?????? ?????? ?????? ????????? ??????(???????????????)
                // '?????? ?????? ??????'??? ?????? ??????????????? ?????? ????????? ?????? ?????????????????? ???????????? ???
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQ_LOC);
                return false;
            } else
                // ????????? ?????? ?????? ?????? ???
                return true;
        }
        return false;
    }

    /*???????????? ????????? ?????? ???????????? ?????? ????????? ?????? ??????*/
    // ???????????? ?????? ?????????
    // ???????????? ?????? ?????? ????????? ?????? ???????????? ?????? ???/????????? ???????????? req.code ???????????? ?????? ??????

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQ_LOC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*????????? ??????????????? ??? ??????????????? ?????? ?????? ??????*/
                } else {
                    /*??????????????? ?????? ????????? ?????? ??????*/
                    Toast.makeText(this, "Permissions are not granted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ?????? ?????? ??????
        imageFileManager.clearTemporaryFiles();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                query = et_search.getText().toString();
                // OpenAPI ????????? query ?????? ??? ???????????? ???????????? ?????????
                // ????????? ???????????? ?????? ?????? ??? ???????????? ??????
                if (query.equals("")) {
                    Toast.makeText(this, "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        new NetworkAsyncTask().execute(apiAddress + URLEncoder.encode(query, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(MainActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = null;
            // networking
            result = networkManager.downloadContents(address);
            // ???????????? ?????? ??????
            if(result == null) return "Error!";
            Log.d(TAG, result);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            resultList = parser.parse(result);
            if (resultList != null) {
                for (NaverMovieDto dto : resultList) {
                    Bitmap bitmap = networkManager.downloadImage(dto.getImageLink());
                    if (bitmap != null) {
                        Log.d(TAG, "bitmap is not null");
                        imageFileManager.saveBitmapToTemporary(bitmap, dto.getImageLink());
                    }
                }
                // notify?????? ?????????
                adapter.setList(resultList);
            }
            progressDlg.dismiss();
        }
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
}