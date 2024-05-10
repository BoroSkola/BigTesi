package com.example.bigtesi;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bigtesi.fragments.MaiFragment;
import com.example.bigtesi.fragments.NaploFragment;
import com.example.bigtesi.fragments.TestTomegFragment;

public class HomeViewPagerAdapter extends FragmentStateAdapter {
    public NaploFragment naplo;


    public HomeViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MaiFragment();
            case 1:
                return naplo = new NaploFragment();
            case 2:
                return new TestTomegFragment();
            default:
                return new MaiFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
