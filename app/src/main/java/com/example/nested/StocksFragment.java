package com.example.nested;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.datatype.Duration;


public class StocksFragment extends Fragment {

    private RecyclerView recyclerView;
    private static ParseAdapter recyclerAdapter;
    private static ArrayList<Ticker> arrayTicker = new ArrayList<>();
    View fragmentView;
    private static SwipeRefreshLayout refreshLayout;
    NestedScrollView nestedScrollView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.recycler, container, false);
        refreshLayout = fragmentView.findViewById(R.id.swipeLayout);
        nestedScrollView = fragmentView.findViewById(R.id.scrollView);
        recyclerView = fragmentView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerAdapter = new ParseAdapter(arrayTicker, getActivity());
        recyclerView.setAdapter(recyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        try {
            FullRefresh();
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                try {
                    FullRefresh();
                } catch (IOException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        return fragmentView;
    }


    public static void Search(String userInput) {
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute("search", userInput);
    }


    private void FullRefresh() throws IOException, ExecutionException, InterruptedException {
        CheckInternet checkInternet = new CheckInternet();
        checkInternet.execute().get();
        if (checkInternet.isOnline) {
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
                                FullRefresh();
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


    public static void FavouriteRefresh() {
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
                        arrayTicker.addAll(ParseQuery.extractTickers());
                    } catch (JSONException ed) {
                        ed.printStackTrace();
                    } catch (IOException ed) {
                        ed.printStackTrace();
                    }
                    return null;

                case "search":
                    try {
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
            arrayTicker.clear();
            recyclerAdapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerAdapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}



