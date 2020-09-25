package it.unive.dais.legodroid.app;

public interface OnSensorChangedListener {
    void onUltrasonicChanged(float distance);
    void onTouchSensorChanged(boolean isTouch);
}
