package com.example.nested;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class ParseQuery {

        public static final String LOG_TAG = ParseQuery.class.getSimpleName();
        public static final String URL_INITIAL_LIST = "https://fcsapi.com/api-v3/stock/list?exchange=nyse,nasdaq,paris&access_key=Onjp1M5scMuw94IofYSImHNgq";
        public static String decimal_format = "#0.00";
        public static ArrayList<Ticker> arrayTicker = new ArrayList<>();
        public static ArrayList<Ticker> searchArrayTicker;
        public static String jsonResponse;
        public static int count;
        public static int searchCount = 100;


        public static ArrayList<Ticker> extractTickers() throws JSONException, IOException {
                count = 0;

                URL url = createUrl(URL_INITIAL_LIST);
                jsonResponse = null;
                jsonResponse = makeHttpRequest(url);
                arrayTicker = extractInitialFromApi(jsonResponse);

                for (int x = 0; x < arrayTicker.size(); x++) {
                        String urlForImg = "https://finnhub.io/api/v1/stock/profile2?symbol=" + arrayTicker.get(x).getTickerName() + "&token=c114p3f48v6t4vgvtshg"; //img
                        url = createUrl(urlForImg);
                        jsonResponse = null;
                        jsonResponse = makeHttpRequest(url);
                        extractImgFromApi2(jsonResponse, x, arrayTicker);

                        String urlForPrice = "https://finnhub.io/api/v1/quote?symbol=" + arrayTicker.get(x).getTickerName() + "&token=c114p3f48v6t4vgvtshg";//price
                        url = createUrl(urlForPrice);
                        jsonResponse = null;
                        jsonResponse = makeHttpRequest(url);
                        extractPriceFromApi3(jsonResponse, x, arrayTicker);

                        count++;
                }

                return arrayTicker;
        }


        public static ArrayList<Ticker> extractSearchTickers(CharSequence charSequence) throws JSONException, IOException {
                searchCount++;
                searchArrayTicker = new ArrayList<>();

                String requestUrl = "https://finnhub.io/api/v1/search?q=" + charSequence.toString().toLowerCase().trim() + "&token=c114p3f48v6t4vgvtshg";

                URL url = createUrl(requestUrl);
                jsonResponse = null;
                jsonResponse = makeHttpRequest(url);
                searchArrayTicker.addAll(searchExtractFromApi(jsonResponse));


                for (int x = 0; x < searchArrayTicker.size(); x++) {
                        String urlForPic = "https://finnhub.io/api/v1/stock/profile2?symbol=" + searchArrayTicker.get(x).getTickerName() + "&token=c114p3f48v6t4vgvtshg"; //img
                        jsonResponse = null;
                        jsonResponse = makeHttpRequest(createUrl(urlForPic));
                        if (jsonResponse.equals("{}")) {
                        } else {
                                extractImgFromApi2(jsonResponse, x, searchArrayTicker);
                        }
                }
                return searchArrayTicker;
        }


        public static URL createUrl(String stringUrl) {
                URL url = null;
                try {
                        url = new URL(stringUrl);
                } catch (MalformedURLException e) {
                        Log.e(LOG_TAG, "Error with creating URL ", e);
                }
                return url;
        }


        public static String makeHttpRequest(URL url) throws IOException {
                String jsonResponse = "";

                if (url == null) {
                        return jsonResponse;
                }

                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;

                try {
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setReadTimeout(10000);
                        urlConnection.setConnectTimeout(15000);
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();


                        if (urlConnection.getResponseCode() == 200) {
                                inputStream = urlConnection.getInputStream();
                                jsonResponse = inputStreamReader(inputStream);
                        } else {
                                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                        }
                } catch (IOException e) {
                        Log.e(LOG_TAG, "Problem with http reques", e);
                } finally {
                        if (urlConnection != null) {
                                urlConnection.disconnect();
                        }
                        if (inputStream != null) {
                                inputStream.close();
                        }
                }

                return jsonResponse;
        }


        private static String inputStreamReader(InputStream inputStream) throws IOException {
                StringBuilder sb = new StringBuilder();
                if (inputStream != null) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                        BufferedReader bufReader = new BufferedReader(inputStreamReader);
                        String line = bufReader.readLine();
                        while (line != null) {
                                sb.append(line);
                                line = bufReader.readLine();
                        }
                }
                return sb.toString();
        }


        public static ArrayList<Ticker> extractInitialFromApi(String jsonResponse) {

                if (TextUtils.isEmpty(jsonResponse)) {
                        Log.e("extractInitialFromApi", "Empty");
                        return null;
                }

                ArrayList<Ticker> tickersTmp = new ArrayList<>();

                try {
                        JSONObject jobject = new JSONObject(jsonResponse);
                        JSONObject jarray = jobject.getJSONObject("response");

                        for (int i = 0; i < 12; i++) {  //Пейджинг не реализован, поэтому так
                                if (jarray.has(Integer.toString(i))) {
                                        JSONObject properties = jarray.optJSONObject(Integer.toString(i));
                                        String tickerName = properties.optString("short_name");
                                        String companyName = properties.optString("name");
                                        tickersTmp.add(new Ticker(String.valueOf(count), null, tickerName, companyName, "null", "null", "null", "null"));
                                        count++;
                                }
                        }
                } catch (JSONException e) {
                        Log.e("QueryUtils", "Problem parsing  JSON results", e);
                }
                return tickersTmp;
        }


        public static ArrayList<Ticker> extractImgFromApi2(String jsonResponse, int x, ArrayList<Ticker> array) {
                try {
                        JSONObject jobject = new JSONObject(jsonResponse);
                        String pic = jobject.optString("logo");
                        array.get(x).setPic(pic);
                        String currency = jobject.optString("currency");
                        array.get(x).setCurrency(currency);
                } catch (JSONException ex) {
                        ex.printStackTrace();
                }
                return null;
        }


        public static ArrayList<Ticker> extractPriceFromApi3(String jsonResponse, int x, ArrayList<Ticker> array) {

                String currency = chooseCurrency(arrayTicker.get(x).getCurrency());

                try {
                        JSONObject jobject = new JSONObject(jsonResponse);
                        String price = jobject.optString("c");
                        array.get(x).setPrice(currency + " " + price);
                        String previousPrice = jobject.optString("pc");

                        String deltaCont = deltaCount(previousPrice, price, currency);
                        array.get(x).setDeltaPrice(deltaCont);

                } catch (JSONException ex) {
                        ex.printStackTrace();
                }
                return null;
        }


        public static ArrayList<Ticker> searchExtractFromApi(String jsonResponse) {

                if (TextUtils.isEmpty(jsonResponse)) {
                        Log.e("searchExtractFromApi", "Empty");
                        return null;
                }

                ArrayList<Ticker> tickersTmp = new ArrayList<>();
                ArrayList<Ticker> clearTickersTmp = new ArrayList<>();
                try {
                        JSONObject jobject = new JSONObject(jsonResponse);
                        int counts = Integer.valueOf(jobject.optString("count"));
                        JSONArray jarray = (JSONArray) jobject.get("result");

                        for (int i = 0; i < counts; i++) {
                                JSONObject jsobject = jarray.getJSONObject(i);
                                String tickerName = jsobject.optString("symbol");
                                String companyName = jsobject.optString("description");

                                tickersTmp.add(new Ticker(String.valueOf(searchCount), null, tickerName.toUpperCase(), companyName, "null", "null", "0", "null"));

                                String urlForPrice = "https://finnhub.io/api/v1/quote?symbol=" + tickersTmp.get(0).getTickerName() + "&token=c114p3f48v6t4vgvtshg";//price

                                jsonResponse = null;
                                jsonResponse = makeHttpRequest(createUrl(urlForPrice));
                                extractPriceFromApi3(jsonResponse, 0, tickersTmp);
                                //если для найденого тикера нет стоимости и дельты, то считаем тикер мусорным
                                Ticker tiker = tickersTmp.get(0);
                                if (tiker.getPrice().equals("null") && tiker.getDeltaPrice().equals("null")) {
                                        tickersTmp.clear();
                                } else {
                                        clearTickersTmp.add(tiker);
                                        tickersTmp.clear();
                                }
                                searchCount++;
                        }
                } catch (JSONException | IOException e) {
                        Log.e("searchExtractFromApi", "Problem with Search", e);
                }
                return clearTickersTmp;
        }


        private static String chooseCurrency(String currency) {

                switch (currency) {
                        case "USD":
                                return "$";
                        case "RUB":
                                return "₽";
                        case "EUR":
                                return "€";
                        default:
                                return currency;
                }
        }


        private static String deltaCount(String previousPrice, String price, String currency) {
                double percent;
                try {
                        double a = Double.parseDouble(previousPrice);
                        double b = Double.parseDouble(price);

                        double delta = b - a;

                        if (delta >= 0) {
                                percent = (delta / b) * 100.;
                                return "+" + currency + new DecimalFormat(decimal_format).format(delta) + " (" + new DecimalFormat(decimal_format).format(percent) + "%)";

                        } else {
                                percent = (delta * (-1) / b) * 100.;
                                return "-" + currency + new DecimalFormat(decimal_format).format(delta * -1) + " (" + new DecimalFormat(decimal_format).format(percent) + "%)";
                        }
                } catch (Exception e) {
                        return null;
                }
        }
}