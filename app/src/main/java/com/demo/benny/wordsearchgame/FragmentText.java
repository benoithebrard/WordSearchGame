package com.demo.benny.wordsearchgame;

import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

/**
 * This class displays the text UI for the game
 */
public class FragmentText extends Fragment {
    
    private TextView mTitle;
    private TextView mGameText;
    private TextView mRemaining;
    private GameViewModel mGameViewModel;
    private int mNbGames;
    private View mCommand;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_text, container, false);
        mTitle = root.findViewById(R.id.title);
        mGameText = root.findViewById(R.id.game);
        mRemaining = root.findViewById(R.id.remaining);
        mCommand = root.findViewById(R.id.command);
        Button previous = root.findViewById(R.id.previous);
        previous.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameViewModel.loadPreviousGame();
            }
        });
        Button next = root.findViewById(R.id.next);
        next.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameViewModel.loadNextGame();
            }
        });

        // Use view model to persist game data across configuration changes
        // as well as to share data between fragments
        mGameViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(GameViewModel.class);

        // Load all games
        mGameViewModel.getGames().observe(this, new Observer<List<Game>>() {
            @Override
            public void onChanged(@Nullable List<Game> games) {
                if (games != null) {
                    mNbGames = games.size();
                    mGameViewModel.loadCurrentGame();
                }
            }
        });

        // Update UI if a new game has been selected
        mGameViewModel.currentGame.observe(getActivity(), new Observer<Game>() {
            @Override
            public void onChanged(@Nullable Game game) {
                updateGameText();
                if (game != null) {
                    updateTitleText(game);
                    updateRemainingText(game.getNbRemainingWords());
                }
            }
        });

        // Update UI if a word has been found
        mGameViewModel.nbRemainingWords.observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer nbRemainingWords) {
                //noinspection ConstantConditions
                updateRemainingText(nbRemainingWords);
            }
        });

        return root;
    }

    private void updateGameText() {
        mCommand.setVisibility(View.VISIBLE);
        String gameStr = getResources().getString(R.string.game,
                mGameViewModel.getCurrentGameIndex() + 1, mNbGames);
        mGameText.setText(gameStr);
    }

    private void updateTitleText(Game game) {
        String translate = getString(R.string.translate);
        String from = getString(R.string.from);
        String to = getString(R.string.to);
        String title = translate + " <b>" + game.getWordToTranslate() + "</b> " + from + " <i>"
                + decodeLanguage(game.getSourceLanguage()) + "</i> " + to + " <i>"
                + decodeLanguage(game.getTargetLanguage()) + "</i>";
        //noinspection deprecation
        mTitle.setText(Html.fromHtml(title));
    }

    private String decodeLanguage(String languageCode) {
        switch (languageCode) {
            case "en": return getString(R.string.english);
            case "es": return getString(R.string.spanish);
            default: break;
        }
        return languageCode;
    }

    private void updateRemainingText(int nbRemainingWords) {
        String remainingStr = getResources().getString(R.string.remaining, nbRemainingWords);
        remainingStr += nbRemainingWords > 1 ? "s" : "";
        mRemaining.setText(remainingStr);
    }

}
