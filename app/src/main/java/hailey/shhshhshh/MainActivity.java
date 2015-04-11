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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;

import java.util.concurrent.TimeUnit;

import hailey.shhshhshh.views.TimeBoardCustomView;
import hailey.shhshhshh.views.WhiteNoiseBoard;


public class MainActivity extends ActionBarActivity {

    public static final String IS_PLAYING_SHH = "IS_PLAYING_SHH";
    public static final String MEDIA_STOPPED_ACTION = "MEDIA_STOPPED_ACTION";
    private static final String IS_PLAYING_WHITE_NOISE = "IS_PLAYING_WHITE_NOISE";
    private static final String SERVICE_TIME_OUT_VALUE = "SERVICE_TIME_OUT_VALUE";

    private Intent mPlayIntent;
    private boolean mIsPlayingShh = false;
    private ImageButton mStartButton;
    private WhiteNoiseBoard mDeepWhiteNoiseButton;
    private TimeBoardCustomView mTimeBoardCustomView;
    private ImageButton mSpinningArrow;
    private long mTimeServiceIsRunning;
    private boolean mIsPlayingDeepWhiteNoise = false;
    private long mStopServiceValue;

    private BroadcastReceiver mMediaPlayerStoppedBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MEDIA_STOPPED_ACTION)) {
                mTimeServiceIsRunning = System.currentTimeMillis() - mTimeServiceIsRunning;
                mStartButton.setEnabled(true);
                mTimeBoardCustomView.setEnabled(true);
                mDeepWhiteNoiseButton.setEnabled(true);
                mDeepWhiteNoiseButton.initBitmap();
                setSpinningArrow(false);
                displayFinishedAlert();
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
                updatePlayIntent(timeAmount.timeValue(), MediaService.MediaType.SHH);
            }
        });

        mDeepWhiteNoiseButton = (WhiteNoiseBoard) findViewById(R.id.deepWhiteNoiseButton);
        mDeepWhiteNoiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlayingDeepWhiteNoise){
                    mIsPlayingDeepWhiteNoise = false;
                    mDeepWhiteNoiseButton.initBitmap();
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

                    mTimeBoardCustomView.initBitmaps();
                    updatePlayIntent(0, MediaService.MediaType.DEEP_WHITE_NOISE);
                    startService(mPlayIntent);
                    mIsPlayingDeepWhiteNoise = true;
                }
            }
        });

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
                updatePlayIntent(mTimeBoardCustomView.getCurrentTimeAmount().timeValue(), MediaService.MediaType.SHH); //TODO: don't forget to set this
                startService(mPlayIntent);
                mIsPlayingShh = true;
                setSpinningArrow(true);
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(mPlayIntent);
                mIsPlayingShh = false;
                mIsPlayingDeepWhiteNoise = false;
                setSpinningArrow(false);
                mDeepWhiteNoiseButton.initBitmap();
                mDeepWhiteNoiseButton.setEnabled(true);
                mStartButton.setEnabled(true);
                mTimeBoardCustomView.setEnabled(true);
            }
        });

        findViewById(R.id.informationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCreditAlert();
            }
        });

        if (savedInstanceState != null) {
            long timeOut = savedInstanceState.getLong(SERVICE_TIME_OUT_VALUE, TimeBoardCustomView.TimeAmount.TEN.timeValue());

            if (savedInstanceState.getBoolean(IS_PLAYING_SHH, false)) {
                mIsPlayingShh = true;
                mIsPlayingDeepWhiteNoise = false;
                mStartButton.setEnabled(false);
                mTimeBoardCustomView.setEnabled(false);
//                mTimeBoardCustomView.markSelectedButtonByTimeValue(timeOut);
                mDeepWhiteNoiseButton.setEnabled(false);
                setSpinningArrow(true);
                updatePlayIntent(timeOut, MediaService.MediaType.SHH);
            } else if (savedInstanceState.getBoolean(IS_PLAYING_WHITE_NOISE, false)){
                mIsPlayingShh = false;
                mIsPlayingDeepWhiteNoise = true;
                mStartButton.setEnabled(false);
                mTimeBoardCustomView.setEnabled(false);
//                mTimeBoardCustomView.markSelectedButtonByTimeValue(timeOut);
                mDeepWhiteNoiseButton.setEnabled(false);
                setSpinningArrow(true);
                updatePlayIntent(timeOut, MediaService.MediaType.SHH);
            } else {
                mIsPlayingDeepWhiteNoise = false;
                mIsPlayingShh = false;
                mStartButton.setEnabled(true);
                setSpinningArrow(false);
                updatePlayIntent(timeOut, MediaService.MediaType.SHH);
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

    private void setSpinningArrow(boolean isOn){
        if (mSpinningArrow != null){
            mSpinningArrow.animate().rotation(isOn? 180 : 0).setDuration(200l).setInterpolator(new AccelerateDecelerateInterpolator());
        }
    }

    private void updatePlayIntent(long timeToFinish, MediaService.MediaType mediaType){
        mStopServiceValue = timeToFinish;
        mPlayIntent = new Intent(this, MediaService.class);
        if (timeToFinish > 0){
            mPlayIntent.putExtra(MediaService.INTENT_TIME_TO_FINISH, timeToFinish);
        }

        mPlayIntent.putExtra(MediaService.INTENT_MEDIA_TYPE, mediaType);
    }

    private void displayCreditAlert(){
        String message = "Graphic elements Designed by Freepik.com";
        AlertDialog dialog = displayAlert(message);
        dialog.setTitle("Acknowledgements");
        dialog.show();
    }

    private void displayFinishedAlert() {
        String formatted = String.format("Operation ran for: " + "%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(mTimeServiceIsRunning),
                TimeUnit.MILLISECONDS.toSeconds(mTimeServiceIsRunning) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTimeServiceIsRunning))
        );

        displayAlert(formatted).show();
    }

    private AlertDialog displayAlert(String message) {
        return new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setNeutralButton(getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }
                )
                .create();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_PLAYING_SHH, mIsPlayingShh);
        outState.putSerializable(IS_PLAYING_WHITE_NOISE, mIsPlayingDeepWhiteNoise);
        outState.putLong(SERVICE_TIME_OUT_VALUE, mStopServiceValue);
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
