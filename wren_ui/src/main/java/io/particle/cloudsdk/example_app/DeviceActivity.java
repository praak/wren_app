
package io.particle.cloudsdk.example_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import io.particle.android.sdk.cloud.ParticleDevice;

/**
 * Created by charlesscholle on 2/11/17.
 */

public class DeviceActivity extends AppCompatActivity {

    TextView temperature, devicename, currtemp;
    ImageButton remotesensors, mode, setschedule;
    NumberPicker numberPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        temperature = (TextView) findViewById(R.id.textview_temperature);
        devicename = (TextView) findViewById(R.id.textview_devicename);
        currtemp = (TextView) findViewById(R.id.textview_curr_temp);

        remotesensors = (ImageButton) findViewById(R.id.imagebutton_remotesensors);
        mode = (ImageButton) findViewById(R.id.imagebutton_mode);
        setschedule = (ImageButton) findViewById(R.id.imagebutton_setschedule);

        numberPicker = (NumberPicker) findViewById(R.id.numberpicker_temperature);
        numberPicker.setMinValue(40);
        numberPicker.setMaxValue(110);
        numberPicker.setValue(72);
        numberPicker.setWrapSelectorWheel(false);

        Bundle bundle = getIntent().getExtras();

        ParticleDevice device = bundle.getParcelable("Device");

        devicename.setText("Text" + device.getName());
        devicename.setAlpha(0.0f);
        temperature.setAlpha(0.0f);

        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            currtemp.setText(String.valueOf(newVal));
        });

    }

}
