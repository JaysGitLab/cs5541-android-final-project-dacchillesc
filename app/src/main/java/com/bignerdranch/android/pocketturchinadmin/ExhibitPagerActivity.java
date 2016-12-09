package com.bignerdranch.android.pocketturchinadmin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

/**
 * .
 */

public class ExhibitPagerActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private ArrayList<Exhibit> mExhibits;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);
        mExhibits = ExhibitLab.get(this).getExhibits();
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount()
            { return mExhibits.size(); }

            @Override
            public Fragment getItem(int pos) {
                Exhibit exhibit = mExhibits.get(pos);
                return ExhibitFragment.newInstance(exhibit.getUUId());
            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) { }

            @Override
            public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) { }

            @Override
            public void onPageSelected(int pos) {
                String s = mExhibits.get(pos).getTitle();
                if(s != null)
                { setTitle(s); }
            }
        });
        UUID exhibitId = (UUID)getIntent().getSerializableExtra(ExhibitFragment.EXTRA_EXHIBIT_ID);
        for(int i = 0; i < mExhibits.size(); i++){
            if(mExhibits.get(i).getUUId().equals(exhibitId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
