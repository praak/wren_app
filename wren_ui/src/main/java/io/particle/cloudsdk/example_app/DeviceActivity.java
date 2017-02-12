
package io.particle.cloudsdk.example_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.NumberPicker;
import android.widget.TextView;

import io.particle.android.sdk.cloud.ParticleDevice;

/**
 * Created by charlesscholle on 2/11/17.
 */

public class DeviceActivity extends AppCompatActivity {

    TextView textView;

    NumberPicker numberPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        textView = (TextView) findViewById(R.id.textview_devicename);

        Bundle bundle = getIntent().getExtras();

        ParticleDevice device = bundle.getParcelable("Device");

        textView.setText("Text" + device.getName());

    }
}
