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

    private static final int ONE_MINUTE = 1000 ;

    private Bitmap mBitmap1;
    private Bitmap mBitmap2;
    private Bitmap mBitmap3;
    private boolean mIsTouchable = true;

    public enum TimeAmount {
        TEN(10 * ONE_MINUTE),
        TWENTY(20 * ONE_MINUTE),
        THIRTY(30 * ONE_MINUTE);

        private int mTimeInMillis;

        TimeAmount(int timeInMillis){
            mTimeInMillis = timeInMillis;
        }

        public int timeValue(){
            return mTimeInMillis;
        }
    }


    private TimeAmount mTimeAmount;
    private OnTimeAmountClickListener mOnTimeAmountClickListener;

    public interface OnTimeAmountClickListener{
        public void onTimeAmountClicked(TimeAmount timeAmount);
    }

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

    public void setOnClickListener(OnTimeAmountClickListener listener){
        mOnTimeAmountClickListener = listener;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mIsTouchable = enabled;
    }

    public void initBitmaps() {
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

        if (mIsTouchable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTimeAmount = TimeAmount.TEN;
                    int xx = (int) event.getX();
                    int yy = (int) event.getY();
                    if (Color.alpha(mBitmap1.getPixel(xx, yy)) != 0) {
                        initBitmaps();
                        mBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.ten_min_board_active);
                        mTimeAmount = TimeAmount.TEN;
                    } else if (Color.alpha(mBitmap2.getPixel(xx, yy)) != 0) {
                        initBitmaps();
                        mBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.twenty_min_board_active);
                        mTimeAmount = TimeAmount.TWENTY;
                    } else if (Color.alpha(mBitmap3.getPixel(xx, yy)) != 0) {
                        initBitmaps();
                        mBitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.thirty_min_board_active);
                        mTimeAmount = TimeAmount.THIRTY;
                    }
                    invalidate();
                    mOnTimeAmountClickListener.onTimeAmountClicked(mTimeAmount);
                    break;
            }

        }

        return mIsTouchable;
    }

    public TimeAmount getCurrentTimeAmount(){
        if (mTimeAmount == null){
            mTimeAmount = TimeAmount.TEN;
        }

        return mTimeAmount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mBitmap1.getWidth(), mBitmap1.getHeight());
    }
}
