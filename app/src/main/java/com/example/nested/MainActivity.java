package com.example.nested;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String input;
    EditText userInput;
    private ChipGroup chipGroup;
    ArrayList<String> recentArray;
    private ChipGroup chipGroupRecent;
    TextView popular, recent, blank;
    ImageButton search, back, close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().hide();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ViewPager2 viewpager2 = findViewById(R.id.viewpager);
        viewpager2.setAdapter(new TabAdapter(this));

        TabLayout tableLayout = findViewById(R.id.tabLayout);

        final Typeface typeface = ResourcesCompat.getFont(MainActivity.this, R.font.montserratbold);
        final Typeface typeface2 = ResourcesCompat.getFont(MainActivity.this, R.font.montserratregular);


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

        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextSize(28);
                ((TextView) tab.getCustomView()).setTypeface(typeface,Typeface.BOLD);
                ((TextView) tab.getCustomView()).setTextColor(Color.parseColor("#1A1A1A"));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextSize(18);
                ((TextView) tab.getCustomView()).setTypeface(typeface2,Typeface.NORMAL);
                ((TextView) tab.getCustomView()).setTextColor(Color.parseColor("#BABABA"));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        userInput = findViewById(R.id.searchView);
        popular = findViewById(R.id.polular);
        recent = findViewById(R.id.recent);
        chipGroup = findViewById(R.id.chipgrpup);
        chipGroupRecent = findViewById(R.id.chipGroupRecent);
        search = findViewById(R.id.search);
        back = findViewById(R.id.back);
        close = findViewById(R.id.close);
        blank = findViewById(R.id.blank);


        final ArrayList<String> popularArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.popularArray)));
        recentArray = new ArrayList<>();

        loadArrayRecent();

        userInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    chipGroup.setVisibility(View.VISIBLE);
                    popular.setVisibility(View.VISIBLE);
                    chipGroupRecent.setVisibility(View.VISIBLE);
                    recent.setVisibility(View.VISIBLE);
                    search.setVisibility(View.GONE);
                    back.setVisibility(View.VISIBLE);
                    blank.setVisibility(View.VISIBLE);
                }
            }
        });

        createChip(popularArray, chipGroup);
        createChip(recentArray, chipGroupRecent);

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() { // поиск по chipgroup
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {


                userInput.setText(((Chip) group.getChildAt(checkedId)).getText().toString());
                userInput.setSelection(userInput.getText().length());
                addToResent(recentArray, userInput.getText().toString());
                refreshChip(popularArray, chipGroup);
                StocksFragment.Search(userInput.getText().toString());
                chipGroupRecent.setVisibility(View.GONE);
                chipGroup.setVisibility(View.GONE);
                popular.setVisibility(View.GONE);
                recent.setVisibility(View.GONE);
                blank.setVisibility(View.GONE);
                onHideKeyoard();

            }
        });

        chipGroupRecent.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                userInput.setText(((Chip) group.getChildAt(checkedId - 10)).getText().toString());
                userInput.setSelection(userInput.getText().length());
                addToResent(recentArray, userInput.getText().toString());
                refreshChip(recentArray, chipGroupRecent);
                StocksFragment.Search(userInput.getText().toString());
                chipGroupRecent.setVisibility(View.GONE);
                chipGroup.setVisibility(View.GONE);
                popular.setVisibility(View.GONE);
                recent.setVisibility(View.GONE);
                blank.setVisibility(View.GONE);
                onHideKeyoard();
            }
        });

        userInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    input = userInput.getText().toString();
                    addToResent(recentArray, input);
                    StocksFragment.Search(userInput.getText().toString());
                    chipGroupRecent.setVisibility(View.GONE);
                    chipGroup.setVisibility(View.GONE);
                    popular.setVisibility(View.GONE);
                    recent.setVisibility(View.GONE);
                    blank.setVisibility(View.GONE);
                    onHideKeyoard();
                    return true;
                }
                return false;
            }

        });

        userInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.setVisibility(View.VISIBLE);
                popular.setVisibility(View.VISIBLE);
                chipGroupRecent.setVisibility(View.VISIBLE);
                recent.setVisibility(View.VISIBLE);
                search.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
                blank.setVisibility(View.VISIBLE);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.setVisibility(View.GONE);
                popular.setVisibility(View.GONE);
                recent.setVisibility(View.GONE);
                chipGroupRecent.setVisibility(View.GONE);
                userInput.getText().clear();
                search.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
                userInput.clearFocus();
                blank.setVisibility(View.GONE);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInput.getText().clear();
            }
        });

        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    close.setVisibility(View.VISIBLE);
                } else {
                    close.setVisibility(View.GONE);
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
            chip.setPadding(10, 6, 0, 6);
            chip.setText(array.get(i));
            chip.setTextSize(12);
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

    @Override
    public void onBackPressed() {
        if (chipGroup.getVisibility() == View.GONE) {
            super.onBackPressed();
            return;
        }
        chipGroup.setVisibility(View.GONE);
        popular.setVisibility(View.GONE);
        chipGroupRecent.setVisibility(View.GONE);
        recent.setVisibility(View.GONE);
        blank.setVisibility(View.GONE);
        userInput.getText().clear();
        search.setVisibility(View.VISIBLE);
        back.setVisibility(View.GONE);
        close.setVisibility(View.GONE);
        userInput.clearFocus();
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

}