package com.demo.benny.wordsearchgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This class implements the RecyclerView adapter to connect the grid view to the data
 * It keeps a list of grid letters and highlighted cells.
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private List<String> mLetters = new ArrayList<>();
    private List<Integer> mHighlightedCells = new ArrayList<>();
    private int mNbColumns = -1;

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView txtLetter;

        ViewHolder(View v) {
            super(v);
            txtLetter = v.findViewById(R.id.letter);
        }
    }

    /**
     * Updates adapter to different letters and number of columns
     */
    public void update(List<String> letters, int nbColumns) {
        mLetters.clear();
        mHighlightedCells.clear();
        mLetters = letters;
        mNbColumns = nbColumns;
        mHighlightedCells = new ArrayList<>(Collections.nCopies(letters.size(), 0));
        notifyDataSetChanged();
    }

    /**
     * Toggles cell selection. A cell can be highlighted more than once.
     */
    public void toggle(int position, boolean selected) {
        int nbHighlight = mHighlightedCells.get(position);
        mHighlightedCells.set(position, selected ? ++nbHighlight : --nbHighlight);
        notifyItemChanged(position);
    }

    @Override
    public GridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // Inflate a new cell view and make it square
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_layout, parent,
                false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) v.getLayoutParams();
        // Compute the cell´s height dynamically, depending on the number of columns
        lp.height = parent.getMeasuredWidth() / mNbColumns;
        v.setLayoutParams(lp);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // Replace the contents of the view with new data
        final String letter = mLetters.get(position);
        holder.txtLetter.setText(letter);
        holder.txtLetter.setTextColor(isHighlighted(position) ? Color.WHITE : Color.BLACK);
        holder.txtLetter.setBackgroundResource(isHighlighted(position) ?
                R.drawable.rounded_corner_highlighted : R.drawable.rounded_corner_white);
    }

    private boolean isHighlighted(int position) {
        return mHighlightedCells.get(position) > 0;
    }

    @Override
    public int getItemCount() {
        return mLetters.size();
    }

}