package com.focustimer.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private PreferencesManager prefs;

    private View rootView;
    private LinearLayout headerBar;
    private TextView tvAppTitle;
    private Button btnStats;
    private Button btnTheme;
    private Button btnSettings;
    private View headerDivider;

    private LinearLayout bannerLayout;
    private TextView tvStateBanner;
    private TextView tvCycleDots;

    private TimerRingView timerRing;
    private TextView tvCountdown;
    private TextView tvStateSubtitle;

    private LinearLayout bottomControls;
    private Button btnPrimaryAction;
    private Button btnSkip;
    private Button btnReset;
    private TextView tvKeyboardHint;

    // Timer State
    private TimerState currentState = TimerState.FOCUS;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private long totalDurationMillis = 25 * 60 * 1000L;
    private long remainingMillis = 25 * 60 * 1000L;
    private long targetEndTime = 0L;

    private int completedPomodorosInCycle = 0;

    private final Handler handler = new Handler();
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                long now = System.currentTimeMillis();
                remainingMillis = targetEndTime - now;

                if (remainingMillis <= 0) {
                    remainingMillis = 0;
                    isRunning = false;
                    isPaused = false;
                    updateTimerDisplay();
                    onSessionFinished();
                } else {
                    updateTimerDisplay();
                    handler.postDelayed(this, 100);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = new PreferencesManager(this);

        rootView = findViewById(R.id.rootView);
        headerBar = (LinearLayout) findViewById(R.id.headerBar);
        tvAppTitle = (TextView) findViewById(R.id.tvAppTitle);
        btnStats = (Button) findViewById(R.id.btnStats);
        btnTheme = (Button) findViewById(R.id.btnTheme);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        headerDivider = findViewById(R.id.headerDivider);

        bannerLayout = (LinearLayout) findViewById(R.id.bannerLayout);
        tvStateBanner = (TextView) findViewById(R.id.tvStateBanner);
        tvCycleDots = (TextView) findViewById(R.id.tvCycleDots);

        timerRing = (TimerRingView) findViewById(R.id.timerRing);
        tvCountdown = (TextView) findViewById(R.id.tvCountdown);
        tvStateSubtitle = (TextView) findViewById(R.id.tvStateSubtitle);

        bottomControls = (LinearLayout) findViewById(R.id.bottomControls);
        btnPrimaryAction = (Button) findViewById(R.id.btnPrimaryAction);
        btnSkip = (Button) findViewById(R.id.btnSkip);
        btnReset = (Button) findViewById(R.id.btnReset);
        tvKeyboardHint = (TextView) findViewById(R.id.tvKeyboardHint);

        // Header listeners
        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StatsActivity.class));
            }
        });

        btnTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.setDarkTheme(!prefs.isDarkTheme());
                applyCurrentTheme();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        // Controls
        btnPrimaryAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTimer();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipSession();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCurrentTimer();
            }
        });

        initTimerForCurrentState();
        applyCurrentTheme();
        FontHelper.apply(rootView, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyCurrentTheme();
        if (!isRunning && !isPaused) {
            initTimerForCurrentState();
        }
    }

    private void initTimerForCurrentState() {
        int minutes;
        if (currentState == TimerState.FOCUS) {
            minutes = prefs.getFocusMinutes();
        } else if (currentState == TimerState.SHORT_BREAK) {
            minutes = prefs.getShortBreakMinutes();
        } else {
            minutes = prefs.getLongBreakMinutes();
        }
        totalDurationMillis = minutes * 60 * 1000L;
        remainingMillis = totalDurationMillis;
        updateTimerDisplay();
    }

    private void toggleTimer() {
        NotificationHelper.playClick(prefs);
        if (isRunning) {
            // Pause
            isRunning = false;
            isPaused = true;
            handler.removeCallbacks(timerRunnable);
            setKeepScreenOn(false);
            btnPrimaryAction.setText("[ RESUME ]");
            tvStateSubtitle.setText("[ paused ]");
        } else {
            // Start or Resume
            isRunning = true;
            isPaused = false;
            targetEndTime = System.currentTimeMillis() + remainingMillis;
            setKeepScreenOn(true);
            btnPrimaryAction.setText("[ PAUSE ]");
            tvStateSubtitle.setText(currentState == TimerState.FOCUS ? "[ stay focused ]" : "[ rest your eyes ]");
            handler.post(timerRunnable);
        }
    }

    private void resetCurrentTimer() {
        NotificationHelper.playClick(prefs);
        if (isRunning) {
            isRunning = false;
            handler.removeCallbacks(timerRunnable);
        }
        isPaused = false;
        setKeepScreenOn(false);
        initTimerForCurrentState();
        btnPrimaryAction.setText(currentState == TimerState.FOCUS ? "[ START FOCUS ]" : "[ START BREAK ]");
        tvStateSubtitle.setText("[ ready ]");
    }

    private void skipSession() {
        NotificationHelper.playClick(prefs);
        if (isRunning) {
            isRunning = false;
            handler.removeCallbacks(timerRunnable);
        }
        isPaused = false;
        setKeepScreenOn(false);

        // Advance phase directly
        advanceToNextPhase(false);
    }

    private void onSessionFinished() {
        setKeepScreenOn(false);
        NotificationHelper.notifySessionComplete(this, prefs);

        if (currentState == TimerState.FOCUS) {
            prefs.recordCompletedFocusSession(prefs.getFocusMinutes());
            advanceToNextPhase(true);
        } else {
            // Break finished -> Back to focus
            currentState = TimerState.FOCUS;
            initTimerForCurrentState();
            btnPrimaryAction.setText("[ START FOCUS ]");
            tvStateSubtitle.setText("[ break finished — ready ]");
        }

        updateCycleDisplay();
        updateStateBanner();

        // Check auto-start breaks
        if (currentState != TimerState.FOCUS && prefs.isAutoStartBreaks()) {
            toggleTimer();
        }
    }

    private void advanceToNextPhase(boolean countCompletedPomodoro) {
        if (currentState == TimerState.FOCUS) {
            if (countCompletedPomodoro) {
                completedPomodorosInCycle++;
            }
            int interval = prefs.getLongBreakInterval();
            if (completedPomodorosInCycle >= interval) {
                currentState = TimerState.LONG_BREAK;
                completedPomodorosInCycle = 0; // reset cycle after long break is triggered
            } else {
                currentState = TimerState.SHORT_BREAK;
            }
            btnPrimaryAction.setText("[ START BREAK ]");
            tvStateSubtitle.setText("[ break time ]");
        } else {
            currentState = TimerState.FOCUS;
            btnPrimaryAction.setText("[ START FOCUS ]");
            tvStateSubtitle.setText("[ ready to focus ]");
        }

        initTimerForCurrentState();
        updateCycleDisplay();
        updateStateBanner();
    }

    private void updateTimerDisplay() {
        long totalSeconds = (remainingMillis + 999) / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        tvCountdown.setText(String.format("%02d:%02d", minutes, seconds));

        float progress = totalDurationMillis > 0 ? (float) remainingMillis / (float) totalDurationMillis : 0f;
        timerRing.setProgress(progress);

        updateStateBanner();
        updateCycleDisplay();
    }

    private void updateStateBanner() {
        if (currentState == TimerState.FOCUS) {
            tvStateBanner.setText("[ FOCUS SESSION ]");
        } else if (currentState == TimerState.SHORT_BREAK) {
            tvStateBanner.setText("[ SHORT BREAK ]");
        } else {
            tvStateBanner.setText("[ LONG BREAK ]");
        }
    }

    private void updateCycleDisplay() {
        int interval = prefs.getLongBreakInterval();
        StringBuilder sb = new StringBuilder();
        int activeSession = (completedPomodorosInCycle % interval) + 1;

        for (int i = 1; i <= interval; i++) {
            if (i <= completedPomodorosInCycle) {
                sb.append("● ");
            } else if (i == activeSession && currentState == TimerState.FOCUS) {
                sb.append("◐ ");
            } else {
                sb.append("○ ");
            }
        }
        sb.append("  (session ").append(activeSession).append(" of ").append(interval).append(")");
        tvCycleDots.setText(sb.toString().trim());
    }

    private void setKeepScreenOn(boolean keepOn) {
        if (keepOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void applyCurrentTheme() {
        ThemeManager.Palette p = new ThemeManager.Palette(prefs.isDarkTheme());

        rootView.setBackgroundColor(p.bg);
        headerBar.setBackgroundColor(p.headerBg);
        tvAppTitle.setTextColor(p.textPrimary);

        btnStats.setTextColor(p.textSecondary);
        btnTheme.setTextColor(p.textSecondary);
        btnTheme.setText(prefs.isDarkTheme() ? "[light]" : "[dark]");
        btnSettings.setTextColor(p.textSecondary);
        headerDivider.setBackgroundColor(p.divider);

        bannerLayout.setBackgroundColor(p.bg);
        tvStateBanner.setTextColor(p.textPrimary);
        tvCycleDots.setTextColor(p.textSecondary);

        timerRing.setColors(p.ringBg, p.ringProgress);
        tvCountdown.setTextColor(p.textPrimary);
        tvStateSubtitle.setTextColor(p.textSecondary);

        bottomControls.setBackgroundColor(p.bg);
        btnPrimaryAction.setBackgroundDrawable(ThemeManager.createBox(p.btnBg, p.btnBorder, 6));
        btnPrimaryAction.setTextColor(p.textPrimary);

        btnSkip.setBackgroundDrawable(ThemeManager.createBox(p.surface, p.divider, 4));
        btnSkip.setTextColor(p.textSecondary);

        btnReset.setBackgroundDrawable(ThemeManager.createBox(p.surface, p.divider, 4));
        btnReset.setTextColor(p.textSecondary);

        tvKeyboardHint.setTextColor(p.textSecondary);
    }

    // Physical Keyboard Shortcuts for BlackBerry 10
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            toggleTimer();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_S) {
            skipSession();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_R) {
            resetCurrentTimer();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_T) {
            prefs.setDarkTheme(!prefs.isDarkTheme());
            applyCurrentTheme();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timerRunnable);
        setKeepScreenOn(false);
    }
}
