package com.demo.benny.wordsearchgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class fills each cell view with the corresponding cell data on the game grid
 */
public class GridRecyclerViewAdapter extends RecyclerView.Adapter<GridRecyclerViewAdapter.ViewHolder> {

    private final int mNbColumns;
    private final int mScreenWidth;
    private List<String> mData;
    private List<Integer> mHighlighted;
    private LayoutInflater mInflater;

    // Data is passed into the constructor
    GridRecyclerViewAdapter(Context context, List<String> data, int nbColumns) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mNbColumns = nbColumns;
        mHighlighted = new ArrayList<>(Collections.nCopies(data.size(), 0));
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mScreenWidth = displaymetrics.widthPixels;
    }

    // Inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        // Make the cells square by adjusting cell height dynamically
        int parentWidth = parent.getMeasuredWidth();
        view.getLayoutParams().height = (parentWidth > 0 ? parentWidth : mScreenWidth * 3/5) / mNbColumns;
        return new ViewHolder(view);
    }

    // Binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String letter = mData.get(position);
        holder.letterTextView.setText(letter);
        holder.letterTextView.setTextColor(isHighlighted(position) ? Color.WHITE : Color.BLACK);
        holder.letterTextView.setBackgroundResource(isHighlighted(position) ?
                R.drawable.rounded_corner_highlighted : R.drawable.rounded_corner_white);
    }

    // Total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // Stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView letterTextView;

        ViewHolder(View itemView) {
            super(itemView);
            letterTextView = itemView.findViewById(R.id.info_text);
        }
    }

    // Toggles cell selection. A cell can be highlighted more than once.
    public void toggle(int position, boolean selected) {
        int nbHighlight = mHighlighted.get(position);
        mHighlighted.set(position, selected ? ++nbHighlight : --nbHighlight);
        notifyItemChanged(position);
    }

    private boolean isHighlighted(int position) {
        return mHighlighted.get(position) > 0;
    }

}