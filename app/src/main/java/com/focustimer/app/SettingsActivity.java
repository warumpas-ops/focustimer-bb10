package com.focustimer.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    private PreferencesManager prefs;

    private View rootView;
    private LinearLayout headerBar;
    private Button btnBack;
    private TextView tvHeaderTitle;
    private Button btnSave;
    private View headerDivider;
    private ScrollView rootScrollView;

    private TextView tvThemeHeader;
    private LinearLayout rowDarkTheme;
    private TextView tvThemeLabel;
    private CheckBox cbDarkMode;
    private View div1;

    private TextView tvDurationsHeader;
    private TextView tvFocusLabel;
    private Button btnFocusMinus;
    private TextView tvFocusValue;
    private Button btnFocusPlus;

    private TextView tvShortBreakLabel;
    private Button btnShortMinus;
    private TextView tvShortValue;
    private Button btnShortPlus;

    private TextView tvLongBreakLabel;
    private Button btnLongMinus;
    private TextView tvLongValue;
    private Button btnLongPlus;

    private TextView tvIntervalLabel;
    private Button btnIntervalMinus;
    private TextView tvIntervalValue;
    private Button btnIntervalPlus;
    private View div2;

    private TextView tvAlertsHeader;
    private TextView tvSoundLabel;
    private CheckBox cbSound;
    private TextView tvVibrateLabel;
    private CheckBox cbVibrate;
    private TextView tvAutoBreakLabel;
    private CheckBox cbAutoBreak;

    private int focusMinutes;
    private int shortMinutes;
    private int longMinutes;
    private int intervalSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = new PreferencesManager(this);

        rootView = findViewById(R.id.rootView);
        headerBar = (LinearLayout) findViewById(R.id.headerBar);
        btnBack = (Button) findViewById(R.id.btnBack);
        tvHeaderTitle = (TextView) findViewById(R.id.tvHeaderTitle);
        btnSave = (Button) findViewById(R.id.btnSave);
        headerDivider = findViewById(R.id.headerDivider);
        rootScrollView = (ScrollView) findViewById(R.id.rootScrollView);

        tvThemeHeader = (TextView) findViewById(R.id.tvThemeHeader);
        rowDarkTheme = (LinearLayout) findViewById(R.id.rowDarkTheme);
        tvThemeLabel = (TextView) findViewById(R.id.tvThemeLabel);
        cbDarkMode = (CheckBox) findViewById(R.id.cbDarkMode);
        div1 = findViewById(R.id.div1);

        tvDurationsHeader = (TextView) findViewById(R.id.tvDurationsHeader);
        tvFocusLabel = (TextView) findViewById(R.id.tvFocusLabel);
        btnFocusMinus = (Button) findViewById(R.id.btnFocusMinus);
        tvFocusValue = (TextView) findViewById(R.id.tvFocusValue);
        btnFocusPlus = (Button) findViewById(R.id.btnFocusPlus);

        tvShortBreakLabel = (TextView) findViewById(R.id.tvShortBreakLabel);
        btnShortMinus = (Button) findViewById(R.id.btnShortMinus);
        tvShortValue = (TextView) findViewById(R.id.tvShortValue);
        btnShortPlus = (Button) findViewById(R.id.btnShortPlus);

        tvLongBreakLabel = (TextView) findViewById(R.id.tvLongBreakLabel);
        btnLongMinus = (Button) findViewById(R.id.btnLongMinus);
        tvLongValue = (TextView) findViewById(R.id.tvLongValue);
        btnLongPlus = (Button) findViewById(R.id.btnLongPlus);

        tvIntervalLabel = (TextView) findViewById(R.id.tvIntervalLabel);
        btnIntervalMinus = (Button) findViewById(R.id.btnIntervalMinus);
        tvIntervalValue = (TextView) findViewById(R.id.tvIntervalValue);
        btnIntervalPlus = (Button) findViewById(R.id.btnIntervalPlus);
        div2 = findViewById(R.id.div2);

        tvAlertsHeader = (TextView) findViewById(R.id.tvAlertsHeader);
        tvSoundLabel = (TextView) findViewById(R.id.tvSoundLabel);
        cbSound = (CheckBox) findViewById(R.id.cbSound);
        tvVibrateLabel = (TextView) findViewById(R.id.tvVibrateLabel);
        cbVibrate = (CheckBox) findViewById(R.id.cbVibrate);
        tvAutoBreakLabel = (TextView) findViewById(R.id.tvAutoBreakLabel);
        cbAutoBreak = (CheckBox) findViewById(R.id.cbAutoBreak);

        // Load current values
        focusMinutes = prefs.getFocusMinutes();
        shortMinutes = prefs.getShortBreakMinutes();
        longMinutes = prefs.getLongBreakMinutes();
        intervalSets = prefs.getLongBreakInterval();

        cbDarkMode.setChecked(prefs.isDarkTheme());
        cbSound.setChecked(prefs.isSoundEnabled());
        cbVibrate.setChecked(prefs.isVibrateEnabled());
        cbAutoBreak.setChecked(prefs.isAutoStartBreaks());

        updateValueDisplays();

        // Plus / Minus Listeners
        btnFocusMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (focusMinutes > 1) { focusMinutes--; updateValueDisplays(); }
            }
        });
        btnFocusPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (focusMinutes < 90) { focusMinutes++; updateValueDisplays(); }
            }
        });

        btnShortMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shortMinutes > 1) { shortMinutes--; updateValueDisplays(); }
            }
        });
        btnShortPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shortMinutes < 30) { shortMinutes++; updateValueDisplays(); }
            }
        });

        btnLongMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longMinutes > 1) { longMinutes--; updateValueDisplays(); }
            }
        });
        btnLongPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longMinutes < 60) { longMinutes++; updateValueDisplays(); }
            }
        });

        btnIntervalMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intervalSets > 2) { intervalSets--; updateValueDisplays(); }
            }
        });
        btnIntervalPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intervalSets < 12) { intervalSets++; updateValueDisplays(); }
            }
        });

        cbDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.setDarkTheme(isChecked);
                applyTheme();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        applyTheme();
        FontHelper.apply(rootView, this);
    }

    private void updateValueDisplays() {
        tvFocusValue.setText(focusMinutes + " min");
        tvShortValue.setText(shortMinutes + " min");
        tvLongValue.setText(longMinutes + " min");
        tvIntervalValue.setText(intervalSets + " sets");
    }

    private void save() {
        prefs.setDarkTheme(cbDarkMode.isChecked());
        prefs.setFocusMinutes(focusMinutes);
        prefs.setShortBreakMinutes(shortMinutes);
        prefs.setLongBreakMinutes(longMinutes);
        prefs.setLongBreakInterval(intervalSets);
        prefs.setSoundEnabled(cbSound.isChecked());
        prefs.setVibrateEnabled(cbVibrate.isChecked());
        prefs.setAutoStartBreaks(cbAutoBreak.isChecked());
    }

    private void applyTheme() {
        ThemeManager.Palette p = new ThemeManager.Palette(prefs.isDarkTheme());

        rootView.setBackgroundColor(p.bg);
        headerBar.setBackgroundColor(p.headerBg);
        tvHeaderTitle.setTextColor(p.textPrimary);
        btnBack.setTextColor(p.textSecondary);
        btnSave.setTextColor(p.textPrimary);
        headerDivider.setBackgroundColor(p.divider);

        tvThemeHeader.setTextColor(p.textSecondary);
        tvThemeLabel.setTextColor(p.textPrimary);
        div1.setBackgroundColor(p.divider);

        tvDurationsHeader.setTextColor(p.textSecondary);
        tvFocusLabel.setTextColor(p.textPrimary);
        tvFocusValue.setTextColor(p.textPrimary);

        tvShortBreakLabel.setTextColor(p.textPrimary);
        tvShortValue.setTextColor(p.textPrimary);

        tvLongBreakLabel.setTextColor(p.textPrimary);
        tvLongValue.setTextColor(p.textPrimary);

        tvIntervalLabel.setTextColor(p.textPrimary);
        tvIntervalValue.setTextColor(p.textPrimary);

        div2.setBackgroundColor(p.divider);
        tvAlertsHeader.setTextColor(p.textSecondary);
        tvSoundLabel.setTextColor(p.textPrimary);
        tvVibrateLabel.setTextColor(p.textPrimary);
        tvAutoBreakLabel.setTextColor(p.textPrimary);

        // Style plus/minus buttons
        Button[] btns = {btnFocusMinus, btnFocusPlus, btnShortMinus, btnShortPlus,
                         btnLongMinus, btnLongPlus, btnIntervalMinus, btnIntervalPlus};
        for (Button b : btns) {
            b.setBackgroundDrawable(ThemeManager.createBox(p.btnBg, p.btnBorder, 4));
            b.setTextColor(p.textPrimary);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }
}
