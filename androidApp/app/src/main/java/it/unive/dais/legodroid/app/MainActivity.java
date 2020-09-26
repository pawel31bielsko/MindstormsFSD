package it.unive.dais.legodroid.app;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import it.unive.dais.legodroid.lib.GenEV3;
import it.unive.dais.legodroid.lib.util.Prelude;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = Prelude.ReTAG("MainActivity");

    private final Map<String, Object> statusMap = new HashMap<>();
    private TeslaSteering teslaSteering;
    private ImageButton forwardButton;
    private ImageButton backwardButton;
    private Slider mainEnginePowerSlider;
    private Slider mainEngineSpeedSlider;
    private ImageButton leftButton;
    private ImageButton rightButton;
    private Slider turnEnginePowerSlider;
    private Slider turnEngineSpeedSlider;
    private TextView sensorView;

    private ImageButton turnSensorLeftButton;
    private ImageButton turnSensorRightButton;
    private Slider turnSensorSpeedSlider;
    private Slider turnSensorPowerSlider;
    private RadioButton gearOneRadio;
    private RadioButton gearTwoRadio;
    private RadioGroup gearRadioGroup;

    private Button syncGearButton;
    // quick wrapper for accessing the private field MainActivity.motor only when not-null; also ignores any exception thrown


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.sensorView = findViewById(R.id.sensorView);
        forwardButton = (ImageButton) findViewById(R.id.forward);
        backwardButton = (ImageButton) findViewById(R.id.backwardButton);
        mainEnginePowerSlider = (Slider) findViewById(R.id.drivePowerSlider);
        mainEngineSpeedSlider = (Slider) findViewById(R.id.driveSpeedSlider);

        leftButton = (ImageButton) findViewById(R.id.leftButton);
        rightButton = (ImageButton) findViewById(R.id.rightButton);
        turnEngineSpeedSlider = (Slider) findViewById(R.id.turnSpeedSlider);
        turnEnginePowerSlider = (Slider) findViewById(R.id.turnPowerSlider);

        turnSensorLeftButton = (ImageButton) findViewById(R.id.turnSensorLeftButton);
        turnSensorRightButton = (ImageButton) findViewById(R.id.turnSensorRightButton);
        turnSensorPowerSlider = (Slider) findViewById(R.id.turnSensorPowerSlider);
        turnSensorSpeedSlider = (Slider) findViewById(R.id.turnSensorSpeedSlider);
        syncGearButton = (Button) findViewById(R.id.syncGearButton);
        gearOneRadio = (RadioButton)  findViewById(R.id.radioGearOne);
        gearTwoRadio = (RadioButton) findViewById(R.id.radioGearTwo);
        gearRadioGroup = (RadioGroup) findViewById(R.id.gearRadioGroup);

        try {
            this.teslaSteering = new TeslaSteering("EV3", new OnSensorChangedListener() {
                @Override
                public void onUltrasonicChanged(int distance) {
                    Log.d(TAG, String.format("New distance %d", distance));
                    statusMap.put("distance", distance);
                    printSensors();
                }

                @Override
                public void onTouchSensorChanged(boolean isPressed) {
                    Log.d(TAG, String.format("Is pressed changed: %b", isPressed));
                    statusMap.put("isPressed", isPressed);
                    printSensors();
                }
            }, r -> this.runOnUiThread(r));
        } catch (IOException | GenEV3.AlreadyRunningException e) {
            Log.e(TAG, "fatal error: cannot connect to EV3");
            e.printStackTrace();
        }

        forwardButton.setOnTouchListener((v, e) -> drive(e, 1));
        backwardButton.setOnTouchListener((v, e) -> drive(e, -1));
        leftButton.setOnTouchListener((v, e) -> turn(e, -1));
        rightButton.setOnTouchListener((v, e) -> turn(e, 1));
        turnSensorRightButton.setOnTouchListener((v, e) -> turnSensors(e, 1));
        turnSensorLeftButton.setOnTouchListener((v, e) -> turnSensors(e, -1));
        syncGearButton.setOnClickListener((v) -> teslaSteering.testGear());
        gearRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int gear = checkedId == R.id.radioGearOne ?1:2;
            this.teslaSteering.changeGear(gear);
        });


//        Joystick joystick = (Joystick) findViewById(R.id.joystick);
//        joystick.setJoystickListener(new JoystickListener() {
//            @Override
//            public void onDown() {
//
//            }
//
//            @Override
//            public void onDrag(float degrees, float offset) {
//
//                double angleRad = degrees * Math.PI / 180.0;
//                double rotation = Math.sin(angleRad) * offset;
//                double direction = Math.cos(angleRad) * offset;
//                Log.d(TAG, String.format("Input degrees: %f, offset: %f transformed to: rot: %f direction: %f", degrees, offset, rotation, direction));
//                teslaStearing.drive(rotation, direction);
//            }
//
//            @Override
//            public void onUp() {
//                teslaStearing.stopDriving();
//            }
//        });
    }

    private void printSensors() {
        try {
            sensorView.setText(new JSONObject(statusMap).toString(3));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean drive(MotionEvent e, int direction) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            this.teslaSteering.drive((int) mainEngineSpeedSlider.getValue(), (int) mainEnginePowerSlider.getValue(), direction);
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            this.teslaSteering.stopDriving();
        }
        return true;
    }

    private boolean turn(MotionEvent e, int direction) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            this.teslaSteering.turn((int) turnEngineSpeedSlider.getValue(), (int) turnEnginePowerSlider.getValue(), direction);
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            this.teslaSteering.stopTurning();
        }
        return true;
    }

    private boolean turnSensors(MotionEvent e, int direction) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            this.teslaSteering.turnSensors((int) turnSensorSpeedSlider.getValue(), (int) turnSensorPowerSlider.getValue(), direction);
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            this.teslaSteering.stopTurningSensors();
        }
        return true;
    }
}


