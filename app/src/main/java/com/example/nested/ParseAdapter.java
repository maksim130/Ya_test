package com.example.nested;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ParseAdapter extends RecyclerView.Adapter<ParseAdapter.ViewHolder> { //StockFragment adapter

    private ArrayList<Ticker> arrayTicker;
    private Context context;
    private static TickerDB tickerDB;

    public ParseAdapter(ArrayList<Ticker> arrayTicker, Context context) {
        this.arrayTicker = arrayTicker;
        this.context = context;
    }


    @NonNull
    @Override
    public ParseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        tickerDB = new TickerDB(context);
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);
        if (firstStart) {
            createTableOnFirstStart();
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticker_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParseAdapter.ViewHolder holder, int position) {


        final Ticker parseItem = arrayTicker.get(position);

        readFavStatusData(parseItem, holder);

        holder.tickerName.setText(parseItem.getTickerName());
        holder.companyName.setText(parseItem.getCompanyName());
        holder.price.setText(parseItem.getPrice());
        holder.deltaPrice.setText(parseItem.getDeltaPrice());

        if (parseItem.getDeltaPrice()!=null && parseItem.getDeltaPrice().startsWith("+")) {
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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyPic = itemView.findViewById(R.id.companyPic);
            tickerName = itemView.findViewById(R.id.tickerName);
            companyName = itemView.findViewById(R.id.companyName);
            price = itemView.findViewById(R.id.price);
            deltaPrice = itemView.findViewById(R.id.deltaPrice);
            favourite = itemView.findViewById(R.id.favorite);

            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Ticker ticker = arrayTicker.get(position);
                    if (ticker.getIsFavourite().equals("1")) {
                        ticker.setIsFavourite("0");
                        tickerDB.remove_fav(ticker.getId());
                        favourite.setBackgroundResource(R.drawable.empty_star);

                        StocksFragment.FavouriteRefresh();
                        FavouriteFragment.FavouriteRefresh();

                    } else {
                        ticker.setIsFavourite("1");
                        tickerDB.insertIntoTheDatabase(ticker.getId(), ticker.getPic(), ticker.getTickerName(), ticker.getCompanyName(),
                                ticker.getPrice(), ticker.getDeltaPrice(), ticker.getIsFavourite(), ticker.getCurrency());
                        favourite.setBackgroundResource(R.drawable.yelow_star);

                        StocksFragment.FavouriteRefresh();
                        FavouriteFragment.FavouriteRefresh();

                    }
                }
            });

        }
    }


    private void createTableOnFirstStart() {
        tickerDB.insertEmpty();

        SharedPreferences preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    public static void readFavStatusData(Ticker ticker, ViewHolder viewHolder) {
        Cursor cursor = tickerDB.read_all_data(ticker.getId());
        SQLiteDatabase db = tickerDB.getReadableDatabase();
        try {
            while (cursor.moveToNext()) {
                String item_fav_status = cursor.getString(cursor.getColumnIndex(tickerDB.COLUMN_FAVOURITE));
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
