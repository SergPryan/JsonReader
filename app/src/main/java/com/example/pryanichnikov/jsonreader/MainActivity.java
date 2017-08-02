package com.example.pryanichnikov.jsonreader;


import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private NewsAdapter newsAdapter;

    @Override
    protected void onResume() {
        super.onResume();

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        Task task = new Task();
        task.execute("http://www.allenpike.com/feed.json");
//        List<News> list = null;
//        try {
//            list = task.get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
    }

    private class Task extends AsyncTask<String,Integer,List<News>>{

        private InputStream makeHttpRequest(String url) throws IOException {
            URL tempUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) tempUrl.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            return urlConnection.getInputStream();
        }

        private String readAllLine(InputStream stream) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line=null;
            while ((line=br.readLine()) != null){
                sb.append(line);
            }
            return sb.toString();
        }

        private List<News> parseJson(String json,List<News> list) throws JSONException, IOException {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject tmp = jsonArray.getJSONObject(i);
                News news = new News();
                news.setId(tmp.getString("id"));
                news.setContent(tmp.getString("content_html"));
                news.setTitle(tmp.getString("title"));
                news.setUrl(tmp.getString("url"));
                list.add(news);
            }

            return list;
        }


        @Override
        protected List<News> doInBackground(String... strings) {
            List<News> result = new CopyOnWriteArrayList<>();
            try {
                for(int i=0;i<strings.length;i++){
                    InputStream stream = makeHttpRequest(strings[i]);
                    String json = readAllLine(stream);
                    result = parseJson(json,result);
                }
            }catch (JSONException | IOException e){
                e.printStackTrace();
            }

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<News> newses) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
            ListView listView = (ListView) findViewById(R.id.list_view);
            newsAdapter =new NewsAdapter(getApplicationContext(),newses);
            listView.setAdapter(newsAdapter);

        }


    }



}
