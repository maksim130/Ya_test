package com.example.nested;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocketListener;

// т.к. торгов на выходных нет, обновления не приходят через webSocket. Полностью проверить функционал websocket не удалось. возможна некорректная работа

public class FavouriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private static FavouriteAdapter favouriteAdapter;
    private static ArrayList<Ticker> arrayFavTicker = new ArrayList<>();
    View fragmentView;
    static TickerDB2 tickerDB2;
    private static SwipeRefreshLayout refreshLayout;

    public Context context;
    okhttp3.WebSocket ws;



    private static final int NORMAL_CLOSURE_STATUS = 1000;
    String price;
    String tickerName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.recycler, container, false);

        refreshLayout = fragmentView.findViewById(R.id.swipeLayout);
        tickerDB2 = new TickerDB2(getActivity());
        recyclerView = fragmentView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadFavourite();

        favouriteAdapter = new FavouriteAdapter(arrayFavTicker, getActivity());
        recyclerView.setAdapter(favouriteAdapter);

        start();

        final Handler handler = new Handler();
        final int delay = 2000;

        handler.postDelayed(new Runnable() {
            public void run() {
                favouriteRefresh();
                handler.postDelayed(this, delay);
            }
        }, delay);
        return fragmentView;
    }

    public static void loadFavourite() {
        if (arrayFavTicker != null) {
            arrayFavTicker.clear();
        }

        SQLiteDatabase db = tickerDB2.getReadableDatabase();
        Cursor cursor = tickerDB2.select_all_favorite_list();
        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(TickerDB2.ID));
                String pic = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_PIC));
                String tickerName = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_TICKERNAME));
                String companyName = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_COMPANYNAME));
                String tmpprice = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_PRICE));
                String tmpprice2 = tmpprice.replaceAll("[^0-9.]", "");
                String oldPrice = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_OLDPRICE));
                String currency = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_CURRENCY));
                String price = ParseQuery.chooseCurrency(currency) + " " + tmpprice2;
                String deltaPrice = ParseQuery.deltaCount(oldPrice, tmpprice2, currency);

                Ticker favouriteItem = new Ticker(id, pic, tickerName, companyName, price, deltaPrice, null, oldPrice, currency,"no_data","no_data","no_data","no_data");
                arrayFavTicker.add(favouriteItem);

            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }
    }

    public static void favouriteRefresh() {
        loadFavourite();
        favouriteAdapter.notifyDataSetChanged();
    }

    public void webSocketRefresh() {
        start();
    }

    private final class EchoWebSocketListener extends WebSocketListener {


        @Override
        public void onOpen(okhttp3.WebSocket webSocket, Response response) {

            Log.e("sdsadasdsa"," a "+ response);
            for (Ticker ticker : arrayFavTicker) {
                webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"" + ticker.getTickerName() + "\"}");

            }

        }

        @Override
        public void onMessage(okhttp3.WebSocket webSocket, String text) {
            Log.e("sdsadasdsa"," a "+ text);
            try {
                extractPriceData(text);
            } catch (JSONException | SQLException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onClosing(okhttp3.WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);

        }

        @Override
        public void onFailure(okhttp3.WebSocket webSocket, Throwable t, Response response) {
        }
    }


    public void start() {

        OkHttpClient client = new OkHttpClient.Builder().readTimeout(2, TimeUnit.SECONDS)
                .connectTimeout(2, TimeUnit.SECONDS).build();

        Request request = new Request.Builder().url("wss://ws.finnhub.io?token=c114p3f48v6t4vgvtshg").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    private void stop() {
        ws.close(NORMAL_CLOSURE_STATUS, null);
    }

    private void extractPriceData(String responce) throws JSONException, SQLException {

        JSONObject jobject = new JSONObject(responce);
        JSONArray jarray = jobject.getJSONArray("data");

        price = jarray.getJSONObject(0).optString("p");
        tickerName = jarray.getJSONObject(0).optString("s");

        if (!price.equals(null)) {
            tickerDB2.update_price(tickerName, price);
        }

    }


    @Override
    public void onStop() {


        super.onStop();
        stop();

    }

    @Override
    public void onStart() {
        super.onStart();
        start();
    }

}
