package com.wozart.aura.aura;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wozart.aura.aura.tab.favouriteTab.FavouriteTab;
import com.wozart.aura.aura.tab.homeTab.HomeTab;
import com.wozart.aura.aura.tab.sceneTab.SceneTab;

/**
 * Created by wozart on 29/12/17.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FavouriteTab Favourites = new FavouriteTab();
                return Favourites;
            case 1:
                HomeTab Home = new HomeTab();
                return Home;
            case 2:
                SceneTab Scenes = new SceneTab();
                return Scenes;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
