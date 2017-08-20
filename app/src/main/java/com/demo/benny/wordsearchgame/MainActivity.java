package com.demo.benny.wordsearchgame;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements GridItemTouchListener.GridItemCallbacks {

    private static final String TAG = "MainActivity";
    private static final String URL = "https://s3.amazonaws.com/duolingo-data/s3/js2/find_challenges.txt";
    private static final int FIRST_GAME_INDEX = 0;
    private GridAdapter gridAdapter = null;
    private GridItemTouchListener gridItemTouchListener;
    private GridLayoutManager layoutManager;
    private TextView titleText;
    private TextView remainingText;
    private ImageButton refreshButton;
    private RecyclerView recyclerView;
    private GameStateFragment stateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleText = (TextView) findViewById(R.id.title);
        remainingText = (TextView) findViewById(R.id.remaining);
        refreshButton = (ImageButton) findViewById(R.id.refresh);
        recyclerView = (RecyclerView) findViewById(R.id.grid);

        recyclerView.setHasFixedSize(true);
        gridItemTouchListener = new GridItemTouchListener(this);
        recyclerView.addOnItemTouchListener(gridItemTouchListener);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        // Use a headless retained fragment to store the application state across rotation
        stateFragment =
                (GameStateFragment) getFragmentManager()
                        .findFragmentByTag(GameStateFragment.TAG);

        if (stateFragment == null) {
            stateFragment = new GameStateFragment();
            getFragmentManager().beginTransaction()
                    .add(stateFragment, GameStateFragment.TAG).commit();
        }

        if (stateFragment.jsonGames != null) {
            restoreGame();
        } else {
            fetchNewGames();
        }
    }

    private void restoreGame() {
        playGame(stateFragment.model == null);
        List<Integer> selectedPositions = stateFragment.model.getSelectedPositions();
        for (int i = 0; i < selectedPositions.size(); i++) {
            gridAdapter.toggle(selectedPositions.get(i), true);
        }
    }

    private void fetchNewGames() {
        new DownloadGamesTask().execute(URL);
    }

    private class DownloadGamesTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            titleText.setText(R.string.loading_games);
            refreshButton.setVisibility(View.GONE);
            remainingText.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... urls) {
            OkHttpClient client = new OkHttpClient();
            Request request =
                    new Request.Builder()
                            .url(urls[0])
                            .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    //noinspection ConstantConditions
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                stateFragment.gameIndex = FIRST_GAME_INDEX;
                stateFragment.jsonGames = result.split("\n");
                startNewGame();
                remainingText.setVisibility(View.VISIBLE);
            } else {
                titleText.setText(R.string.loading_failed);
                refreshButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void startNewGame() {
        playGame(true);
    }

    public void onRefreshClicked(@SuppressWarnings("UnusedParameters") View view) {
        fetchNewGames();
    }

    private void playGame(boolean createNewGame) {
        try {
            if (createNewGame) {
                JSONObject jsonGame = new JSONObject(stateFragment.jsonGames[stateFragment.gameIndex]);
                stateFragment.model = new GameModel(jsonGame);
            }

            // Update UI with the model data
            int nbRemaining = stateFragment.model.getNbRemainingTargets();
            int nbColumns = stateFragment.model.getNbColumns();

            updateTitleText();
            updateRemainingText(nbRemaining);

            gridAdapter = new GridAdapter();
            recyclerView.setAdapter(gridAdapter);
            layoutManager.setSpanCount(nbColumns);
            gridItemTouchListener.setSpanCount(nbColumns);
            gridAdapter.update(stateFragment.model.getLetters(), nbColumns);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateTitleText() {
        String translate = getString(R.string.translate);
        String from = getString(R.string.from);
        String to = getString(R.string.to);
        String title = translate + " <b>" + stateFragment.model.getWord() + "</b> " + from + " &lt;" + stateFragment.model.getSourceLanguage() + "&gt; " + to + " &lt;" + stateFragment.model.getTargetLanguage() + "&gt;";
        //noinspection deprecation
        titleText.setText(Html.fromHtml(title));
    }

    private void updateRemainingText(int nbRemaining) {
        String find = getString(R.string.find);
        String word = getString(R.string.word);
        remainingText.setText(find + " " + nbRemaining + " " + word + (nbRemaining > 1 ? "s" : ""));
    }

    @Override
    public void onWordSelected(List<Integer> positions) {
        Log.d(TAG, "selected vector " + positions.toString());

        if (stateFragment.model.matchPositions(positions)) {
            Toast.makeText(this, "nice one :)", Toast.LENGTH_SHORT).show();
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nextState();
                }
            }, 1000);
        } else {
            gridItemTouchListener.unselectSelection();
        }
    }

    private void nextState() {
        int nbRemaining = stateFragment.model.getNbRemainingTargets();
        if (nbRemaining > 0) {
            updateRemainingText(nbRemaining);
        } else {
            if (stateFragment.hasMoreGames()) {
                stateFragment.gameIndex++;
                startNewGame();
            } else {
                fetchNewGames();
            }
        }
    }

    @Override
    public void onLetterSelected(int position, boolean isSelected) {
        gridAdapter.toggle(position, isSelected);
    }

}
