package com.demo.benny.wordsearchgame;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * This class listens for swipe inputs and calls callback functions when a word or a letter
 * gets selected. The swipe movement has to be horizontal, vertical or diagonal.
 */

class GridItemTouchListener implements RecyclerView.OnItemTouchListener {

    private final SelectedListener mListener;
    private int mNbColumns = -1;
    private final List<Integer> mPositions = new ArrayList<>();

    public void setNbColumns(int nbColumns) {
        mNbColumns = nbColumns;
    }

    private enum Direction {
        RIGHT,
        DOWN,
        DIAGONAL,
        UNKNOWN
    }

    /**
     * Callback functions when a letter or a word has been selected.
     */
    public interface SelectedListener {
        void onWordSelected(List<Integer> positions);

        void onLetterSelected(int position, boolean b);
    }

    GridItemTouchListener(SelectedListener listener) {
        mListener = listener;
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
                mListener.onWordSelected(mPositions);
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
        mListener.onLetterSelected(position, true);
        lastPos = position;
    }

    private void selectRight(int position) {
        for (int i = lastPos + 1; i <= position; i++) {
            mPositions.add(i);
            mListener.onLetterSelected(i, true);
        }
    }

    private void selectDown(int position) {
        for (int i = lastPos + mNbColumns; i <= position; i += mNbColumns) {
            mPositions.add(i);
            mListener.onLetterSelected(i, true);
        }
    }

    private void selectDiagonal(int position) {
        for (int i = lastPos + mNbColumns + 1; i <= position; i += mNbColumns + 1) {
            mPositions.add(i);
            mListener.onLetterSelected(i, true);
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

}
