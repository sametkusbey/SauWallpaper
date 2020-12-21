package com.samet.sauwallpaper.Adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.samet.sauwallpaper.Fragment.CategoryFragment;
import com.samet.sauwallpaper.Fragment.PopularFragment;
import com.samet.sauwallpaper.Fragment.RecentFragment;
import com.samet.sauwallpaper.util.FIREBASECONSTANTS;

/**
 * Created by Taiyab on 03-09-2018.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    private Context context;


    public FragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context= context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position==0)
            return CategoryFragment.getInstance();
        else if (position ==1)
            return PopularFragment.getInstance();
        else if (position == 2)
            return RecentFragment.getInstance(context);
        else
            return null;
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        return null;
    }
}
