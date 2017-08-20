package com.demo.benny.wordsearchgame;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benny on 17/08/2017.
 * Filters out touch events so that only valid words get selected
 */

class GridItemTouchListener implements RecyclerView.OnItemTouchListener {

    private final GridItemCallbacks listener;
    private int nbColumns = -1;
    private final List<Integer> selectedPos = new ArrayList<>();

    public void setSpanCount(int spanCount) {
        nbColumns = spanCount;
    }

    private enum Direction {
        RIGHT,
        DOWN,
        DIAGONAL,
        UNKNOWN
    }

    public interface GridItemCallbacks {
        void onWordSelected(List<Integer> positions);

        void onLetterSelected(int position, boolean b);
    }

    public GridItemTouchListener(GridItemCallbacks callbacks) {
        listener = callbacks;
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
                    int margin = rv.getMeasuredWidth() / nbColumns * 2 / 3;
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
                listener.onWordSelected(selectedPos);
                break;
        }
        return false;
    }

    private boolean isRight(int position) {
        int row = lastPos / nbColumns;
        return position / nbColumns == row;
    }

    private boolean isDown(int position) {
        int column = lastPos % nbColumns;
        return position % nbColumns == column;
    }

    private boolean isDiagonal(int position) {
        int row = lastPos / nbColumns;
        int column = lastPos % nbColumns;
        int deltaRow = position / nbColumns - row;
        int deltaColumn = position % nbColumns - column;
        return deltaRow == deltaColumn;
    }

    private void selectFirst(int position) {
        selectedPos.add(position);
        listener.onLetterSelected(position, true);
        lastPos = position;
    }

    private void selectRight(int position) {
        for (int i = lastPos + 1; i <= position; i++) {
            selectedPos.add(i);
            listener.onLetterSelected(i, true);
        }
    }

    private void selectDown(int position) {
        for (int i = lastPos + nbColumns; i <= position; i += nbColumns) {
            selectedPos.add(i);
            listener.onLetterSelected(i, true);
        }
    }

    private void selectDiagonal(int position) {
        for (int i = lastPos + nbColumns + 1; i <= position; i += nbColumns + 1) {
            selectedPos.add(i);
            listener.onLetterSelected(i, true);
        }
    }

    void unselectSelection() {
        for (int i = 0; i < selectedPos.size(); i++) {
            listener.onLetterSelected(selectedPos.get(i), false);
        }
    }

    private void clear() {
        selectedPos.clear();
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
