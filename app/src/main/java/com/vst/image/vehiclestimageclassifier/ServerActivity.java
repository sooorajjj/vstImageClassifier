package com.vst.image.vehiclestimageclassifier;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vst.image.vehiclestimageclassifier.Constants.TAG_ID;

public class ServerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //GridView Object
    private GridView gridView;

    //ArrayList for Storing image urls and titles
    private ArrayList<String> ids;
    private ArrayList<String> folder_names;
    private ArrayList<String> file_names;
    private ArrayList<String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_server);
        navigationView.setNavigationItemSelectedListener(this);

        gridView = (GridView) findViewById(R.id.gv_images);

        ids = new ArrayList<>();
        folder_names = new ArrayList<>();
        file_names = new ArrayList<>();
        urls = new ArrayList<>();

        getData();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (id == R.id.nav_upload) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_view_image) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_download) {
            Intent intent = new Intent(this, DownloadActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Toast.makeText(this, "Share to SocialMedia is [WIP]\n",
                    Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_about) {
            Toast.makeText(this, "About Screen is [WIP]\n",
                    Toast.LENGTH_SHORT).show();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getData(){
        //Showing a progress dialog while our app fetches the data from url
        final ProgressDialog loading = ProgressDialog.show(this, "Please wait...","Fetching data...",false,false);

        //Creating a json array request to get the json from our api
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Constants.IMAGES_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Dismissing the progressdialog on response
                        loading.dismiss();

                        //Displaying our grid
                        showGrid(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding our request to the queue
        requestQueue.add(jsonArrayRequest);
    }

    private void showGrid(JSONArray jsonArray){
        //Looping through all the elements of json array
        for(int i = 0; i<jsonArray.length(); i++){
            //Creating a json object of the current index
            JSONObject obj = null;
            try {
                //getting json object from current index
                obj = jsonArray.getJSONObject(i);

                //getting image url and title from json object
                ids.add(obj.getString(TAG_ID));
                folder_names.add(obj.getString(Constants.TAG_FOLDER_NAME));
                file_names.add(obj.getString(Constants.TAG_FILE_NAME));
                urls.add(obj.getString(Constants.TAG_URL));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Creating GridViewAdapter Object
        GridViewAdapter gridViewAdapter = new GridViewAdapter(this,ids,folder_names,file_names,urls);

        //Adding adapter to gridview
        gridView.setAdapter(gridViewAdapter);
    }
}
