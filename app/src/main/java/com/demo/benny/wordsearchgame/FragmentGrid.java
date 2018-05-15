package com.demo.benny.wordsearchgame;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

/**
 * This class displays the grid UI for the game
 */
public class FragmentGrid extends Fragment implements GridItemTouchListener.ItemTouchListener {

    private GameViewModel mGameViewModel;
    private RecyclerView mRecyclerView;
    private Game mGame;
    private GridRecyclerViewAdapter mAdapter;
    private GridItemTouchListener mItemTouchListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_grid, container, false);
        mRecyclerView = root.findViewById(R.id.grid);
        mRecyclerView.setHasFixedSize(true);

        // Use view model to persist game data across configuration changes
        // as well as to share data between fragments
        mGameViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(GameViewModel.class);

        // Update UI if a new game has been selected
        mGameViewModel.currentGame.observe(getActivity(), new Observer<Game>() {
            @Override
            public void onChanged(@Nullable Game game) {
                mGame = game;
                showGrid();
            }
        });

        return root;
    }

    /**
     * Setup recycler view: loads data and listens to user inputs
     */
    private void showGrid() {
        int nbColumns = mGame.getNbColumns();

        // setup layout manager and adapter
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), nbColumns));
        mAdapter = new GridRecyclerViewAdapter(getContext(), mGame.getLetters(), nbColumns);
        mRecyclerView.setAdapter(mAdapter);

        // setup touch listener
        if (mItemTouchListener != null) {
            mRecyclerView.removeOnItemTouchListener(mItemTouchListener);
        }
        mItemTouchListener = new GridItemTouchListener(nbColumns);
        mRecyclerView.addOnItemTouchListener(mItemTouchListener);
        mItemTouchListener.setTouchListener(this);

        restoreSelections(mGame);
    }

    /**
     * Highlight previously highlighted cells in case of configuration change
     */
    private void restoreSelections(Game game) {
        List<Integer> selectedPositions = game.getSelectedPositions();
        for (int i = 0; i < selectedPositions.size(); i++) {
            mAdapter.toggle(selectedPositions.get(i), true);
        }
    }

    @Override
    public void onWordSelected(List<Integer> positions) {
        if (mGame.matchPositions(positions)) {
            Toast.makeText(getActivity(), "nice one :)", Toast.LENGTH_SHORT).show();
            nextState();
        } else {
            unselectAll(positions);
        }
    }

    /**
     * Check what the next UI´s state should be
     */
    private void nextState() {
        int nbRemainingWords = mGame.getNbRemainingWords();
        if (nbRemainingWords > 0) {
            mGameViewModel.nbRemainingWords.setValue(nbRemainingWords);
        } else {
            mGame.clear();
            mGameViewModel.loadNextGame();
        }
    }

    @Override
    public void onLetterSelected(int position, boolean selected) {
        mAdapter.toggle(position, selected);
    }

    private void unselectAll(List<Integer> positions) {
        for (int i = 0; i < positions.size(); i++) {
            mAdapter.toggle(positions.get(i), false);
        }
    }

}
