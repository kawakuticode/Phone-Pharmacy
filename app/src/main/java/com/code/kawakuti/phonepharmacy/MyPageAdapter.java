package com.code.kawakuti.phonepharmacy;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Russelius on 31/01/16.
 */
public class MyPageAdapter extends FragmentPagerAdapter {


    Context mContext;
String [] pages = {"Medicines" , "Memo Recips" , "Find Pharmacy" };
    public MyPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                return new MedicinesFragment();

            case 1:
                return new MemoRecipsFragment();

            case 2:
                return new FindPharmacyFragment();
            default:
                Fragment fragmentx = new MedicinesFragment();
                // Attach some data to the fragment
                // that we'll use to populate our fragment layouts
              Bundle  args = new Bundle();
               // args.putInt("page_position", position + 1);

                // Set the arguments on the fragment
                // that will be fetched in the
                // MedicinesFragment@onCreateView
                fragmentx.setArguments(args);
                return fragmentx;

        }
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pages [position];
    }
}
