package com.example.nested;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import java.util.ArrayList;


public class FavouriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private static FavouriteAdapter favouriteAdapter;
    private static ArrayList<Ticker> arrayFavTicker = new ArrayList<>();
    View fragmentView;
   static TickerDB tickerDB;
    private static SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.recycler, container, false);
        refreshLayout = fragmentView.findViewById(R.id.swipeLayout);
        tickerDB = new TickerDB(getActivity());
        recyclerView = fragmentView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadFavourite();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        favouriteAdapter = new FavouriteAdapter(arrayFavTicker, getActivity());
        recyclerView.setAdapter(favouriteAdapter);
        return fragmentView;
    }

    public static void loadFavourite() {
        if (arrayFavTicker != null) {
            arrayFavTicker.clear();
        }

        SQLiteDatabase db = tickerDB.getReadableDatabase();
        Cursor cursor = tickerDB.select_all_favorite_list();
        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(TickerDB.ID));
                String pic = cursor.getString(cursor.getColumnIndex(TickerDB.COLUMN_PIC));
                String tickerName = cursor.getString(cursor.getColumnIndex(TickerDB.COLUMN_TICKERNAME));
                String companyName = cursor.getString(cursor.getColumnIndex(TickerDB.COLUMN_COMPANYNAME));
                String price = cursor.getString(cursor.getColumnIndex(TickerDB.COLUMN_PRICE));
                String deltaPrice = cursor.getString(cursor.getColumnIndex(TickerDB.COLUMN_DELTAPRICE));

                Ticker favouriteItem = new Ticker(id, pic, tickerName,companyName,price,deltaPrice,null, null);
                arrayFavTicker.add(favouriteItem);
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }
    }

    public  static void FavouriteRefresh() {
        loadFavourite();
        favouriteAdapter.notifyDataSetChanged();
    }
}