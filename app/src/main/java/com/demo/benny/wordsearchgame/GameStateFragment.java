package com.demo.benny.wordsearchgame;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Benny on 18/08/2017.
 * Headless fragment retaining application state across rotation
 */

public class GameStateFragment  extends Fragment {

    public static final String TAG = "headless";
    public int gameIndex = -1;
    public String[] jsonGames = null;
    public GameModel model = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public boolean hasMoreGames() {
        return gameIndex < jsonGames.length - 1;
    }
}