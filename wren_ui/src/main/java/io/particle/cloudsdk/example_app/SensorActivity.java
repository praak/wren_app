
package io.particle.cloudsdk.example_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SensorActivity extends AppCompatActivity {

    private static final String TAG = "SensorActivity";

    ListView listView;
    SensorsAdapter mSensorsAdapter;
    ArrayList<Sensor> mSensors = new ArrayList<Sensor>();

    public static Intent buildIntent(Context ctx, Integer value, String deviceid) {
        Intent intent = new Intent(ctx, SensorActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        setTitle("Remote Sensors");
        listView = (ListView) findViewById(R.id.listview_sensors);

        Intent intent = getIntent();

        // This is assumed to be valid
        String jsonString = intent.getStringExtra("jsonString");

        JSONObject jObject = null;
        JSONArray jsonArray = null;
        try {
            jObject = new JSONObject(jsonString);
            jsonArray = jObject.getJSONArray("RSensors");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0, size = jsonArray.length(); i < size; i++) {
            JSONObject objectInArray = null;
            try {
                objectInArray = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String temp, id, battStatus;
            try {
                temp = objectInArray.getString("Temp");
                id = objectInArray.getString("RemoteId");
                battStatus = objectInArray.getString("BattStatus");
                mSensors.add(new Sensor(temp, id, battStatus));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        mSensorsAdapter = new SensorsAdapter(getApplicationContext(), mSensors,
                SensorActivity.this);
        listView.setAdapter(mSensorsAdapter);

    }
}

class Sensor {

    public String mTemp = null;
    public String mId = null;
    public boolean mBattStatus;

    public Sensor(String temp, String id, String battStatus) {
        mTemp = temp;
        mId = id;
        mBattStatus = (battStatus.equals("true") ? true : false);
    }
}
