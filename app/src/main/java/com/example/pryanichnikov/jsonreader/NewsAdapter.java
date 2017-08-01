package com.example.pryanichnikov.jsonreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {


    public NewsAdapter(@NonNull Context context, @NonNull List<News> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View result = convertView;
        if(result == null){
            result = LayoutInflater.from(getContext()).inflate(R.layout.template_list_news,parent,false);
        }
        News news = getItem(position);

        TextView date = result.findViewById(R.id.news_date);
        date.setText((CharSequence) news.getDate());
        TextView title = result.findViewById(R.id.news_title);
        title.setText( news.getTitle());
        TextView content = result.findViewById(R.id.news_content);
        content.setText(Html.fromHtml(news.getContent(),Html.FROM_HTML_MODE_COMPACT));

        return result;
    }
}
