package com.focustimer.app;

public enum TimerState {
    FOCUS("Focus Session"),
    SHORT_BREAK("Short Break"),
    LONG_BREAK("Long Break");

    private final String displayName;

    TimerState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
