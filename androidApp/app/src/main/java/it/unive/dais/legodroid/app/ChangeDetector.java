package it.unive.dais.legodroid.app;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.plugs.TouchSensor;
import it.unive.dais.legodroid.lib.plugs.UltrasonicSensor;
import it.unive.dais.legodroid.lib.util.Consumer;

public class ChangeDetector {
    private EV3.Api api;
    private OnSensorChangedListener onSensorChangedListener;
    private Consumer<Runnable> runOnUIThread;


    private TouchSensor touchSensor;
    private UltrasonicSensor ultrasonicSensor;

    private Boolean isPressed;
    private Integer distance;


    public ChangeDetector(EV3.Api api, EV3.InputPort touchSensorInputPort, EV3.InputPort ultrasonicSensorInputPort){
        this.api = api;
        this.isPressed = null;
        this.distance = null;
        this.touchSensor = api.getTouchSensor(touchSensorInputPort);
        this.ultrasonicSensor = api.getUltrasonicSensor(ultrasonicSensorInputPort);
    }

    public void setOnSensorChangedListener(OnSensorChangedListener listener, Consumer<Runnable> runOnUIThread){
        this.onSensorChangedListener = listener;
        this.runOnUIThread = runOnUIThread;
    }

    public void detectChanges() throws IOException, ExecutionException, InterruptedException {
        Integer distance = Math.round(ultrasonicSensor.getDistance().get());
        if(distance >= 0 && distance <= 100 && (this.distance == null || this.distance != distance)){
            this.distance = distance;
            if(this.onSensorChangedListener != null && this.runOnUIThread != null){
                this.runOnUIThread.call(() -> this.onSensorChangedListener.onUltrasonicChanged(this.distance));
            }
        }

        Future<Boolean> isPressed = touchSensor.getPressed();

        if(isPressed.get() != null && (this.isPressed == null || this.isPressed != isPressed.get())){
            this.isPressed = isPressed.get();
            if(this.onSensorChangedListener != null && this.runOnUIThread != null){
                this.runOnUIThread.call(() -> this.onSensorChangedListener.onTouchSensorChanged(this.isPressed));
            }
        }
    }


}
