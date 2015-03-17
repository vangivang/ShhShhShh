package hailey.shhshhshh;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by alonm on 3/14/15.
 */
public class MediaService extends Service {

    private MediaPlayer mMediaPlayer;
    final private MediaBinder mBinder = new MediaBinder();

    private Handler mHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    public void stopMediaPlayer() {
        release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();

    }

    private void release() {
        if(mMediaPlayer == null) {
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public void startMediaPlayer(long timeToFinish) {
        mMediaPlayer = MediaPlayer.create(MediaService.this, R.raw.shhshhshh);
        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    stopMediaPlayer();
                    LocalBroadcastManager.getInstance(MediaService.this).sendBroadcast(new Intent(MainActivity.MEDIA_STOPPED_ACTION));
                }
            }
        }, timeToFinish);


    }

    public class MediaBinder extends Binder {
        public MediaService getService() {
            return MediaService.this;
        }
    }
}
