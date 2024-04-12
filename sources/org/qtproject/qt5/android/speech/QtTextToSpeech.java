package org.qtproject.qt5.android.speech;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class QtTextToSpeech {
    private final long mId;
    private float mPitch;
    private float mRate;
    private TextToSpeech mTts;
    private boolean mInitialized = false;
    private float mVolume = 1.0f;
    private final TextToSpeech.OnInitListener mTtsChangeListener = new TextToSpeech.OnInitListener() { // from class: org.qtproject.qt5.android.speech.QtTextToSpeech.1
        @Override // android.speech.tts.TextToSpeech.OnInitListener
        public void onInit(int i) {
            Log.w("QtTextToSpeech", "tts initialized");
            if (i == 0) {
                QtTextToSpeech.this.mInitialized = true;
                QtTextToSpeech qtTextToSpeech = QtTextToSpeech.this;
                qtTextToSpeech.notifyReady(qtTextToSpeech.mId);
                return;
            }
            QtTextToSpeech.this.mInitialized = false;
            QtTextToSpeech qtTextToSpeech2 = QtTextToSpeech.this;
            qtTextToSpeech2.notifyError(qtTextToSpeech2.mId);
        }
    };
    private final UtteranceProgressListener mTtsUtteranceProgressListener = new UtteranceProgressListener() { // from class: org.qtproject.qt5.android.speech.QtTextToSpeech.2
        @Override // android.speech.tts.UtteranceProgressListener
        public void onDone(String str) {
            Log.w("UtteranceProgressListener", "onDone");
            if (str.equals("UtteranceId")) {
                QtTextToSpeech qtTextToSpeech = QtTextToSpeech.this;
                qtTextToSpeech.notifyReady(qtTextToSpeech.mId);
            }
        }

        @Override // android.speech.tts.UtteranceProgressListener
        public void onError(String str) {
            Log.w("UtteranceProgressListener", "onError");
            if (str.equals("UtteranceId")) {
                QtTextToSpeech qtTextToSpeech = QtTextToSpeech.this;
                qtTextToSpeech.notifyReady(qtTextToSpeech.mId);
            }
        }

        @Override // android.speech.tts.UtteranceProgressListener
        public void onStart(String str) {
            Log.w("UtteranceProgressListener", "onStart");
            if (str.equals("UtteranceId")) {
                QtTextToSpeech qtTextToSpeech = QtTextToSpeech.this;
                qtTextToSpeech.notifySpeaking(qtTextToSpeech.mId);
            }
        }
    };

    public native void notifyError(long j);

    public native void notifyReady(long j);

    public native void notifySpeaking(long j);

    public static QtTextToSpeech open(Context context, long j) {
        return new QtTextToSpeech(context, j);
    }

    QtTextToSpeech(Context context, long j) {
        this.mPitch = 1.0f;
        this.mRate = 1.0f;
        this.mId = j;
        TextToSpeech textToSpeech = new TextToSpeech(context, this.mTtsChangeListener);
        this.mTts = textToSpeech;
        textToSpeech.setOnUtteranceProgressListener(this.mTtsUtteranceProgressListener);
        ContentResolver contentResolver = context.getContentResolver();
        try {
            this.mPitch = Settings.Secure.getFloat(contentResolver, "tts_default_pitch") / 100.0f;
        } catch (Settings.SettingNotFoundException e) {
            this.mPitch = 1.0f;
        }
        try {
            this.mRate = Settings.Secure.getFloat(contentResolver, "tts_default_rate") / 100.0f;
        } catch (Settings.SettingNotFoundException e2) {
            this.mRate = 1.0f;
        }
    }

    public void say(String str) {
        Log.w("QtTextToSpeech", str);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("utteranceId", "UtteranceId");
        hashMap.put("volume", Float.toString(this.mVolume));
        int speak = this.mTts.speak(str, 0, hashMap);
        Log.w("QtTextToSpeech", "RESULT: " + Integer.toString(speak));
    }

    public void stop() {
        Log.w("QtTextToSpeech", "STOP");
        this.mTts.stop();
    }

    public float pitch() {
        return this.mPitch;
    }

    public int setPitch(float f) {
        if (Float.compare(f, this.mPitch) == 0) {
            return -1;
        }
        int pitch = this.mTts.setPitch(f);
        if (pitch == 0) {
            this.mPitch = f;
        }
        return pitch;
    }

    public int setRate(float f) {
        if (Float.compare(f, this.mRate) == 0) {
            return -1;
        }
        int speechRate = this.mTts.setSpeechRate(f);
        if (speechRate == 0) {
            this.mRate = f;
        }
        return speechRate;
    }

    public void shutdown() {
        this.mTts.shutdown();
    }

    public float volume() {
        return this.mVolume;
    }

    public int setVolume(float f) {
        if (Float.compare(f, this.mVolume) == 0) {
            return -1;
        }
        this.mVolume = f;
        return 0;
    }

    public boolean setLocale(Locale locale) {
        int language = this.mTts.setLanguage(locale);
        return (language == -2 || language == -1) ? false : true;
    }

    public List<Object> getAvailableVoices() {
        if (this.mInitialized && Build.VERSION.SDK_INT >= 21) {
            return new ArrayList(this.mTts.getVoices());
        }
        return new ArrayList();
    }

    public List<Locale> getAvailableLocales() {
        if (this.mInitialized && Build.VERSION.SDK_INT >= 21) {
            return new ArrayList(this.mTts.getAvailableLanguages());
        }
        return new ArrayList();
    }

    public Locale getLocale() {
        return this.mTts.getLanguage();
    }

    public Object getVoice() {
        if (this.mInitialized && Build.VERSION.SDK_INT >= 21) {
            return this.mTts.getVoice();
        }
        return null;
    }

    public boolean setVoice(String str) {
        if (this.mInitialized && Build.VERSION.SDK_INT >= 21) {
            Iterator<Voice> it = this.mTts.getVoices().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Voice next = it.next();
                if (next.getName().equals(str)) {
                    if (this.mTts.setVoice(next) == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
