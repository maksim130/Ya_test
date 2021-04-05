package com.example.nested;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class NewsFragment extends Fragment {

    static ArrayList<News> newsArray = new ArrayList<>();
    static NewsAdapter newsAdapter;
    RecyclerView recyclerView;
    View fragmentView;
    static ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = fragmentView.findViewById(R.id.NewsrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        newsAdapter = new NewsAdapter(newsArray, getActivity());
        recyclerView.setAdapter(newsAdapter);
        progressBar = fragmentView.findViewById(R.id.progree_bar);

        try {
            getNews();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        return fragmentView;
    }


    private static class GetNewsBackground extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


            try {
                newsArray.addAll(ParseQuery.extractNews(DetailActivit.TickerfromMA));

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            newsAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);

        }

    }

    private void getNews() throws ExecutionException, InterruptedException {
        newsArray.clear();
        CheckInternet checkInternet = new CheckInternet();
        checkInternet.execute().get();
        if (checkInternet.isOnline) {

            GetNewsBackground backgroundTask = new GetNewsBackground();
            backgroundTask.execute();
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle("No Internet")
                    .setMessage("Check Internet Connection")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                CheckInternet checkInternet = new CheckInternet();
                                checkInternet.execute().get();
                                getNews();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }


}