package hailey.shhshhshh.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import hailey.shhshhshh.R;

/**
 * Created by alonm on 4/9/15.
 */
public class TimeBoardCustomView extends View {

    private Bitmap mBitmap1;
    private Bitmap mBitmap2;
    private Bitmap mBitmap3;
    private boolean mIsButton1Selected = false;
    private boolean mIsButton2Selected = false;
    private boolean mIsButton3Selected = false;

    public TimeBoardCustomView(Context context) {
        super(context);
        initBitmaps();
    }

    public TimeBoardCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBitmaps();
    }

    public TimeBoardCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBitmaps();
    }


    private void initBitmaps() {
        mBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.ten_min_board_non_active);
        mBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.twenty_min_board_non_active);
        mBitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.thirty_min_board_non_active);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap1, 0, 0, null);
        canvas.drawBitmap(mBitmap2, 0, 0, null);
        canvas.drawBitmap(mBitmap3, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int xx = (int) event.getX();
                int yy = (int) event.getY();
                if (Color.alpha(mBitmap1.getPixel(xx, yy)) != 0) {
                    initBitmaps();
                    if (!mIsButton1Selected){
                        mIsButton1Selected = true;
                        mBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.ten_min_board_active);
                    } else {
                        mIsButton1Selected = false;
                    }
                } else if (Color.alpha(mBitmap2.getPixel(xx, yy)) != 0) {
                    initBitmaps();
                    if (!mIsButton2Selected){
                        mIsButton2Selected = true;
                        mBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.twenty_min_board_active);
                    } else {
                        mIsButton2Selected = false;
                    }
                } else if (Color.alpha(mBitmap3.getPixel(xx, yy)) != 0) {
                    initBitmaps();
                    if (!mIsButton3Selected){
                        mIsButton3Selected = true;
                        mBitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.thirty_min_board_active);
                    } else {
                        mIsButton3Selected = false;
                    }
                }
                break;
        }

        invalidate();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mBitmap1.getWidth(), mBitmap1.getHeight());
    }
}
