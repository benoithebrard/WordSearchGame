package com.demo.benny.wordsearchgame;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Objects;

import com.demo.benny.wordsearchgame.databinding.FragmentTextBinding;

/**
 * This class displays the text UI for the game
 */
public class FragmentText extends Fragment {

    private GameViewModel mGameViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentTextBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_text, container, false);

        // Use view model to persist game data across configuration changes
        // as well as to share data between fragments
        mGameViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(GameViewModel.class);
        binding.setModel(mGameViewModel);

        // Load all games
        mGameViewModel.title.set(new SpannableString(getResources().getString(R.string.loading)));
        mGameViewModel.getGames().observe(this, new Observer<List<Game>>() {
            @Override
            public void onChanged(@Nullable List<Game> games) {
                if (games != null) {
                    mGameViewModel.nbGames.set(games.size());
                    mGameViewModel.loadCurrentGame();
                }
            }
        });

        // Update UI if a new game has been selected
        mGameViewModel.currentGame.observe(getActivity(), new Observer<Game>() {
            @Override
            public void onChanged(@Nullable Game game) {
                if (game != null) {
                    mGameViewModel.nbRemaining.set(game.getNbRemainingWords());
                    updateTitleText(game);
                }
            }
        });

        return binding.getRoot();
    }

    private void updateTitleText(Game game) {
        String translate = getString(R.string.translate);
        String from = getString(R.string.from);
        String to = getString(R.string.to);
        String title = translate + " <b>" + game.getWordToTranslate() + "</b> " + from + " <i>"
                + decodeLanguage(game.getSourceLanguage()) + "</i> " + to + " <i>"
                + decodeLanguage(game.getTargetLanguage()) + "</i>";
        //noinspection deprecation
        mGameViewModel.title.set(Html.fromHtml(title));
    }

    private String decodeLanguage(String languageCode) {
        switch (languageCode) {
            case "en": return getString(R.string.english);
            case "es": return getString(R.string.spanish);
            default: break;
        }
        return languageCode;
    }

}
