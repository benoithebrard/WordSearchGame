package com.demo.benny.wordsearchgame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class represents the game´s data and state. It parses a JSON file and converts it to data
 * structures. User input can modify the game´s state through functions.
 */
public class Game {
    private String mWordToTranslate;
    private String mSourceLanguage;
    private String mTargetLanguage;
    private int mNbColumns;
    private final List<String> mLetters = new ArrayList<>();
    private final Map<String, String> mWordLocations = new HashMap<>();
    private final Map<String, Boolean> mFoundWords = new HashMap<>();

    /**
     * Converts JSON object to game object
     */
    Game(JSONObject json) {
        try {
            mWordToTranslate = json.getString("word");
            mSourceLanguage = json.getString("source_language");
            mTargetLanguage = json.getString("target_language");
            // Convert character grid into array
            JSONArray characterGrid = json.getJSONArray("character_grid");
            mNbColumns = characterGrid.length();
            for (int i = 0; i < mNbColumns; i++) {
                JSONArray row = characterGrid.getJSONArray(i);
                for (int j = 0; j < row.length(); j++) {
                    mLetters.add(row.getString(j));
                }
            }
            // Extract word locations
            JSONObject locations = json.getJSONObject("word_locations");
            Iterator<String> keys = locations.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = locations.optString(key);
                mWordLocations.put(key, value);
                mFoundWords.put(key, false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getters
     */
    public String getWordToTranslate() {
        return mWordToTranslate;
    }

    public String getSourceLanguage() { return mSourceLanguage; }

    public String getTargetLanguage() { return mTargetLanguage; }

    public int getNbColumns() {
        return mNbColumns;
    }

    public List<String> getLetters() {
        return mLetters;
    }

    /**
     * Compute the number of words that haven´t been found yet
     */
    public int getNbRemainingWords() {
        int nbRemaining = mWordLocations.size();
        for (String key : mFoundWords.keySet()) {
            // If a word has been found, it is not counted as remaining anymore
            if (mFoundWords.get(key)) {
                nbRemaining--;
            }
        }
        return nbRemaining;
    }

    /**
     * Convert found words´s (X,Y) coordinates into grid positions
     * For example for "chica": "2,2,3,2,4,2,5,2,6,2" --> grid positions "7, 8, 9, 10"
     */
    public List<Integer> getSelectedPositions() {
        List<Integer> selectedPositions = new ArrayList<>();
        for (String key : mFoundWords.keySet()) {
            // Convert found targets to positions
            if (mFoundWords.get(key)) {
                for (int i = 0; i < key.length() - 2; i += 4) {
                    int position = locationToPosition(key.charAt(i), key.charAt(i + 2));
                    selectedPositions.add(position);
                }
            }
        }
        return selectedPositions;
    }

    /**
     * Try to match the user´s selected grid positions with any of the words that haven´t
     * been found yet.
     */
    public boolean matchPositions(List<Integer> positions) {
        boolean success = false;
        for (String key : mWordLocations.keySet()) {
            // Check that both coordinates and letters match
            if (matchCoordinates(positions, key) && matchWord(positions, mWordLocations.get(key))) {
                // Mark word as found
                mFoundWords.put(key, true);
                success = true;
                break;
            }
        }
        return success;
    }

    /**
     * Clear game by resetting found words
     */
    public void clear() {
        for (String key : mFoundWords.keySet()) {
            // Clear found words
            mFoundWords.put(key, false);
        }
    }

    /**
     * Utility functions
     */
    private boolean matchCoordinates(List<Integer> positions, String location) {
        int nbPositions = (location.length() + 1) / 4;
        int nbMatches = 0;
        if (nbPositions != positions.size()) {
            return false;
        }
        // Convert and match locations
        for (int i = 0, j = 0; i < location.length() - 2 || j < positions.size(); i += 4, j++, nbMatches++) {
            int position = locationToPosition(location.charAt(i), location.charAt(i + 2));
            // Stop if mismatch
            if (position != positions.get(j)) {
                break;
            }
        }
        return nbMatches == nbPositions;
    }

    private int locationToPosition(char charX, char charY) {
        int x = Character.getNumericValue(charX);
        int y = Character.getNumericValue(charY);
        return x + y * mNbColumns;
    }

    private boolean matchWord(List<Integer> positions, String word) {
        return word.length() == positions.size() && positionsToWord(positions).equalsIgnoreCase(word);
    }

    private String positionsToWord(List<Integer> positions) {
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < positions.size(); i++) {
            String letter = mLetters.get(positions.get(i));
            decoded.append(letter);
        }
        return decoded.toString();
    }


}
