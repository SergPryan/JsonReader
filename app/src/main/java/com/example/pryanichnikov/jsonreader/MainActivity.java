package com.example.pryanichnikov.jsonreader;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.ProgressBar;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progress_start_application);
        progressBar.setMax(10);
        Task task = new Task();
        task.execute();
        List<News> list = null;
        try {
            list = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.list_view);
        ListView listView = (ListView) findViewById(R.id.list_view);
        NewsAdapter newsAdapter =new NewsAdapter(this,list);
        listView.setAdapter(newsAdapter);
    }

    class Task extends AsyncTask<Void,Integer,List<News>>{

        @Override
        protected void onProgressUpdate(Integer... values) {
            System.out.println(values[0]);
            progressBar.setProgress(values[0]);
        }

        private List<News> makeHttpRequest() throws IOException, JSONException {
            URL url = new URL("http://www.allenpike.com/feed.json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line=null;
            while ((line=br.readLine()) != null){
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            List<News> list = new ArrayList<>();

            for(int i=0;i<jsonArray.length();i++){
              JSONObject tmp = jsonArray.getJSONObject(i);
                News news = new News();
                news.setId(tmp.getString("id"));
                news.setContent(tmp.getString("content_html"));
//                news.setDate(Date.valueOf(tmp.getString("date_published")));
                news.setTitle(tmp.getString("title"));
                news.setUrl(tmp.getString("url"));
                list.add(news);
            }

            return list;
        }

        @Override
        protected List<News> doInBackground(Void... voids) {
            List<News> result = null;
            try {
                result =  makeHttpRequest();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }
    }



}
