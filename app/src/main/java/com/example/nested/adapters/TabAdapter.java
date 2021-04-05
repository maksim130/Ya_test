package com.example.nested.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.nested.fragments.FavouriteFragment;
import com.example.nested.fragments.StocksFragment;

public class TabAdapter extends FragmentStateAdapter {

    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new StocksFragment();
            default:
                return new FavouriteFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
