package com.calculator.vault.lock.hide.photo.video.ui.cloud;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;



public class DrivefolderAdapter extends FragmentPagerAdapter {
    int behavior;

    public DrivefolderAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.behavior = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Photos_Fragment();
            case 1:
                return new VideosFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return behavior;
    }
}
