package com.sspl.org.tp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    private int tabCount;
    private FragmentManager fragmentManager;

    SectionsPagerAdapter(FragmentManager fragmentManager, int tabCount) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        //Initializing tab count
        this.tabCount = tabCount;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = SessionFragment.newInstance("", "");
                break;
            case 1:
                fragment = BoothsFragment.newInstance("", "");
                break;
            case 2:
                fragment = ProductsFragment.newInstance("", "");
                break;
            case 3:
                fragment = SpeakersFragment.newInstance("", "");
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Sessions";
            case 1:
                return "Booths";
            case 2:
                return "Products";
            case 3:
                return "Speakers";
        }
        return null;
    }
}
