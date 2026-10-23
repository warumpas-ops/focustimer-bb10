package com.focustimer.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class StatsActivity extends Activity {

    private PreferencesManager prefs;
    private View rootView;
    private LinearLayout headerBar;
    private Button btnBack;
    private TextView tvHeaderTitle;
    private View headerDivider;
    private ScrollView rootScrollView;
    private LinearLayout cardToday;
    private TextView tvTodayLabel;
    private TextView tvTodayPomodoros;
    private TextView tvTodaySubtitle;
    private View div1;
    private LinearLayout cardTotal;
    private TextView tvTotalLabel;
    private TextView tvTotalPomodoros;
    private TextView tvTotalSubtitle;
    private View div2;
    private TextView tvInfoTitle;
    private TextView tvInfoBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        prefs = new PreferencesManager(this);

        rootView = findViewById(R.id.rootView);
        headerBar = (LinearLayout) findViewById(R.id.headerBar);
        btnBack = (Button) findViewById(R.id.btnBack);
        tvHeaderTitle = (TextView) findViewById(R.id.tvHeaderTitle);
        headerDivider = findViewById(R.id.headerDivider);
        rootScrollView = (ScrollView) findViewById(R.id.rootScrollView);

        cardToday = (LinearLayout) findViewById(R.id.cardToday);
        tvTodayLabel = (TextView) findViewById(R.id.tvTodayLabel);
        tvTodayPomodoros = (TextView) findViewById(R.id.tvTodayPomodoros);
        tvTodaySubtitle = (TextView) findViewById(R.id.tvTodaySubtitle);
        div1 = findViewById(R.id.div1);

        cardTotal = (LinearLayout) findViewById(R.id.cardTotal);
        tvTotalLabel = (TextView) findViewById(R.id.tvTotalLabel);
        tvTotalPomodoros = (TextView) findViewById(R.id.tvTotalPomodoros);
        tvTotalSubtitle = (TextView) findViewById(R.id.tvTotalSubtitle);
        div2 = findViewById(R.id.div2);

        tvInfoTitle = (TextView) findViewById(R.id.tvInfoTitle);
        tvInfoBody = (TextView) findViewById(R.id.tvInfoBody);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadStats();
        applyTheme();
        FontHelper.apply(rootView, this);
    }

    private void loadStats() {
        int todayCount = prefs.getTodayCount();
        int todaySeconds = prefs.getTodaySeconds();
        int totalCount = prefs.getTotalCount();
        int totalSeconds = prefs.getTotalSeconds();

        tvTodayPomodoros.setText(todayCount + " pomodoro" + (todayCount == 1 ? "" : "s"));

        String todayFormatted;
        if (todaySeconds < 60) {
            todayFormatted = todaySeconds + " second" + (todaySeconds == 1 ? "" : "s");
        } else {
            int m = todaySeconds / 60;
            int s = todaySeconds % 60;
            if (s > 0) {
                todayFormatted = m + "m " + s + "s";
            } else {
                todayFormatted = m + " minute" + (m == 1 ? "" : "s");
            }
        }
        tvTodaySubtitle.setText(todayFormatted + " of focused work today");

        double totalHours = Math.round((totalSeconds / 3600.0) * 10.0) / 10.0;
        tvTotalPomodoros.setText(totalCount + " total sessions");
        tvTotalSubtitle.setText(totalHours + " total hours focused");
    }

    private void applyTheme() {
        ThemeManager.Palette p = new ThemeManager.Palette(prefs.isDarkTheme());

        rootView.setBackgroundColor(p.bg);
        headerBar.setBackgroundColor(p.headerBg);
        tvHeaderTitle.setTextColor(p.textPrimary);
        btnBack.setTextColor(p.textSecondary);
        headerDivider.setBackgroundColor(p.divider);

        cardToday.setBackgroundDrawable(ThemeManager.createBox(p.surface, p.divider, 6));
        tvTodayLabel.setTextColor(p.textSecondary);
        tvTodayPomodoros.setTextColor(p.textPrimary);
        tvTodaySubtitle.setTextColor(p.textSecondary);
        div1.setBackgroundColor(p.divider);

        cardTotal.setBackgroundDrawable(ThemeManager.createBox(p.surface, p.divider, 6));
        tvTotalLabel.setTextColor(p.textSecondary);
        tvTotalPomodoros.setTextColor(p.textPrimary);
        tvTotalSubtitle.setTextColor(p.textSecondary);
        div2.setBackgroundColor(p.divider);

        tvInfoTitle.setTextColor(p.textSecondary);
        tvInfoBody.setTextColor(p.textSecondary);
    }
}
