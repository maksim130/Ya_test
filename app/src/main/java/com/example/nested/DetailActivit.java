package com.example.nested;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;

public class DetailActivit extends AppCompatActivity {

    public static String TickerfromMA;
    private TickerDB2 tickerDB2;
    ViewPager2 viewpager2;
    TabLayout tableLayout;
    Typeface typefaceBold;
    Typeface typefaceRegular;
    TextView tickername, companyname;
    ImageButton back, favourite;

    Ticker ticker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail22);
        getSupportActionBar().hide();

        Bundle arguments = getIntent().getExtras();
        try {
            TickerfromMA = arguments.get("id").toString();

        } catch (NullPointerException e) {
        }


        tickername = findViewById(R.id.header_tickername);
        companyname = findViewById(R.id.header_companyname);
        back = findViewById(R.id.backDetail);
        favourite = findViewById(R.id.favoriteDetail);

        typefaceBold = ResourcesCompat.getFont(DetailActivit.this, R.font.montserratbold);
        typefaceRegular = ResourcesCompat.getFont(DetailActivit.this, R.font.montserratregular);


        final TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_detail1, null);
        tabOne.setTextSize(18);
        tabOne.setTextColor(Color.parseColor("#1A1A1A"));

        final TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_detail2, null);
        tabTwo.setTextSize(14);
        tabTwo.setTextColor(Color.parseColor("#BABABA"));


        viewpager2 = findViewById(R.id.viewpager22);
        viewpager2.setAdapter(new TabAdapterDetail(this));
        tableLayout = findViewById(R.id.tabLayout22);


        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tableLayout, viewpager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                switch (position) {
                    case 0:
                        tabOne.setText("Summary");
                        tab.setCustomView(tabOne);
                        break;
                    case 1:
                        tabTwo.setText("News");
                        tab.setCustomView(tabTwo);
                        break;
                }
            }
        });
        tabLayoutMediator.attach();


        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextSize(18);
                ((TextView) tab.getCustomView()).setTypeface(typefaceBold, Typeface.BOLD);
                ((TextView) tab.getCustomView()).setTextColor(Color.parseColor("#1A1A1A"));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextSize(14);
                ((TextView) tab.getCustomView()).setTypeface(typefaceRegular, Typeface.NORMAL);
                ((TextView) tab.getCustomView()).setTextColor(Color.parseColor("#BABABA"));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        tickerDB2 = new TickerDB2(getApplicationContext());

        Cursor cursor = tickerDB2.read_row_data(TickerfromMA);
        SQLiteDatabase db = tickerDB2.getReadableDatabase();
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
                String ipo = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_IPO));
                String phone = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_PHONE));
                String industry = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_INDUSTRY));
                String site = cursor.getString(cursor.getColumnIndex(TickerDB2.COLUMN_SITE));
                ticker = new Ticker(id, pic, tickerName, companyName, price, deltaPrice, isFavourite, oldprice, currency, ipo, phone, industry, site);


            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }
        tickername.setText(ticker.getTickerName());
        companyname.setText(ticker.getCompanyName());

        if (ticker.getIsFavourite().equals("1")) {
            favourite.setBackgroundResource(R.drawable.yelow_star);
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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



