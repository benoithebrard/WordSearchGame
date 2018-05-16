package com.demo.benny.wordsearchgame;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * This class fills each cell view with the corresponding cell data on the game grid
 */
public class GridRecyclerViewAdapter extends RecyclerView.Adapter<GridRecyclerViewAdapter.MyViewHolder> {

    private final int mNbColumns;
    private List<GridData> mData;
    private int mHeight = 0;

    // Data is passed into the constructor
    GridRecyclerViewAdapter(Context context, List<String> data, int nbColumns) {
        this.mData = toGridData(data);
        this.mNbColumns = nbColumns;
    }

    private List<GridData> toGridData(List<String> data) {
        List<GridData> list = new ArrayList<>();
        for (String letter : data) {
            list.add(new GridData(letter));
        }
        return list;
    }

    // Provide a reference to the views for each data item
    class MyViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;

        MyViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Object obj) {
            binding.setVariable(BR.obj, obj);
            binding.executePendingBindings();
        }
    }

    // Inflates the cell layout from xml when needed
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.recyclerview_item, parent, false);
        View view = binding.getRoot();

        // Make the cells square by adjusting cell height dynamically
        if (mHeight == 0) {
            mHeight = parent.getMeasuredWidth() / mNbColumns;
        }
        view.getLayoutParams().height = mHeight;
        return new MyViewHolder(binding);
    }

    // Binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final GridData gridData = mData.get(position);
        holder.bind(gridData);
    }

    // Total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // Toggles cell selection. A cell can be highlighted more than once.
    public void toggle(int position, boolean selected) {
        GridData data = mData.get(position);
        int nbHighlight = data.highlight.get();
        data.highlight.set(selected ? ++nbHighlight : --nbHighlight);
    }

}