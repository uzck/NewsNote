package com.example.nozoeli.newsnote.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nozoeli.newsnote.R;
import com.example.nozoeli.newsnote.adapter.AlbumPagerAdapter;
import com.example.nozoeli.newsnote.util.UtilTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nozoeli on 16-4-19.
 */
public class AlbumActivity extends AppCompatActivity {

    private ArrayList<String> urlSets = new ArrayList<>();
    private ArrayList<ImageView> viewSets = new ArrayList<>();
    private ViewPager albumViewPager;
    private AlbumPagerAdapter albumPagerAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        toolbar = (Toolbar) findViewById(R.id.album_toolbar);
        setSupportActionBar(toolbar);

        LinearLayout layout = (LinearLayout) findViewById(R.id.album_status_bar);
        if (layout != null) {
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    UtilTools.getBarHeight(getResources())));
        }


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("Album");
        toolbar.setTitleTextColor(0xFFFFFF);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        albumViewPager = (ViewPager) findViewById(R.id.album_pager);
        albumPagerAdapter = new AlbumPagerAdapter(this, urlSets, viewSets);
        albumViewPager.setAdapter(albumPagerAdapter);
        String link = getIntent().getStringExtra("link");
        String links[];
        links = link.split("\\|");
        getPicJsonData("http://c.3g.163.com/photo/api/jsonp/set/" + links[0] + "/" + links[1] + ".json");

    }

    private void getPicJsonData(String link) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest jsonString = new StringRequest(link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String subString = response.substring(13, response.length());
                try {
                    JSONObject jsonData = new JSONObject(subString);
                    JSONArray photosArray = jsonData.getJSONArray("photos");
                    for (int i = 0; i < photosArray.length(); i++) {
                        urlSets.add(photosArray.getJSONObject(i).getString("imgurl"));
                        viewSets.add(new ImageView(AlbumActivity.this));
                    }
                    albumPagerAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        queue.add(jsonString);
    }
}
