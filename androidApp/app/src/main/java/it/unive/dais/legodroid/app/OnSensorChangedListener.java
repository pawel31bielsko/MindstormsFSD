package it.unive.dais.legodroid.app;

public interface OnSensorChangedListener {
    void onUltrasonicChanged(int distance);
    void onTouchSensorChanged(boolean isTouch);
}
