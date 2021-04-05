package com.example.nested;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<News> newsArray;


    public NewsAdapter(ArrayList<News> newsArray, Context context) {
        this.context = context;
        this.newsArray = newsArray;
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item,
                parent, false);

        return new NewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {

        holder.newsheader.setText(newsArray.get(position).getHeadline());
        holder.newsdate.setText(newsArray.get(position).getDate());
        holder.newssource.setText(newsArray.get(position).getSource());

        if(position%2==0) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#F0F4F7"));
        }


        if (newsArray.get(position).getImage() == null || newsArray.get(position).getImage().isEmpty()) {
            Picasso.get().load(R.drawable.no_logo).error(R.drawable.no_logo).into(holder.newsPic);
        } else {
            Picasso.get().load(newsArray.get(position).getImage()).error(R.drawable.no_logo).into(holder.newsPic);
        }
    }

    @Override
    public int getItemCount() {
        return newsArray.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView newsPic;
        TextView newsheader;
        TextView newsdate;
        TextView newssource;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.news_card_view_item);
            newsPic = itemView.findViewById(R.id.newsPic);
            newsheader = itemView.findViewById(R.id.newsheader);
            newsdate = itemView.findViewById(R.id.newsdate);
            newssource = itemView.findViewById(R.id.newssource);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                     News news = newsArray.get(position);
                    String url = news.getUrl();

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(browserIntent);

                }
            });


        }
    }
}
