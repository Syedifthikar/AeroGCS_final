package org.qtproject.qt5.android.multimedia;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceHolder;
import com.ftdi.j2xx.ft4222.FT_4222_Defines;
import com.hoho.android.usbserial.driver.UsbId;
import java.util.HashMap;
import org.pdrl.AeroGCS.BuildConfig;

/* loaded from: classes.dex */
public class QtAndroidMediaPlayer {
    private static final String TAG = "Qt MediaPlayer";
    private final Context mContext;
    private final long mID;
    private MediaPlayer mMediaPlayer = null;
    private AudioAttributes mAudioAttributes = null;
    private HashMap<String, String> mHeaders = null;
    private Uri mUri = null;
    private boolean mMuted = false;
    private int mVolume = 100;
    private SurfaceHolder mSurfaceHolder = null;
    private volatile int mState = 1;

    public native void onBufferingUpdateNative(int i, long j);

    public native void onDurationChangedNative(int i, long j);

    public native void onErrorNative(int i, int i2, long j);

    public native void onInfoNative(int i, int i2, long j);

    public native void onProgressUpdateNative(int i, long j);

    public native void onStateChangedNative(int i, long j);

    public native void onVideoSizeChangedNative(int i, int i2, long j);

    /* loaded from: classes.dex */
    private class State {
        static final int Error = 512;
        static final int Idle = 2;
        static final int Initialized = 16;
        static final int Paused = 128;
        static final int PlaybackCompleted = 256;
        static final int Prepared = 8;
        static final int Preparing = 4;
        static final int Started = 32;
        static final int Stopped = 64;
        static final int Uninitialized = 1;

        private State() {
            QtAndroidMediaPlayer.this = r1;
        }
    }

    /* loaded from: classes.dex */
    private class MediaPlayerErrorListener implements MediaPlayer.OnErrorListener {
        private MediaPlayerErrorListener() {
            QtAndroidMediaPlayer.this = r1;
        }

