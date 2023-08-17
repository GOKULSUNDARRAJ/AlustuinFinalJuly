package com.gokulsundar4545.connectwithpeople;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class DraggableImageView extends ImageView {

    private float x, y;

    public DraggableImageView(Context context) {
        super(context);
    }

    public DraggableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getRawX() - getTranslationX();
                y = event.getRawY() - getTranslationY();
                break;
            case MotionEvent.ACTION_MOVE:
                setTranslationX(event.getRawX() - x);
                setTranslationY(event.getRawY() - y);
                break;
        }
        return true;
    }
}
