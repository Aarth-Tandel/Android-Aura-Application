package com.example.wozart.aura.tab.sceneTab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wozart.aura.R;

/**
 * Created by wozart on 29/12/17.
 */

public class SceneTab extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scene_fragment, container, false);
    }
}
