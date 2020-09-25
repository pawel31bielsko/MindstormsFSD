package it.unive.dais.legodroid.app;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.GenEV3;
import it.unive.dais.legodroid.lib.comm.BluetoothConnection;
import it.unive.dais.legodroid.lib.plugs.TachoMotor;
import it.unive.dais.legodroid.lib.plugs.TouchSensor;
import it.unive.dais.legodroid.lib.plugs.UltrasonicSensor;
import it.unive.dais.legodroid.lib.util.Consumer;
import it.unive.dais.legodroid.lib.util.Prelude;
import it.unive.dais.legodroid.lib.util.ThrowingConsumer;

public class TeslaSteering {

    private BluetoothConnection bluetoothConnection;
    private EV3 ev3;
    private TachoMotor mainMotor;
    private TachoMotor turningMotor;
    private TachoMotor sensorRotationMotor;
    private TachoMotor gearMotor;
    private ChangeDetector changeDetector;


    public TeslaSteering(String connectionName, OnSensorChangedListener listener, Consumer<Runnable> runOnUIThread) throws IOException, GenEV3.AlreadyRunningException {
        this.bluetoothConnection = new BluetoothConnection(connectionName);
        this.ev3 = new EV3(this.bluetoothConnection.connect());    // replace with your own brick name
        this.ev3.run(api -> {
            this.mainMotor = api.getTachoMotor(EV3.OutputPort.A);
            this.turningMotor = api.getTachoMotor(EV3.OutputPort.D);
            applyMotor(turningMotor, m -> m.setType(TachoMotor.Type.MEDIUM));
            this.sensorRotationMotor = api.getTachoMotor(EV3.OutputPort.C);
            applyMotor(sensorRotationMotor, m -> m.setType(TachoMotor.Type.MEDIUM));
            this.gearMotor = api.getTachoMotor(EV3.OutputPort.B);
            this.changeDetector = new ChangeDetector(api,EV3.InputPort._1, EV3.InputPort._4);
            this.changeDetector.setOnSensorChangedListener(listener, runOnUIThread);
            mainLoop(api);
        });
    }



    public void drive(double direction, double rotation) {
        applyMotor(this.mainMotor, m -> {
            if (direction != 0) {
                m.setSpeed((int) Math.ceil(100 * Math.abs(direction)));
                m.setPolarity(direction > 0 ? TachoMotor.Polarity.FORWARD : TachoMotor.Polarity.BACKWARDS);
                m.start();
            } else {
                m.setSpeed(0);
                m.stop();
            }
        });

        applyMotor(this.turningMotor, m -> {
            if (rotation != 0) {
                m.setSpeed(50);
                m.setPower(50);
                m.setPolarity(rotation > 0 ? TachoMotor.Polarity.FORWARD : TachoMotor.Polarity.BACKWARDS);
                m.start();
            } else {
                m.stop();
            }
        });
    }

    public void drive(int speed, int power, int direction){
        if(speed > 0 && power > 0){
            applyMotor(mainMotor, m ->{
                m.setSpeed(speed);
                m.setPower(power);
                m.setPolarity(direction < 0 ? TachoMotor.Polarity.FORWARD : TachoMotor.Polarity.BACKWARDS);
                m.start();
            });
        }
    }

    public void stopDriving() {
        applyMotor(mainMotor, m -> {
            m.setSpeed(0);
            m.stop();
        });

    }

    private void applyMotor(TachoMotor motor, ThrowingConsumer<TachoMotor, Throwable> f) {
        if (motor != null)
            Prelude.trap(() -> f.call(motor));
    }

    public void stopTurning() {
        applyMotor(turningMotor, m-> {
            m.setSpeed(0);
            m.stop();
        });
    }

    public void turn(int speed, int power, int direction) {
        if(speed > 0 && power > 0){
            applyMotor(turningMotor, m ->{
                m.setSpeed(speed);
                m.setPower(power);
                m.setPolarity(direction < 0 ? TachoMotor.Polarity.FORWARD : TachoMotor.Polarity.BACKWARDS);
                m.start();
            });
        }
    }

    public void turnSensors(int speed, int power, int direction) {
        if(speed > 0 && power > 0){
            applyMotor(this.sensorRotationMotor, m ->{
                m.setSpeed(speed);
                m.setPower(power);
                m.setPolarity(direction < 0 ? TachoMotor.Polarity.FORWARD : TachoMotor.Polarity.BACKWARDS);
                m.start();
            });
        }
    }

    public void stopTurningSensors(){
        applyMotor(this.sensorRotationMotor, m-> {
            m.setSpeed(0);
            m.stop();
        });
    }

    private  void mainLoop(EV3.Api api){
        while (!api.ev3.isCancelled()) {
            try {
                this.changeDetector.detectChanges();
            } catch (IOException |ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
