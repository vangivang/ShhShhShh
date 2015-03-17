package hailey.shhshhshh;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;


public class MainActivity extends ActionBarActivity {

    public static final String IS_RUNNING = "IS_RUNNING";
    public static final String MEDIA_STOPPED_ACTION = "MEDIA_STOPPED_ACTION";
    private static final long ONE_MINUTE = 1000 * 60;
    private Intent mPlayIntent;
    private MediaService mMediaService;
    private boolean mIsRunning = false;
    private long mTimeToFinish;
    private Button mStartButton;

    private BroadcastReceiver mMediaPlayerStoppedBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MEDIA_STOPPED_ACTION)){
                mStartButton.setEnabled(true);
                mSpinner.setEnabled(true);
            }
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaService.MediaBinder binder = (MediaService.MediaBinder) service;
            mMediaService = binder.getService();
            LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mMediaPlayerStoppedBroadCast, new IntentFilter(MEDIA_STOPPED_ACTION));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMediaService = null;
        }
    };
    private Spinner mSpinner;

    @Override
    protected void onResume() {
        super.onResume();
        bindService(mPlayIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mServiceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(mToolBar);

        if (mPlayIntent == null){
            mPlayIntent = new Intent(this, MediaService.class);
        }

        startService(mPlayIntent);

        mSpinner = (Spinner) findViewById(R.id.shutDownSpinner);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mTimeToFinish = 5 * ONE_MINUTE;
                        break;
                    case 1:
                        mTimeToFinish = 10 * ONE_MINUTE;
                        break;
                    case 2:
                        mTimeToFinish = 15 * ONE_MINUTE;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mStartButton = (Button) findViewById(R.id.startButton);
        Button mStopButton = (Button) findViewById(R.id.stopButton);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                mSpinner.setEnabled(false);
                if (mMediaService != null) {
                    mMediaService.startMediaPlayer(mTimeToFinish);
                    mIsRunning = true;
                }
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaService.stopMediaPlayer();
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
            }
        }
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
