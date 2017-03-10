package com.code.kawakuti.phonepharmacy.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.code.kawakuti.phonepharmacy.fragments.MedicinesFragment;
import com.code.kawakuti.phonepharmacy.home.AlarmActivity;
import com.code.kawakuti.phonepharmacy.location.FindHospitalFragment;
import com.code.kawakuti.phonepharmacy.location.FindPharmacyFragment;

/**
 * Created by Russelius on 31/01/16.
 */
public class MyPageAdapter extends FragmentPagerAdapter {


    Context mContext;
    String[] pages = {"Medicines", "Memo Recips", "Find Pharmacy", "Find Hospital"};

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
                return  new AlarmActivity();
            case 2:
                return new FindPharmacyFragment();
            case 3:
                return new FindHospitalFragment();
            default:
                return new MedicinesFragment();

        }
    }


    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pages[position];
    }
}
