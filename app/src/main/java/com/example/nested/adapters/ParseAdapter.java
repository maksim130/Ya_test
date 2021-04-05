package com.example.nested.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nested.activity.DetailActivity;
import com.example.nested.R;
import com.example.nested.db.TickerDB2;
import com.example.nested.entity.Ticker;
import com.example.nested.fragments.FavouriteFragment;
import com.example.nested.fragments.StocksFragment;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.ArrayList;

public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.ViewHolder> { //StockFragment adapter

    private ArrayList<Ticker> arrayTicker;
    private Context context;
    private static TickerDB2 tickerDB2;

    public ParseAdapter(ArrayList<Ticker> arrayTicker, Context context) {
        this.arrayTicker = arrayTicker;
        this.context = context;
    }


    @NonNull
    @Override
    public ParseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        tickerDB2 = new TickerDB2(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticker_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ParseAdapter.ViewHolder holder, int position) {


        final Ticker parseItem = arrayTicker.get(position);

        readFavStatusData(parseItem, holder);

        holder.tickerName.setText(parseItem.getTickerName());
        holder.companyName.setText(parseItem.getCompanyName());
        holder.price.setText(parseItem.getPrice());
        holder.deltaPrice.setText(parseItem.getDeltaPrice());

        if (position % 2 == 0) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#F0F4F7"));
        }


        if (parseItem.getDeltaPrice() != null && parseItem.getDeltaPrice().startsWith("+")) {
            holder.deltaPrice.setTextColor(Color.parseColor("#008500"));
        } else {
            holder.deltaPrice.setTextColor(Color.parseColor("#FF0000"));
        }

        if (parseItem.getPic() == null || parseItem.getPic().isEmpty()) {
            Picasso.get().load(R.drawable.no_logo).error(R.drawable.no_logo).into(holder.companyPic);
        } else {
            Picasso.get().load(parseItem.getPic()).error(R.drawable.no_logo).into(holder.companyPic);
        }


    }

    @Override
    public int getItemCount() {
        return arrayTicker.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView companyPic;
        TextView tickerName;
        TextView companyName;
        ImageButton favourite;
        TextView price;
        TextView deltaPrice;
        CardView cardView;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraint);
            cardView = itemView.findViewById(R.id.card_view_item);
            companyPic = itemView.findViewById(R.id.companyPic);
            tickerName = itemView.findViewById(R.id.tickerName);
            companyName = itemView.findViewById(R.id.companyName);
            price = itemView.findViewById(R.id.price);
            deltaPrice = itemView.findViewById(R.id.deltaPrice);
            favourite = itemView.findViewById(R.id.favorite);


            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Ticker ticker = arrayTicker.get(position);
                    String tickerName = ticker.getTickerName();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("id", tickerName);
                    context.startActivity(intent);
                }
            });

            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Ticker ticker = arrayTicker.get(position);
                    if (ticker.getIsFavourite().equals("1")) {
                        ticker.setIsFavourite("0");
                        tickerDB2.remove_fav(ticker.getTickerName());
                        favourite.setBackgroundResource(R.drawable.empty_star);

                        StocksFragment.favouriteRefresh();
                        FavouriteFragment.favouriteRefresh();
                        new FavouriteFragment().webSocketRefresh();

                    } else {
                        ticker.setIsFavourite("1");
                        try {
                            tickerDB2.become_fav(ticker.getTickerName());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        favourite.setBackgroundResource(R.drawable.yelow_star);

                        StocksFragment.favouriteRefresh();
                        FavouriteFragment.favouriteRefresh();
                        new FavouriteFragment().webSocketRefresh();

                    }
                }
            });

        }

    }

    public static void readFavStatusData(Ticker ticker, ViewHolder viewHolder) {
        Cursor cursor = tickerDB2.read_row_data(ticker.getTickerName());
        SQLiteDatabase db = tickerDB2.getReadableDatabase();
        try {
            while (cursor.moveToNext()) {
                String item_fav_status = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_FAVOURITE));
                ticker.setIsFavourite(item_fav_status);


                if (item_fav_status != null && item_fav_status.equals("1")) {
                    viewHolder.favourite.setBackgroundResource(R.drawable.yelow_star);

                } else if (item_fav_status != null && item_fav_status.equals("0")) {
                    viewHolder.favourite.setBackgroundResource(R.drawable.empty_star);

                }
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }
    }
}
