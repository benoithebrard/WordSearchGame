package com.demo.benny.wordsearchgame;

import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GameModelUnitTest {
    private final String SAMPLE_JSON = "{\"source_language\": \"en\", \"word\": \"girl\", \"character_grid\": [[\"o\", \"s\", \"\\u00f3\", \"x\", \"h\", \"\\u00f1\", \"h\"], [\"\\u00fc\", \"r\", \"g\", \"o\", \"l\", \"\\u00fa\", \"b\"], [\"a\", \"t\", \"c\", \"h\", \"i\", \"c\", \"a\"], [\"u\", \"\\u00fa\", \"r\", \"w\", \"\\u00e1\", \"t\", \"\\u00e9\"], [\"p\", \"n\", \"v\", \"r\", \"q\", \"m\", \"l\"], [\"f\", \"d\", \"t\", \"e\", \"a\", \"\\u00f3\", \"l\"], [\"u\", \"t\", \"n\", \"i\", \"\\u00f1\", \"a\", \"s\"]], \"word_locations\": {\"2,2,3,2,4,2,5,2,6,2\": \"chica\", \"2,6,3,6,4,6,5,6\": \"ni\\u00f1a\"}, \"target_language\": \"es\"}";
    /* SAMPLE_JSON:
     {
        "source_language": "en",
        "word": "girl",
        "character_grid": [
            ["o", "s", "\u00f3", "x", "h", "\u00f1", "h"],
            ["\u00fc", "r", "g", "o", "l", "\u00fa", "b"],
            ["a", "t", "c", "h", "i", "c", "a"],
            ["u", "\u00fa", "r", "w", "\u00e1", "t", "\u00e9"],
            ["p", "n", "v", "r", "q", "m", "l"],
            ["f", "d", "t", "e", "a", "\u00f3", "l"],
            ["u", "t", "n", "i", "\u00f1", "a", "s"]
        ],
        "word_locations": {
            "2,2,3,2,4,2,5,2,6,2": "chica",
            "2,6,3,6,4,6,5,6": "ni\u00f1a"
        },
        "target_language": "es"
    }*/

    @Test
    public void match_valid() throws Exception {
        JSONObject json = new JSONObject(SAMPLE_JSON);
        GameModel model = new GameModel(json);
        assertEquals(model.getNbRemainingTargets(), 2);

        List<Integer> positions = new ArrayList<>(Arrays.asList(44, 45, 46, 47));
        assertTrue(model.matchPositions(positions));
        assertEquals(model.getNbRemainingTargets(), 1);

        positions = new ArrayList<>(Arrays.asList(16, 17, 18, 19, 20));
        assertTrue(model.matchPositions(positions));
        assertEquals(model.getNbRemainingTargets(), 0);
    }

    @Test
    public void match_invalid() throws Exception {
        JSONObject json = new JSONObject(SAMPLE_JSON);
        GameModel model = new GameModel(json);

        List<Integer> positions = new ArrayList<>(Arrays.asList(34, 35, 36, 37));
        assertFalse(model.matchPositions(positions));
        assertEquals(model.getNbRemainingTargets(), 2);

        positions = new ArrayList<>(Arrays.asList(16, 17, 18, 19, 20, 21));
        assertFalse(model.matchPositions(positions));
        assertEquals(model.getNbRemainingTargets(), 2);
    }
}