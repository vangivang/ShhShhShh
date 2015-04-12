package com.shhshhshh;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.shhshhshh.views.TimeBoardCustomView;

/**
 * Created by alonm on 3/14/15.
 */
public class MediaService extends Service {

    public static final String INTENT_TIME_TO_FINISH = "INTENT_TIME_TO_FINISH";
    public static final String INTENT_MEDIA_TYPE = "INTENT_MEDIA_TYPE" ;

    public enum MediaType{SHH, DEEP_WHITE_NOISE}
    private MediaPlayer mMediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long timeToFinish = intent.getLongExtra(INTENT_TIME_TO_FINISH, TimeBoardCustomView.TimeAmount.TEN.timeValue());
        MediaType mediaType = (MediaType) intent.getSerializableExtra(INTENT_MEDIA_TYPE);

        startMediaPlayer(timeToFinish, mediaType);
        return Service.START_NOT_STICKY;
    }

    private void stopMediaPlayer() {
        release();
        LocalBroadcastManager.getInstance(MediaService.this).sendBroadcast(new Intent(MainActivity.MEDIA_STOPPED_ACTION));
        stopForeground(true);
    }

    @Override
    public void onDestroy() {
        release();
    }

    private void release() {
        if (mMediaPlayer == null) {
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    private void startMediaPlayer(long timeToFinish, MediaType mediaType) {

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_icon)
                        .setContentTitle("ShhShhShh Player")
                        .setContentText("Playing the ShhShhShh song...")
                        .setContentIntent(resultPendingIntent);
        Notification noti = mBuilder.build();
        startForeground(1337, noti);

        initWithLaunchMediaPlayer(timeToFinish, mediaType);

    }

    private void initWithLaunchMediaPlayer(long timeToFinish, MediaType mediaType) {

        if (mediaType == MediaType.SHH){
            mMediaPlayer = MediaPlayer.create(MediaService.this, R.raw.shhshhshh);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        stopMediaPlayer();
                    }
                }
            }, timeToFinish);
        } else {
            mMediaPlayer = MediaPlayer.create(MediaService.this, R.raw.white_noise_amp);
        }

        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }
}
