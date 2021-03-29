package com.example.nested;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Ticker> arrayFavTicker;
    private TickerDB tickerDB;

    public FavouriteAdapter(ArrayList<Ticker> arrayFavTicker, Context context) {
        this.context = context;
        this.arrayFavTicker = arrayFavTicker;
    }

    @NonNull
    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticer_item2,
                parent, false);
        tickerDB = new TickerDB(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.ViewHolder holder, int position) {


        holder.tickerName.setText(arrayFavTicker.get(position).getTickerName());
        holder.companyName.setText(arrayFavTicker.get(position).getCompanyName());
        holder.price.setText(arrayFavTicker.get(position).getPrice());
        holder.deltaPrice.setText(arrayFavTicker.get(position).getDeltaPrice());

        if (arrayFavTicker.get(position).getDeltaPrice().startsWith("+")) {
            holder.deltaPrice.setTextColor(Color.parseColor("#008500"));
        } else {
            holder.deltaPrice.setTextColor(Color.parseColor("#FF0000"));
        }

        if (arrayFavTicker.get(position).getPic() == null || arrayFavTicker.get(position).getPic().isEmpty()) {
            Picasso.get().load(R.drawable.no_logo).error(R.drawable.no_logo).into(holder.companyPic);
        } else {
            Picasso.get().load(arrayFavTicker.get(position).getPic()).error(R.drawable.no_logo).into(holder.companyPic);
        }
    }

    @Override
    public int getItemCount() {
        return arrayFavTicker.size();
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
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final Ticker favTicker = arrayFavTicker.get(position);
                    tickerDB.remove_fav(favTicker.getId());
                    arrayFavTicker.remove(position);
                    notifyDataSetChanged();
                    StocksFragment.FavouriteRefresh();
                }
            });
        }
    }
}
