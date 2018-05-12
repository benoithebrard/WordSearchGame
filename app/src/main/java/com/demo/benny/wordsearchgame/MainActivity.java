package com.demo.benny.wordsearchgame;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * This class is the main entry point for the application
 */
public class MainActivity extends AppCompatActivity implements GridItemTouchListener.SelectedListener {

    private static final String TAG = "MainActivity";
    private GridAdapter gridAdapter = null;
    private GridItemTouchListener gridItemTouchListener;
    private GridLayoutManager layoutManager;
    private TextView gameText;
    private TextView titleText;
    private TextView remainingText;
    private RecyclerView recyclerView;
    private List<Game> mGames;
    private GameViewModel mGameViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameText = findViewById(R.id.game);
        titleText = findViewById(R.id.title);
        remainingText = findViewById(R.id.remaining);
        recyclerView = findViewById(R.id.grid);

        recyclerView.setHasFixedSize(true);
        gridItemTouchListener = new GridItemTouchListener(this);
        recyclerView.addOnItemTouchListener(gridItemTouchListener);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);


        /* Create a ViewModel the first time the system calls an activity's onCreate() method.
         * Re-created activities will receive the same GameViewModel instance created by the
         * first activity.
         */
        mGameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);

        //

        mGameViewModel.getGames().observe(this, new Observer<List<Game>>() {
            @Override
            public void onChanged(@Nullable List<Game> games) {
                mGames = games;
                showGame();
            }
        });
    }

    private Game getCurrentGame() {
        return mGames.get(mGameViewModel.currentGameIndex);
    }

    /**
     * Setup the UI to display a game
     */
    private void showGame() {
        Game game = getCurrentGame();
        int nbRemaining = game.getNbRemainingWords();
        int nbColumns = game.getNbColumns();

        updateGameText();
        updateTitleText(game);
        updateRemainingText(nbRemaining);

        gridAdapter = new GridAdapter();
        recyclerView.setAdapter(gridAdapter);
        layoutManager.setSpanCount(nbColumns);
        gridItemTouchListener.setNbColumns(nbColumns);
        gridAdapter.update(game.getLetters(), nbColumns);
        restoreSelections(game);
    }

    /**
     * Highlights previously highlighted cells in case of configuration change
     */
    private void restoreSelections(Game game) {
        List<Integer> selectedPositions = game.getSelectedPositions();
        for (int i = 0; i < selectedPositions.size(); i++) {
            gridAdapter.toggle(selectedPositions.get(i), true);
        }
    }

    private void updateGameText() {
        String gameStr = getResources().getString(R.string.game,
                mGameViewModel.currentGameIndex + 1, mGames.size());
        gameText.setText(gameStr);
    }

    private void updateTitleText(Game game) {
        String translate = getString(R.string.translate);
        String from = getString(R.string.from);
        String to = getString(R.string.to);
        String title = translate + " <b>" + game.getWordToTranslate() + "</b> " + from + " <i>"
                + decodeLanguage(game.getSourceLanguage()) + "</i> " + to + " <i>"
                + decodeLanguage(game.getTargetLanguage()) + "</i>";
        //noinspection deprecation
        titleText.setText(Html.fromHtml(title));
    }

    private String decodeLanguage(String languageCode) {
        switch (languageCode) {
            case "en": return getString(R.string.english);
            case "es": return getString(R.string.spanish);
            default: break;
        }
        return languageCode;
    }

    private void updateRemainingText(int nbRemaining) {
        String remainingStr = getResources().getString(R.string.remaining, nbRemaining);
        remainingStr += nbRemaining > 1 ? "s" : "";
        remainingText.setText(remainingStr);
    }

    @Override
    public void onLetterSelected(int position, boolean selected) {
        gridAdapter.toggle(position, selected);
    }

    @Override
    public void onWordSelected(List<Integer> positions) {
        Log.d(TAG, "selected vector " + positions.toString());
        Game game = getCurrentGame();

        if (game.matchPositions(positions)) {
            Toast.makeText(this, "nice one :)", Toast.LENGTH_SHORT).show();
            nextState(game);
        } else {
            unselectAll(positions);
        }
    }

    private void unselectAll(List<Integer> positions) {
        for (int i = 0; i < positions.size(); i++) {
            gridAdapter.toggle(positions.get(i), false);
        }
    }

    /**
     * Checks what the next UI´s state should be
     */
    private void nextState(Game game) {
        int nbRemainingWords = game.getNbRemainingWords();
        if (nbRemainingWords > 0) {
            updateRemainingText(nbRemainingWords);
        } else {
            game.clear();
            computeNextIndex();
            showGame();
        }
    }

    private void computeNextIndex() {
        mGameViewModel.currentGameIndex = ++mGameViewModel.currentGameIndex % mGames.size();
    }


}
