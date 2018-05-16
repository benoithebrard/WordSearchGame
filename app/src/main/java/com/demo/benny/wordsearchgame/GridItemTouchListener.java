package com.demo.benny.wordsearchgame;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * This class listens for touch events and notify a listener.
 * The touch movement must be forward and in a horizontal, vertical or diagonal direction.
 */
class GridItemTouchListener implements RecyclerView.OnItemTouchListener {

    private int mNbColumns;
    private final List<Integer> mPositions = new ArrayList<>();
    private ItemTouchListener mTouchListener;

    GridItemTouchListener(int nbColumns) {
        mNbColumns = nbColumns;
    }

    private enum Direction {
        RIGHT,
        DOWN,
        DIAGONAL,
        UNKNOWN
    }

    private int lastPos;
    private Direction direction;
    private float startX;
    private float startY;

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        // find the view that got the touch event
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        int position = rv.getChildLayoutPosition(childView);
        if (position == -1) return false;

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                clear();
                startX = e.getX();
                startY = e.getY();
                selectFirst(position);
                break;
            case MotionEvent.ACTION_MOVE:
                // Filter out backward select
                if (position <= lastPos) {
                    break;
                }
                // Determine select direction
                // Improve diagonal detection by filtering out other directions below margin
                if (direction == Direction.UNKNOWN) {
                    int margin = rv.getMeasuredWidth() / mNbColumns * 2 / 3;
                    int deltaX = (int) (e.getX() - startX);
                    int deltaY = (int) (e.getY() - startY);
                    if (isDiagonal(position)) {
                        direction = Direction.DIAGONAL;
                    } else if (isRight(position) && deltaX > margin) {
                        direction = Direction.RIGHT;
                    } else if (isDown(position) && deltaY > margin) {
                        direction = Direction.DOWN;
                    }
                }
                // Highlight selected letters
                boolean success = true;
                if (direction == Direction.RIGHT && isRight(position)) {
                    selectRight(position);
                } else if (direction == Direction.DOWN && isDown(position)) {
                    selectDown(position);
                } else if (direction == Direction.DIAGONAL && isDiagonal(position)) {
                    selectDiagonal(position);
                } else {
                    success = false;
                }
                if (success) {
                    lastPos = position;
                }
                break;
            case MotionEvent.ACTION_UP:
                // Send back result
                if (mTouchListener != null) mTouchListener.onWordSelected(mPositions);
                break;
        }
        return false;
    }

    /**
     * Utility functions
     */
    private boolean isRight(int position) {
        int row = lastPos / mNbColumns;
        return position / mNbColumns == row;
    }

    private boolean isDown(int position) {
        int column = lastPos % mNbColumns;
        return position % mNbColumns == column;
    }

    private boolean isDiagonal(int position) {
        int row = lastPos / mNbColumns;
        int column = lastPos % mNbColumns;
        int deltaRow = position / mNbColumns - row;
        int deltaColumn = position % mNbColumns - column;
        return deltaRow == deltaColumn;
    }

    private void selectFirst(int position) {
        mPositions.add(position);
        if (mTouchListener != null ) mTouchListener.onLetterSelected(position, true);
        lastPos = position;
    }

    private void selectRight(int position) {
        for (int i = lastPos + 1; i <= position; i++) {
            mPositions.add(i);
            if (mTouchListener != null ) mTouchListener.onLetterSelected(i, true);
        }
    }

    private void selectDown(int position) {
        for (int i = lastPos + mNbColumns; i <= position; i += mNbColumns) {
            mPositions.add(i);
            if (mTouchListener != null ) mTouchListener.onLetterSelected(i, true);
        }
    }

    private void selectDiagonal(int position) {
        for (int i = lastPos + mNbColumns + 1; i <= position; i += mNbColumns + 1) {
            mPositions.add(i);
            if (mTouchListener != null ) mTouchListener.onLetterSelected(i, true);
        }
    }

    private void clear() {
        mPositions.clear();
        lastPos = -1;
        direction = Direction.UNKNOWN;
        startX = -1;
        startY = -1;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    // Allows touch events to be caught
    void setTouchListener(GridItemTouchListener.ItemTouchListener itemTouchListener) {
        mTouchListener = itemTouchListener;
    }

    // Parent activity will implement this method to respond to touch events
    public interface ItemTouchListener {
        void onWordSelected(List<Integer> positions);

        void onLetterSelected(int position, boolean b);
    }

}
