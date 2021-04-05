package com.example.nested.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nested.activity.DetailActivity;
import com.example.nested.R;
import com.example.nested.entity.Ticker;
import com.example.nested.db.TickerDB2;
import com.squareup.picasso.Picasso;


public class SummaryFragment extends Fragment {
    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7;
    ImageView sumCompanyPic;
    private TickerDB2 tickerDB2;
    View fragmentView;
    Ticker ticker;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_summary, container, false);

        tv1= fragmentView.findViewById(R.id.tv1);
        tv2= fragmentView.findViewById(R.id.tv2);
        tv3= fragmentView.findViewById(R.id.tv3);
        tv4= fragmentView.findViewById(R.id.tv4);
        tv5= fragmentView.findViewById(R.id.tv5);
        tv6= fragmentView.findViewById(R.id.tv6);
        tv7= fragmentView.findViewById(R.id.tv7);
        sumCompanyPic = fragmentView.findViewById(R.id.sumCompanyPic);


        tickerDB2 = new TickerDB2(getContext());

        Cursor cursor = tickerDB2.read_row_data(DetailActivity.TickerfromMA);
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
                ticker = new Ticker(id, pic, tickerName, companyName, price, deltaPrice, isFavourite, oldprice, currency,ipo,phone,industry,site);


            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }

        String industry = "Industry clasification: " + ticker.getIndustry();
        String currency="Currency used in company filings: "+ ticker.getCurrency();
        String ipo="Date of IPO: "+ticker.getIpo();
        String titlephone="Company phone number: ";
        String titlesite ="Company wesite: ";


        tv1.setText(industry);
        tv2.setText(currency);
        tv3.setText(ipo);
        tv4.setText(titlephone);
        tv5.setText(titlesite);
        tv6.setText(ticker.getPhone());
        tv7.setText(ticker.getSite());

        if (ticker.getPic() == null || ticker.getPic().isEmpty()) {
            Picasso.get().load(R.drawable.no_logo).error(R.drawable.no_logo).into(sumCompanyPic);
        } else {
            Picasso.get().load(ticker.getPic()).error(R.drawable.no_logo).into(sumCompanyPic);
        }

        return fragmentView;
    }
}