package com.focustimer.app;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Vibrator;

public class NotificationHelper {

    private static ToneGenerator toneGen;

    private static ToneGenerator getToneGen() {
        if (toneGen == null) {
            try {
                toneGen = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90);
            } catch (Exception e) {
                // Audio stream might not be ready
            }
        }
        return toneGen;
    }

    public static void notifySessionComplete(Context ctx, PreferencesManager prefs) {
        if (prefs.isSoundEnabled()) {
            try {
                ToneGenerator tg = getToneGen();
                if (tg != null) {
                    tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 350);
                }
            } catch (Exception e) {}
        }

        if (prefs.isVibrateEnabled()) {
            try {
                Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
                if (v != null && v.hasVibrator()) {
                    long[] pattern = {0, 250, 150, 250};
                    v.vibrate(pattern, -1);
                }
            } catch (Exception e) {}
        }
    }

    public static void playClick(PreferencesManager prefs) {
        if (prefs.isSoundEnabled()) {
            try {
                ToneGenerator tg = getToneGen();
                if (tg != null) {
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP, 70);
                }
            } catch (Exception e) {}
        }
    }
}
