package com.linciycuans.homework9.Information;

/**
 *  This file is created by linciy on 2016/11/21.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyLetterView extends View {
    OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    public static String[] AlphaBeta = {  " ","A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"};
    int choose = -1;
    Paint paint = new Paint();

    public MyLetterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyLetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLetterView(Context context) {
        super(context);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showBkg) {
            canvas.drawColor(Color.parseColor("#40000000"));
        }

        int singleHeight = getHeight() / AlphaBeta.length;
        for (int i = 0; i < AlphaBeta.length; i++) {
            paint.setColor(Color.DKGRAY);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(20);
            if (i == choose) {
                paint.setColor(Color.parseColor("#ffffff"));
                paint.setFakeBoldText(true);
            }
            float x_position = getWidth() / 2 - paint.measureText(AlphaBeta[i]) / 2;
            float y_position = singleHeight * i + singleHeight;
            canvas.drawText(AlphaBeta[i], x_position, y_position, paint);
            paint.reset();
        }

    }

    private boolean showBkg = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * AlphaBeta.length);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBkg = true;
                if (oldChoose != c && listener != null) {
                    if (c > 0 && c < AlphaBeta.length) {
                        listener.onTouchingLetterChanged(AlphaBeta[c]);
                        choose = c;
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c && listener != null) {
                    if (c > 0 && c < AlphaBeta.length) {
                        listener.onTouchingLetterChanged(AlphaBeta[c]);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBkg = false;
                choose = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }
}

