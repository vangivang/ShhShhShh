package hailey.shhshhshh;

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

/**
 * Created by alonm on 3/14/15.
 */
public class MediaService extends Service {

    public final static String TIME_TO_FINISH = "TIME_TO_FINISH";
    private MediaPlayer mMediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startMediaPlayer(intent.getLongExtra(TIME_TO_FINISH, 5000));
        return Service.START_NOT_STICKY;
    }

    public void stopMediaPlayer() {
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

    public void startMediaPlayer(long timeToFinish) {

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

        mMediaPlayer = MediaPlayer.create(MediaService.this, R.raw.shhshhshh);
        mMediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    stopMediaPlayer();
                }
            }
        }, timeToFinish);


    }
}
