package com.example.nested.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nested.R;
import com.example.nested.adapters.TabAdapter;
import com.example.nested.fragments.StocksFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    String input;
    TextInputLayout searchView;
    private ChipGroup chipGroup;
    ArrayList<String> recentArray;
    private ChipGroup chipGroupRecent;
    TextView popular, recent;
    TextInputEditText inputText;
    LinearLayout linearLayout;
    ViewPager2 viewpager2;
    TabLayout tableLayout;
    Typeface typefaceBold;
    Typeface typefaceRegular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().hide();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        viewpager2 = findViewById(R.id.viewpager);
        viewpager2.setAdapter(new TabAdapter(this));

        tableLayout = findViewById(R.id.tabLayout);

        typefaceBold = ResourcesCompat.getFont(MainActivity.this, R.font.montserratbold);
        typefaceRegular = ResourcesCompat.getFont(MainActivity.this, R.font.montserratregular);


        final TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setTextSize(28);
        tabOne.setTextColor(Color.parseColor("#1A1A1A"));

        final TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab2, null);
        tabTwo.setTextSize(18);
        tabTwo.setTextColor(Color.parseColor("#BABABA"));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tableLayout, viewpager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                switch (position) {
                    case 0:
                        tabOne.setText("Stocks");
                        tab.setCustomView(tabOne);
                        new StocksFragment();
                        break;
                    case 1:
                        tabTwo.setText("Favourite");
                        tab.setCustomView(tabTwo);
                        break;
                }
            }
        });
        tabLayoutMediator.attach();
        tableLayout.getTabAt(1).select();
        tableLayout.getTabAt(0).select();


        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextSize(28);
                ((TextView) tab.getCustomView()).setTypeface(typefaceBold, Typeface.BOLD);
                ((TextView) tab.getCustomView()).setTextColor(Color.parseColor("#1A1A1A"));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextSize(18);
                ((TextView) tab.getCustomView()).setTypeface(typefaceRegular, Typeface.BOLD);
                ((TextView) tab.getCustomView()).setTextColor(Color.parseColor("#BABABA"));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        searchView = findViewById(R.id.searchView);
        inputText = findViewById(R.id.inputTextView);
        popular = findViewById(R.id.polular);
        recent = findViewById(R.id.recent);
        chipGroup = findViewById(R.id.chipgrpup);
        chipGroupRecent = findViewById(R.id.chipGroupRecent);
        linearLayout = findViewById(R.id.linear_carrier);
        searchView.setEndIconVisible(false);


        final ArrayList<String> popularArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.popularArray)));
        recentArray = new ArrayList<>();

        loadArrayRecent();

        inputText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    linearLayout.setVisibility(View.VISIBLE);
                    tableLayout.setVisibility(View.GONE);
                    viewpager2.setVisibility(View.GONE);
                    searchView.setStartIconDrawable(R.drawable.back);
                    inputText.setHint("");
                }


            }
        });

        createChip(popularArray, chipGroup);
        createChip(recentArray, chipGroupRecent);

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() { // поиск по chipgroup
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {


                inputText.setText(((Chip) group.getChildAt(checkedId)).getText().toString());
                inputText.setSelection(inputText.getText().length());
                addToResent(recentArray, inputText.getText().toString());
                refreshChip(popularArray, chipGroup);
                refreshChip(recentArray, chipGroupRecent);
                StocksFragment.Search(inputText.getText().toString());
                tableLayout.getTabAt(0).select();
                linearLayout.setVisibility(View.GONE);
                tableLayout.setVisibility(View.VISIBLE);
                viewpager2.setVisibility(View.VISIBLE);

                inputText.clearFocus();
                onHideKeyoard();

            }
        });

        chipGroupRecent.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                inputText.setText(((Chip) group.getChildAt(checkedId - 10)).getText().toString());
                inputText.setSelection(inputText.getText().length());
                addToResent(recentArray, inputText.getText().toString());
                refreshChip(recentArray, chipGroupRecent);
                StocksFragment.Search(inputText.getText().toString());
                tableLayout.getTabAt(0).select();
                linearLayout.setVisibility(View.GONE);
                tableLayout.setVisibility(View.VISIBLE);
                viewpager2.setVisibility(View.VISIBLE);

                inputText.clearFocus();
                onHideKeyoard();
            }
        });

        inputText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    input = searchView.getEditText().getText().toString();
                    addToResent(recentArray, input);
                    StocksFragment.Search(input);
                    linearLayout.setVisibility(View.GONE);
                    tableLayout.setVisibility(View.VISIBLE);
                    viewpager2.setVisibility(View.VISIBLE);
                    onHideKeyoard();
                    inputText.clearFocus();
                    refreshChip(recentArray, chipGroupRecent);
                    tableLayout.getTabAt(0).select();
                    return true;
                }
                return false;
            }

        });


        searchView.setStartIconOnClickListener(new View.OnClickListener() { //search back press
            @Override
            public void onClick(View v) {

                searchView.setStartIconDrawable(R.drawable.ellipse_434);
                linearLayout.setVisibility(View.GONE);
                tableLayout.setVisibility(View.VISIBLE);
                viewpager2.setVisibility(View.VISIBLE);
                searchView.getEditText().getText().clear();
                inputText.clearFocus();
                onHideKeyoard();
                inputText.setHint(R.string.hint_string);
            }
        });


        searchView.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.getEditText().getText().clear();
            }
        });


        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchView.setEndIconVisible(true);
                } else {
                    searchView.setEndIconVisible(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    public void createChip(ArrayList<String> array, ChipGroup chipGroup) {
        for (int i = 0; i < array.size(); i++) {
            Chip chip = new Chip(this);
            ChipDrawable drawable = ChipDrawable.createFromAttributes(this, null, 0, R.style.Widget_MaterialComponents_Chip_Choice);
            chip.setChipDrawable(drawable);
            chip.setPadding(2, 0, 2, 8);
            chip.setText(array.get(i));
            chip.setTextSize(12);
            chip.setTypeface(typefaceRegular, Typeface.NORMAL);
            chipGroup.addView(chip);
            if (chipGroup.getId() == R.id.chipgrpup) {
                chip.setId(i);
            } else {
                chip.setId(10 + i);

            }
        }
    }

    public void addToResent(ArrayList<String> array, String input) {
        array.add(0, input);
        if (array.size() >= 10) {
            array.remove(9);
        }
        saveArrayRecent();
    }

    public void refreshChip(ArrayList<String> array, ChipGroup chipGroup) {

        chipGroup.removeAllViews();
        createChip(array, chipGroup);
    }


    public void onHideKeyoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean saveArrayRecent() {
        SharedPreferences preferences = this.getSharedPreferences("array", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("Status_size", recentArray.size());
        for (int i = 0; i < recentArray.size(); i++) {
            editor.remove("Status_" + i);
            editor.putString("Status_" + i, recentArray.get(i));
        }
        return editor.commit();
    }

    public void loadArrayRecent() {
        SharedPreferences prefs = this.getSharedPreferences("array", Context.MODE_PRIVATE);
        recentArray.clear();
        int size = prefs.getInt("Status_size", 0);
        for (int i = 0; i < size; i++) {
            recentArray.add(prefs.getString("Status_" + i, null));
        }
    }

    @Override
    public void onBackPressed() {

        if (linearLayout.getVisibility() == View.VISIBLE) {
            searchView.setStartIconDrawable(R.drawable.ellipse_434);
            linearLayout.setVisibility(View.GONE);
            tableLayout.setVisibility(View.VISIBLE);
            viewpager2.setVisibility(View.VISIBLE);
            searchView.getEditText().getText().clear();
            inputText.clearFocus();
            onHideKeyoard();
            inputText.setHint(R.string.hint_string);

        } else if (StocksFragment.backSearchFlag) {
            StocksFragment.loadStockFromDB();
            StocksFragment.stockRefresh();
        } else {
            super.onBackPressed();
        }
    }
}