        @Override // android.media.MediaPlayer.OnErrorListener
        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
            QtAndroidMediaPlayer.this.setState(512);
            QtAndroidMediaPlayer qtAndroidMediaPlayer = QtAndroidMediaPlayer.this;
            qtAndroidMediaPlayer.onErrorNative(i, i2, qtAndroidMediaPlayer.mID);
            return true;
        }
    }

    /* loaded from: classes.dex */
    private class MediaPlayerBufferingListener implements MediaPlayer.OnBufferingUpdateListener {
        private int mBufferPercent;

        private MediaPlayerBufferingListener() {
            QtAndroidMediaPlayer.this = r1;
            this.mBufferPercent = -1;
        }

        @Override // android.media.MediaPlayer.OnBufferingUpdateListener
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            if (this.mBufferPercent == i) {
                return;
            }
            QtAndroidMediaPlayer qtAndroidMediaPlayer = QtAndroidMediaPlayer.this;
            this.mBufferPercent = i;
            qtAndroidMediaPlayer.onBufferingUpdateNative(i, qtAndroidMediaPlayer.mID);
        }
    }

    /* loaded from: classes.dex */
    private class MediaPlayerCompletionListener implements MediaPlayer.OnCompletionListener {
        private MediaPlayerCompletionListener() {
            QtAndroidMediaPlayer.this = r1;
        }

        @Override // android.media.MediaPlayer.OnCompletionListener
        public void onCompletion(MediaPlayer mediaPlayer) {
            QtAndroidMediaPlayer.this.setState(256);
        }
    }

    /* loaded from: classes.dex */
    private class MediaPlayerInfoListener implements MediaPlayer.OnInfoListener {
        private MediaPlayerInfoListener() {
            QtAndroidMediaPlayer.this = r1;
        }

        @Override // android.media.MediaPlayer.OnInfoListener
        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
            QtAndroidMediaPlayer qtAndroidMediaPlayer = QtAndroidMediaPlayer.this;
            qtAndroidMediaPlayer.onInfoNative(i, i2, qtAndroidMediaPlayer.mID);
            return true;
        }
    }

    /* loaded from: classes.dex */
    private class MediaPlayerPreparedListener implements MediaPlayer.OnPreparedListener {
        private MediaPlayerPreparedListener() {
            QtAndroidMediaPlayer.this = r1;
        }

        @Override // android.media.MediaPlayer.OnPreparedListener
        public void onPrepared(MediaPlayer mediaPlayer) {
            QtAndroidMediaPlayer.this.setState(8);
            QtAndroidMediaPlayer qtAndroidMediaPlayer = QtAndroidMediaPlayer.this;
            qtAndroidMediaPlayer.onDurationChangedNative(qtAndroidMediaPlayer.getDuration(), QtAndroidMediaPlayer.this.mID);
        }
    }

    /* loaded from: classes.dex */
    private class MediaPlayerSeekCompleteListener implements MediaPlayer.OnSeekCompleteListener {
        private MediaPlayerSeekCompleteListener() {
            QtAndroidMediaPlayer.this = r1;
        }

        @Override // android.media.MediaPlayer.OnSeekCompleteListener
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            QtAndroidMediaPlayer qtAndroidMediaPlayer = QtAndroidMediaPlayer.this;
            qtAndroidMediaPlayer.onProgressUpdateNative(qtAndroidMediaPlayer.getCurrentPosition(), QtAndroidMediaPlayer.this.mID);
        }
    }

    /* loaded from: classes.dex */
    private class MediaPlayerVideoSizeChangedListener implements MediaPlayer.OnVideoSizeChangedListener {
        private MediaPlayerVideoSizeChangedListener() {
            QtAndroidMediaPlayer.this = r1;
        }

        @Override // android.media.MediaPlayer.OnVideoSizeChangedListener
        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
            QtAndroidMediaPlayer qtAndroidMediaPlayer = QtAndroidMediaPlayer.this;
            qtAndroidMediaPlayer.onVideoSizeChangedNative(i, i2, qtAndroidMediaPlayer.mID);
        }
    }

    public QtAndroidMediaPlayer(Context context, long j) {
        this.mID = j;
        this.mContext = context;
    }

    public MediaPlayer getMediaPlayerHandle() {
        return this.mMediaPlayer;
    }

    public void setState(int i) {
        if (this.mState == i) {
            return;
        }
        this.mState = i;
        onStateChangedNative(this.mState, this.mID);
    }

    private void init() {
        if (this.mMediaPlayer == null) {
            this.mMediaPlayer = new MediaPlayer();
            setState(2);
            setVolumeHelper(this.mMuted ? 0 : this.mVolume);
            setAudioAttributes(this.mMediaPlayer, this.mAudioAttributes);
        }
    }

    public void start() {
        if ((this.mState & UsbId.DEVICE_UBLOX_8) == 0) {
            return;
        }
        try {
            this.mMediaPlayer.start();
            setState(32);
        } catch (IllegalStateException e) {
            Log.d(TAG, BuildConfig.FLAVOR + e.getMessage());
        }
    }

    public void pause() {
        if ((this.mState & 416) == 0) {
            return;
        }
        try {
            this.mMediaPlayer.pause();
            setState(128);
        } catch (IllegalStateException e) {
            Log.d(TAG, BuildConfig.FLAVOR + e.getMessage());
        }
    }

    public void stop() {
        if ((this.mState & 488) == 0) {
            return;
        }
        try {
            this.mMediaPlayer.stop();
            setState(64);
        } catch (IllegalStateException e) {
            Log.d(TAG, BuildConfig.FLAVOR + e.getMessage());
        }
    }

    public void seekTo(int i) {
        if ((this.mState & UsbId.DEVICE_UBLOX_8) == 0) {
            return;
        }
        try {
            this.mMediaPlayer.seekTo(i);
        } catch (IllegalStateException e) {
            Log.d(TAG, BuildConfig.FLAVOR + e.getMessage());
        }
    }

    public boolean isPlaying() {
        if ((this.mState & 506) == 0) {
            return false;
        }
        try {
            return this.mMediaPlayer.isPlaying();
        } catch (IllegalStateException e) {
            Log.d(TAG, BuildConfig.FLAVOR + e.getMessage());
            return false;
        }
    }

    public void prepareAsync() {
        if ((this.mState & 80) == 0) {
            return;
        }
        try {
            this.mMediaPlayer.prepareAsync();
            setState(4);
        } catch (IllegalStateException e) {
            Log.d(TAG, BuildConfig.FLAVOR + e.getMessage());
        }
    }

    public void initHeaders() {
        this.mHeaders = new HashMap<>();
    }

    public void setHeader(String str, String str2) {
        this.mHeaders.put(str, str2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:556:0x0161 A[Catch: IOException -> 0x015d, TRY_LEAVE, TryCatch #23 {IOException -> 0x015d, blocks: (B:552:0x0159, B:556:0x0161), top: B:632:0x0159 }] */
    /* JADX WARN: Removed duplicated region for block: B:568:0x018d A[Catch: IOException -> 0x0189, TRY_LEAVE, TryCatch #5 {IOException -> 0x0189, blocks: (B:564:0x0185, B:568:0x018d), top: B:622:0x0185 }] */
    /* JADX WARN: Removed duplicated region for block: B:580:0x01b9 A[Catch: IOException -> 0x01b5, TRY_LEAVE, TryCatch #0 {IOException -> 0x01b5, blocks: (B:576:0x01b1, B:580:0x01b9), top: B:620:0x01b1 }] */
    /* JADX WARN: Removed duplicated region for block: B:592:0x01e6 A[Catch: IOException -> 0x01e2, TRY_LEAVE, TryCatch #20 {IOException -> 0x01e2, blocks: (B:588:0x01de, B:592:0x01e6), top: B:630:0x01de }] */
    /* JADX WARN: Removed duplicated region for block: B:604:0x0213 A[Catch: IOException -> 0x020f, TRY_LEAVE, TryCatch #11 {IOException -> 0x020f, blocks: (B:600:0x020b, B:604:0x0213), top: B:624:0x020b }] */
    /* JADX WARN: Removed duplicated region for block: B:619:0x0235  */
    /* JADX WARN: Removed duplicated region for block: B:620:0x01b1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:622:0x0185 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:624:0x020b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:630:0x01de A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:632:0x0159 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:639:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:640:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:641:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:642:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:643:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r15v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r15v1 */
    /* JADX WARN: Type inference failed for: r15v47 */
    /* JADX WARN: Type inference failed for: r15v7, types: [java.io.FileInputStream] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void setDataSource(java.lang.String r15) {
        /*
            Method dump skipped, instructions count: 566
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.qtproject.qt5.android.multimedia.QtAndroidMediaPlayer.setDataSource(java.lang.String):void");
    }

    public int getCurrentPosition() {
        if ((this.mState & 506) == 0) {
            return 0;
        }
        try {
            return this.mMediaPlayer.getCurrentPosition();
        } catch (IllegalStateException e) {
            Log.d(TAG, BuildConfig.FLAVOR + e.getMessage());
            return 0;
        }
    }

    public int getDuration() {
        if ((this.mState & 488) == 0) {
            return 0;
        }
        try {
            return this.mMediaPlayer.getDuration();
        } catch (IllegalStateException e) {
            Log.d(TAG, BuildConfig.FLAVOR + e.getMessage());
            return 0;
        }
    }

    public void setVolume(int i) {
        if (i < 0) {
            i = 0;
        }
        if (i > 100) {
            i = 100;
        }
        this.mVolume = i;
        if (!this.mMuted) {
            setVolumeHelper(i);
        }
    }

    private void setVolumeHelper(int i) {
        if ((this.mState & 506) == 0) {
            return;
        }
        float f = i / 100.0f;
        try {
            this.mMediaPlayer.setVolume(f, f);
        } catch (IllegalStateException e) {
            Log.d(TAG, BuildConfig.FLAVOR + e.getMessage());
        }
    }

    public SurfaceHolder display() {
        return this.mSurfaceHolder;
    }

    public void setDisplay(SurfaceHolder surfaceHolder) {
        this.mSurfaceHolder = surfaceHolder;
        if ((this.mState & 1) != 0) {
            return;
        }
        this.mMediaPlayer.setDisplay(this.mSurfaceHolder);
    }

    public int getVolume() {
        return this.mVolume;
    }

    public void mute(boolean z) {
        this.mMuted = z;
        setVolumeHelper(z ? 0 : this.mVolume);
    }

    public boolean isMuted() {
        return this.mMuted;
    }

    public void reset() {
        if ((this.mState & FT_4222_Defines.FT4222_STATUS.FT4222_GPIO_OPENDRAIN_INVALID_IN_OUTPUTMODE) == 0) {
            return;
        }
        this.mMediaPlayer.reset();
        setState(2);
    }

    public void release() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
        setState(1);
    }

    public void setAudioAttributes(int i, int i2) {
        AudioAttributes build = new AudioAttributes.Builder().setUsage(i2).setContentType(i).build();
        this.mAudioAttributes = build;
        setAudioAttributes(this.mMediaPlayer, build);
    }

    private static void setAudioAttributes(MediaPlayer mediaPlayer, AudioAttributes audioAttributes) {
        if (mediaPlayer == null || audioAttributes == null) {
            return;
        }
        try {
            mediaPlayer.setAudioAttributes(audioAttributes);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, BuildConfig.FLAVOR + e.getMessage());
        }
    }
}
