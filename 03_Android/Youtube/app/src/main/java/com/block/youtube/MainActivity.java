package com.block.youtube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.block.youtube.adapter.RecyclerViewAdapter;
import com.block.youtube.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String youtubeUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&key=AIzaSyAleEFgJqgWcfAI5Bsh2z96MjGCT7HmRZU&maxResults=20&order=date&type=video&type=video";
    RequestQueue requestQueue;

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<Video> videoArrayList = new ArrayList<>();

    EditText editSearch;
    ImageView imgSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        editSearch = findViewById(R.id.editSearch);
        imgSearch = findViewById(R.id.imgSearch);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = editSearch.getText().toString().trim();
                String searhUrl = "";
                if(word.isEmpty()){
                    searhUrl = youtubeUrl;
                }else{
                    searhUrl = youtubeUrl + "&q=" + word;
                }
                // 새로 바뀐 검색어로 데이터를 가져오기 위해서, 원래 들어있던 어레이리스트의
                // 데이터를 모드 지우고, 새로 받아온다.
                videoArrayList.clear();
                getNetworkData(searhUrl);

            }
        });


        // 발리 라이브러리 이용해서, 호출
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        getNetworkData(youtubeUrl);

    }

    public void getNetworkData(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("AAA", response.toString());
                        try {
                            JSONArray items = response.getJSONArray("items");
                            for(int i = 0; i < items.length(); i++){
                                JSONObject jsonObject = items.getJSONObject(i);
                                JSONObject id = jsonObject.getJSONObject("id");
                                String videoId = id.getString("videoId");
                                JSONObject snippet = jsonObject.getJSONObject("snippet");
                                String title = snippet.getString("title");
                                String desc = snippet.getString("description");
                                JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                JSONObject def = thumbnails.getJSONObject("medium");
                                String url = def.getString("url");

                                Video video = new Video(title, desc, url, videoId);
                                videoArrayList.add(video);
//                                Log.i("AAA", videoId +","+title+", "+desc+", "+url);
                            }
                            recyclerViewAdapter = new RecyclerViewAdapter(
                                    MainActivity.this,videoArrayList);
                            recyclerView.setAdapter(recyclerViewAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

}