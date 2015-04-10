package hailey.shhshhshh;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.concurrent.TimeUnit;

import hailey.shhshhshh.views.TimeBoardCustomView;
import hailey.shhshhshh.views.WhiteNoiseBoard;


public class MainActivity extends ActionBarActivity {

    public static final String IS_PLAYING_SHH = "IS_PLAYING_SHH";
    public static final String MEDIA_STOPPED_ACTION = "MEDIA_STOPPED_ACTION";

    private static final long ONE_MINUTE = 1000 * 60;
    private static final String IS_PLAYING_WHITE_NOISE = "IS_PLAYING_WHITE_NOISE";

    private Intent mPlayIntent;
    private boolean mIsPlayingShh = false;
    private ImageButton mStartButton;
    private WhiteNoiseBoard mDeepWhiteNoiseButton;
    private TimeBoardCustomView mTimeBoardCustomView;
    private ImageButton mSpinningArrow;
    private long mTimeServiceIsRunning;
    private boolean mIsPlayingDeepWhiteNoise = false;

    private BroadcastReceiver mMediaPlayerStoppedBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MEDIA_STOPPED_ACTION)) {
                mTimeServiceIsRunning = System.currentTimeMillis() - mTimeServiceIsRunning;
                mStartButton.setEnabled(true);
                mTimeBoardCustomView.setEnabled(true);
                displayAlert();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpinningArrow = (ImageButton) findViewById(R.id.spinningArrow);

        mTimeBoardCustomView = (TimeBoardCustomView) findViewById(R.id.timeBoard);
        mTimeBoardCustomView.setOnClickListener(new TimeBoardCustomView.OnTimeAmountClickListener() {
            @Override
            public void onTimeAmountClicked(TimeBoardCustomView.TimeAmount timeAmount) {
                long timeToFinish;
                switch (timeAmount){
                    case TEN:
                        timeToFinish = 10 * ONE_MINUTE;
                        break;
                    case TWENTY:
                        timeToFinish = 20 * ONE_MINUTE;
                        break;
                    case THIRTY:
                        timeToFinish = 30 * ONE_MINUTE;
                        break;
                    default:
                        timeToFinish = 10 * ONE_MINUTE;
                }

                updatePlayIntent(timeToFinish, MediaService.MediaType.SHH);
            }
        });

        mDeepWhiteNoiseButton = (WhiteNoiseBoard) findViewById(R.id.deepWhiteNoiseButton);

        mStartButton = (ImageButton) findViewById(R.id.startButton);
        ImageButton mStopButton = (ImageButton) findViewById(R.id.stopButton);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mIsPlayingDeepWhiteNoise){
                    stopService(mPlayIntent);
                }

                v.setEnabled(false);
                mDeepWhiteNoiseButton.setEnabled(false);
                mTimeBoardCustomView.setEnabled(false);
                mTimeServiceIsRunning = System.currentTimeMillis();
                updatePlayIntent(10 * ONE_MINUTE, MediaService.MediaType.SHH); //TODO: don't forget to set this
                startService(mPlayIntent);
                mIsPlayingShh = true;
                mSpinningArrow.animate().rotation(180f).setDuration(200l).setInterpolator(new AccelerateDecelerateInterpolator());
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(mPlayIntent);
                mIsPlayingShh = false;
                mSpinningArrow.animate().rotation(0f).setDuration(200l).setInterpolator(new AccelerateDecelerateInterpolator());
                mIsPlayingDeepWhiteNoise = false;
                if (!mStartButton.isEnabled()) {
                    mStartButton.setEnabled(true);
                    mTimeBoardCustomView.setEnabled(true);
                    mTimeBoardCustomView.initBitmaps();
                }

                if (!mDeepWhiteNoiseButton.isEnabled()){
                    mDeepWhiteNoiseButton.setEnabled(true);
                }
            }
        });

        mDeepWhiteNoiseButton.setOnClickListener(new View.OnClickListener() { //TODO: this will not work. use a listener inside the view class
            @Override
            public void onClick(View v) {

                if (mIsPlayingDeepWhiteNoise){
                    mIsPlayingDeepWhiteNoise = false;
                    stopService(mPlayIntent);
                    if (!mStartButton.isEnabled()){
                        mStartButton.setEnabled(true);
                    }

                    if (!mTimeBoardCustomView.isEnabled()){
                        mTimeBoardCustomView.setEnabled(true);
                    }
                } else {
                    if (mIsPlayingShh){
                        stopService(mPlayIntent);
                    }

                    if (mStartButton.isEnabled()){
                        mStartButton.setEnabled(false);
                    }

                    if (mTimeBoardCustomView.isEnabled()){
                        mTimeBoardCustomView.setEnabled(false);
                    }

                    updatePlayIntent(0, MediaService.MediaType.DEEP_WHITE_NOISE);
                    startService(mPlayIntent);
                    mIsPlayingDeepWhiteNoise = true;
                }
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(IS_PLAYING_SHH, false)) {
                mIsPlayingShh = true;
                mIsPlayingDeepWhiteNoise = false;
                mStartButton.setEnabled(false);
            } else if (savedInstanceState.getBoolean(IS_PLAYING_WHITE_NOISE, false)){
                mIsPlayingShh = false;
                mIsPlayingDeepWhiteNoise = true;
                mStartButton.setEnabled(false);
            } else {
                mIsPlayingDeepWhiteNoise = false;
                mIsPlayingShh = false;
                mStartButton.setEnabled(true);
            }
        } else {
            LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mMediaPlayerStoppedBroadCast, new IntentFilter(MEDIA_STOPPED_ACTION));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(mMediaPlayerStoppedBroadCast);
    }

    private void updatePlayIntent(long timeToFinish, MediaService.MediaType mediaType){
        mPlayIntent = new Intent(this, MediaService.class);
        if (timeToFinish > 0){
            mPlayIntent.putExtra(MediaService.INTENT_TIME_TO_FINISH, timeToFinish);
        }

        mPlayIntent.putExtra(MediaService.INTENT_MEDIA_TYPE, mediaType);
    }

//    private long getCurrentTimeSelectionFromSpinner(){
//        long time;
//        int[] values = getResources().getIntArray(R.array.timer_values);
//        if (mSpinner != null){
//            time = values[mSpinner.getSelectedItemPosition()];
//        } else {
//            time = values[0];
//        }
//
//        return time * ONE_MINUTE;
//    }

    private void displayAlert() {
        String formatted = String.format("Operation ran for: " + "%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(mTimeServiceIsRunning),
                TimeUnit.MILLISECONDS.toSeconds(mTimeServiceIsRunning) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTimeServiceIsRunning))
        );

        new AlertDialog.Builder(this)
                .setMessage(formatted)
                .setCancelable(false)
                .setNeutralButton(getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                            }
                        }
                )
                .create().show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_PLAYING_SHH, mIsPlayingShh);
        outState.putSerializable(IS_PLAYING_WHITE_NOISE, mIsPlayingDeepWhiteNoise);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
