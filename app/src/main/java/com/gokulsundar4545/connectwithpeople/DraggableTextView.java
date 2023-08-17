package com.gokulsundar4545.connectwithpeople;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class DraggableTextView extends TextView {

    public DraggableTextView(Context context) {
        super(context);
        init();
    }

    public DraggableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DraggableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(new OnTouchListener() {
            private float xDelta;
            private float yDelta;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xDelta = view.getX() - event.getRawX();
                        yDelta = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .x(event.getRawX() + xDelta)
                                .y(event.getRawY() + yDelta)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }
}
