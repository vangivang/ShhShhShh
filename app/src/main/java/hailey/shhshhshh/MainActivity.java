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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {

    public static final String IS_RUNNING = "IS_RUNNING";
    public static final String MEDIA_STOPPED_ACTION = "MEDIA_STOPPED_ACTION";

    private static final long ONE_MINUTE = 1000 * 60;
    private static final long FIVE_MINUTE = 5 * ONE_MINUTE;
    private static final long TEN_MINUTE = 10 * ONE_MINUTE;
    private static final long FIFTEEN_MINUTE = 15 * ONE_MINUTE;

    private Intent mPlayIntent;
    private boolean mIsRunning = false;
    private Button mStartButton;
    private Spinner mSpinner;
    private long mTimeServiceIsRunning;

    private BroadcastReceiver mMediaPlayerStoppedBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MEDIA_STOPPED_ACTION)) {
                mTimeServiceIsRunning = System.currentTimeMillis() - mTimeServiceIsRunning;
                mStartButton.setEnabled(true);
                mSpinner.setEnabled(true);
                displayAlert();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(mToolBar);

        mSpinner = (Spinner) findViewById(R.id.shutDownSpinner);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long timeToFinish;

                switch (position) {
                    case 0:
                        timeToFinish = FIVE_MINUTE;
                        break;
                    case 1:
                        timeToFinish = TEN_MINUTE;
                        break;
                    case 2:
                        timeToFinish = FIFTEEN_MINUTE;
                        break;
                    default:
                        timeToFinish = FIVE_MINUTE;
                        break;
                }

                mPlayIntent.putExtra(MediaService.TIME_TO_FINISH, timeToFinish);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        updatePlayIntent(FIVE_MINUTE);
        mStartButton = (Button) findViewById(R.id.startButton);
        Button mStopButton = (Button) findViewById(R.id.stopButton);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                mSpinner.setEnabled(false);
                mTimeServiceIsRunning = System.currentTimeMillis();
                startService(mPlayIntent);
                mIsRunning = true;
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(mPlayIntent);
                mIsRunning = false;
                if (!mStartButton.isEnabled()) {
                    mStartButton.setEnabled(true);
                    mSpinner.setEnabled(true);
                }
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(IS_RUNNING, false)) {
                mIsRunning = true;
                mStartButton.setEnabled(false);
            } else {
                mIsRunning = false;
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

    private void updatePlayIntent(long timeToFinish){
        if (mPlayIntent == null) {
            mPlayIntent = new Intent(this, MediaService.class);
            mPlayIntent.putExtra(MediaService.TIME_TO_FINISH, timeToFinish);
        }
    }

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
        outState.putBoolean(IS_RUNNING, mIsRunning);
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
}
