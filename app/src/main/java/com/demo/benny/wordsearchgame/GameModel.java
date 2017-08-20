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
 * Created by Benny on 18/08/2017.
 * Parse, store and match game model data
 * JSON data follows this format:
 * {
 * "source_language": "en",
 * "word": "girl",
 * "character_grid": [
 * ["o", "s", "\u00f3", "x", "h", "\u00f1", "h"],
 * ["\u00fc", "r", "g", "o", "l", "\u00fa", "b"],
 * ["a", "t", "c", "h", "i", "c", "a"],
 * ["u", "\u00fa", "r", "w", "\u00e1", "t", "\u00e9"],
 * ["p", "n", "v", "r", "q", "m", "l"],
 * ["f", "d", "t", "e", "a", "\u00f3", "l"],
 * ["u", "t", "n", "i", "\u00f1", "a", "s"]
 * ],
 * "word_locations": {
 * "2,2,3,2,4,2,5,2,6,2": "chica",
 * "2,6,3,6,4,6,5,6": "ni\u00f1a"
 * },
 * "target_language": "es"
 * }
 */

class GameModel {

    private String word = null;
    private String sourceLanguage = null;
    private String targetLanguage = null;
    private int nbColumns = -1;
    private final List<String> letters = new ArrayList<>();
    private final Map<String, String> targetLocations = new HashMap<>();
    private final Map<String, Boolean> targetsFound = new HashMap<>();

    public GameModel(JSONObject json) {
        try {
            word = json.getString("word");
            sourceLanguage = json.getString("source_language");
            targetLanguage = json.getString("target_language");
            // Convert character grid into array
            JSONArray characterGrid = json.getJSONArray("character_grid");
            nbColumns = characterGrid.length();
            for (int i = 0; i < nbColumns; i++) {
                JSONArray row = characterGrid.getJSONArray(i);
                for (int j = 0; j < row.length(); j++) {
                    letters.add(row.getString(j));
                }
            }
            // Extract word locations
            JSONObject locations = json.getJSONObject("word_locations");
            Iterator<String> keys = locations.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = locations.optString(key);
                targetLocations.put(key, value);
                targetsFound.put(key, false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getWord() {
        return word;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public int getNbColumns() {
        return nbColumns;
    }

    public List<String> getLetters() {
        return letters;
    }

    public int getNbRemainingTargets() {
        int nbRemaining = targetLocations.size();
        for (String key : targetsFound.keySet()) {
            if (targetsFound.get(key)) {
                nbRemaining--;
            }
        }
        return nbRemaining;
    }

    public List<Integer> getSelectedPositions() {
        List<Integer> selectedPositions = new ArrayList<>();
        for (String key : targetsFound.keySet()) {
            // Convert found targets to positions
            if (targetsFound.get(key)) {
                for (int i = 0; i < key.length() - 2; i += 4) {
                    int position = locationToPosition(key.charAt(i), key.charAt(i + 2));
                    selectedPositions.add(position);
                }
            }
        }
        return selectedPositions;
    }

    public boolean matchPositions(List<Integer> positions) {
        boolean success = false;
        for (String key : targetLocations.keySet()) {
            // Check that both coordinates and letters match
            if (matchCoordinates(positions, key) && matchWord(positions, targetLocations.get(key))) {
                // Mark target as found
                targetsFound.put(key, true);
                success = true;
                break;
            }
        }
        return success;
    }

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
        return x + y * nbColumns;
    }

    private boolean matchWord(List<Integer> positions, String targetWord) {
        return targetWord.length() == positions.size() && positionsToWord(positions).equalsIgnoreCase(targetWord);
    }

    private String positionsToWord(List<Integer> positions) {
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < positions.size(); i++) {
            String letter = letters.get(positions.get(i));
            decoded.append(letter);
        }
        return decoded.toString();
    }

}
