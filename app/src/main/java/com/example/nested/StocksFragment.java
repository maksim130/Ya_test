package com.example.nested;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class StocksFragment extends Fragment {

    private RecyclerView recyclerView;
    private static ParseAdapter recyclerAdapter;
    private static ArrayList<Ticker> arrayTicker = new ArrayList<>();
    private static ArrayList<Ticker> tmpArrayTicker = new ArrayList<>();
    View fragmentView;
    private static SwipeRefreshLayout refreshLayout;
    NestedScrollView nestedScrollView;
    static TickerDB2 tickerDB2;
    public static int page = 0;
    public static int searchFlag = 0;
    public static int refreshFlag = 0;

    static LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.recycler, container, false);
        refreshLayout = fragmentView.findViewById(R.id.swipeLayout);
        nestedScrollView = fragmentView.findViewById(R.id.scroll_view);
        recyclerView = fragmentView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerAdapter = new ParseAdapter(arrayTicker, getActivity());
        recyclerView.setAdapter(recyclerAdapter);
        tickerDB2 = new TickerDB2(getActivity());
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        SharedPreferences prefs = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        final Integer pages = prefs.getInt("firstStart", 0);
        if (pages == 0) {
            createTableOnFirstStart();
            try {
                stockRefresh();
            } catch (IOException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            recyclerAdapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(true);
            loadStockFromDB();
            recyclerAdapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);

        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshFlag = 1;
                try {
                    stockRefresh();
                    loadStockFromDB();
                } catch (IOException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) { //достигли kонечной позиции
                    try {
                        stockRefresh();
                    } catch (IOException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return fragmentView;
    }

    public static void Search(String userInput) {
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute("search", userInput);
        searchFlag = 1;
    }


    private void stockRefresh() throws IOException, ExecutionException, InterruptedException {

        CheckInternet checkInternet = new CheckInternet();
        checkInternet.execute().get();
        if (checkInternet.isOnline) {
            if (searchFlag == 1) {
                arrayTicker.clear();
                searchFlag = 0;
                Log.e("!!!!запуска!!!!!", "searchfalag " + searchFlag);
            }
            BackgroundTask backgroundTask = new BackgroundTask();
            backgroundTask.execute("stock", null);
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle("No Internet")
                    .setMessage("Check Internet Connection")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                CheckInternet checkInternet = new CheckInternet();
                                checkInternet.execute().get();
                                stockRefresh();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public static void favouriteRefresh() {
        refreshLayout.setRefreshing(true);
        recyclerAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    private static class BackgroundTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String check = strings[0];
            String userInput = strings[1];

            switch (check) {
                case "stock":
                    try {
                        page++;
                        if (refreshFlag == 1) {
                            page=1;
                        }
                        tmpArrayTicker.addAll(ParseQuery.extractTickers(page));
                    } catch (JSONException ed) {
                        ed.printStackTrace();
                    } catch (IOException ed) {
                        ed.printStackTrace();
                    }

                    return null;

                case "search":
                    try {
                        arrayTicker.clear();
                        arrayTicker.addAll(ParseQuery.extractSearchTickers(userInput));

                    } catch (JSONException ed) {
                        ed.printStackTrace();
                    } catch (IOException ed) {
                        ed.printStackTrace();
                    }
                    return null;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            recyclerAdapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (searchFlag == 1) {
                searchFlag = 0;
            }
            if (refreshFlag == 1) {
                refreshFlag = 0;
                arrayTicker.clear();
                arrayTicker.addAll(tmpArrayTicker);
                tmpArrayTicker.clear();
            }



            saveStockToDB();


            arrayTicker.addAll(tmpArrayTicker);
            tmpArrayTicker.clear();


            recyclerAdapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


    public void loadStockFromDB() {
        if (arrayTicker != null) {
            arrayTicker.clear();
        }

        SQLiteDatabase db = tickerDB2.getReadableDatabase();
        Cursor cursor = tickerDB2.select_all();
        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(TickerDB2.ID));
                String pic = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_PIC));
                String tickerName = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_TICKERNAME));
                String companyName = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_COMPANYNAME));
                String price = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_PRICE));
                String deltaPrice = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_DELTAPRICE));
                String isFavourite = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_FAVOURITE));
                String oldprice = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_OLDPRICE));
                String currency = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_CURRENCY));
                Ticker Item = new Ticker(id, pic, tickerName, companyName, price, deltaPrice, isFavourite, oldprice, currency);
                arrayTicker.add(Item);
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }
    }

    public static void saveStockToDB() {

        for (Ticker item : arrayTicker) {
            tickerDB2.insertIntoTheDatabase(item.getId(), item.getPic(), item.getTickerName(), item.getCompanyName(),
                    item.getPrice(), item.getDeltaPrice(), item.getIsFavourite(), item.getOldprice(), item.getCurrency());
        }
    }


    private void createTableOnFirstStart() {
        tickerDB2.insertEmpty();

        SharedPreferences preferences = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("firstStart", 1);
        editor.apply();
    }


}



