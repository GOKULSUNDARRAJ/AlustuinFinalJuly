package com.gokulsundar4545.connectwithpeople;
import androidx.recyclerview.widget.RecyclerView;

public abstract class HideToolbarOnScrollListener extends RecyclerView.OnScrollListener {
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;
    private static final int HIDE_THRESHOLD = 20;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            onHide();
            controlsVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            onShow();
            controlsVisible = true;
            scrolledDistance = 0;
        }

        if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
            scrolledDistance += dy;
        }
    }

    public abstract void onHide();
    public abstract void onShow();
}
