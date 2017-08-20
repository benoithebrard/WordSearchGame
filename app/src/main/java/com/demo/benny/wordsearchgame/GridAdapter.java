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

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private List<String> values = new ArrayList<>();
    private List<Integer> highlights = new ArrayList<>();
    private int nbColumns = -1;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView txtLetter;

        public ViewHolder(View v) {
            super(v);
            txtLetter = v.findViewById(R.id.letter);
        }
    }

    public void update(List<String> letters, int spanCount) {
        values.clear();
        highlights.clear();
        values = letters;
        nbColumns = spanCount;
        highlights = new ArrayList<>(Collections.nCopies(letters.size(), 0));
        notifyDataSetChanged();
    }

    public void toggle(int position, boolean selected) {
        int nbHighlight = highlights.get(position);
        if (selected) nbHighlight++;
        else nbHighlight--;
        highlights.set(position, nbHighlight);
        notifyItemChanged(position);
    }

    @Override
    public GridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // inflate a new cell view and make it square
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_layout, parent, false);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) v.getLayoutParams();
        lp.height = parent.getMeasuredWidth() / nbColumns;
        v.setLayoutParams(lp);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // replace the contents of the view with new data
        final String letter = values.get(position);
        holder.txtLetter.setText(letter);
        holder.txtLetter.setTextColor(isHighlighted(position) ? Color.WHITE : Color.BLACK);
        holder.txtLetter.setBackgroundResource(isHighlighted(position) ? R.drawable.rounded_corner_highlighted : R.drawable.rounded_corner_white);
    }

    private boolean isHighlighted(int position) {
        return highlights.get(position) > 0;
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

}