package com.demo.benny.wordsearchgame;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;

public class GridData {
    public final ObservableInt highlight = new ObservableInt(0);
    public final ObservableField<String> letter = new ObservableField<>();

    GridData(String aLetter) {
        letter.set(aLetter);
    }
}
