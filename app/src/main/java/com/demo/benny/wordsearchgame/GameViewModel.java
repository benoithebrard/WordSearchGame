package com.demo.benny.wordsearchgame;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.Spanned;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class stores the game´s data across configuration changes
 */
public class GameViewModel extends ViewModel {

    private static final String URL = "https://s3.amazonaws.com/duolingo-data/s3/js2/find_challenges.txt";
    private MutableLiveData<List<Game>> mGames;
    public MutableLiveData<Game> currentGame = new MutableLiveData<>();
    public final ObservableInt currentGameIndex = new ObservableInt(0);
    public final ObservableInt nbGames = new ObservableInt(0);
    public final ObservableInt nbRemaining = new ObservableInt(0);
    public final ObservableField<Spanned> title = new ObservableField<>();

    /**
     * Returns the list of all available games. If the list is not already cached, it is retrieved
     * from the network. This function should always be called first.
     */
    public LiveData<List<Game>> getGames() {
        if (mGames == null) {
            mGames = new MutableLiveData<>();
            loadGames();
        }
        return mGames;
    }

    /**
     * Load all games from a server. The JSON response is converted to a list of games.
     */
    private void loadGames() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request =
                        new Request.Builder()
                                .url(URL)
                                .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        //noinspection ConstantConditions
                        String result = response.body().string();
                        if (result != null) {
                            parseResult(result);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private void parseResult(String result) {
                List<Game> newGames = new ArrayList<>();
                String[] jsonGames = result.split("\n");
                for (String jsonString : jsonGames) {
                    try {
                        JSONObject jsonGame = new JSONObject(jsonString);
                        newGames.add(new Game(jsonGame));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mGames.postValue(newGames);
            }
        }).start();
    }

    /**
     * Navigate between games
     */
    public void loadCurrentGame() {
        List<Game> games = mGames.getValue();
        if (games != null) currentGame.setValue(games.get(currentGameIndex.get()));
    }

    public void loadNextGame() {
        List<Game> games = mGames.getValue();
        if (games != null) {
            int index = currentGameIndex.get();
            currentGameIndex.set(++index % games.size());
            currentGame.setValue(games.get(currentGameIndex.get()));
        }
    }

    public void loadPreviousGame() {
        List<Game> games = mGames.getValue();
        if (games != null) {
            int index = currentGameIndex.get();
            currentGameIndex.set(index == 0 ? games.size() - 1 : --index);
            currentGame.setValue(games.get(currentGameIndex.get()));
        }
    }

}