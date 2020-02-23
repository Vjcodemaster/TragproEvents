package scanning;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    private int tabCount;

    public SectionsPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                try {
                    return new RecentFragment();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            case 1:
                try {
                return new AllFragment();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            default:
                return null;
        }
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
                return "RECENT";
            case 1:
                return "ALL";
        }
        return null;
    }
}
