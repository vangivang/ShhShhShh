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
public class WhiteNoiseBoard extends View {

    private Bitmap mBitmap;
    private boolean mIsBitmapSelected = false;
    private boolean mIsTouchable = true;

    public WhiteNoiseBoard(Context context) {
        super(context);
        initBitmap();
    }

    public WhiteNoiseBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBitmap();
    }

    public WhiteNoiseBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBitmap();
    }

    public void initBitmap(){
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_noise_board_non_active);
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mIsTouchable = enabled;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mIsTouchable){
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    int xx = (int) event.getX();
                    int yy = (int) event.getY();

                    if (Color.alpha(mBitmap.getPixel(xx, yy)) != 0){
                        initBitmap();
                        if (!mIsBitmapSelected){
                            mIsBitmapSelected = true;
                            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_noise_board_active);
                            performClick();
                        } else {
                            mIsBitmapSelected = false;
                        }
                    }
                    invalidate();
                    break;
            }
        }

        return mIsTouchable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mBitmap.getWidth(), mBitmap.getHeight());
    }
}
