package com.example.nested.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.nested.fragments.NewsFragment;
import com.example.nested.fragments.SummaryFragment;

public class TabAdapterDetail extends FragmentStateAdapter {
    public TabAdapterDetail(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new SummaryFragment();
            default:
                return new NewsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
