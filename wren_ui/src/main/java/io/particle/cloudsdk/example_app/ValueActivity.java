
package io.particle.cloudsdk.example_app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;

public class ValueActivity extends AppCompatActivity {

    private static final String TAG = "ValueActivity";
    private static final String ARG_VALUE = "ARG_VALUE";
    private static final String ARG_DEVICEID = "ARG_DEVICEID";
    private static DevicesAdapter mDevicesAdapter;
    ArrayList<ParticleDevice> mDevices;
    ListView listView;
    ArrayAdapter<String> adapter;
    String[] test_strings = {
            "test_1",
            "test_2",
            "test_3"
    };

    private TextView tv;

    public static Intent buildIntent(Context ctx, Integer value, String deviceid) {
        Intent intent = new Intent(ctx, ValueActivity.class);
        intent.putExtra(ARG_VALUE, value);
        intent.putExtra(ARG_DEVICEID, deviceid);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value);

        listView = (ListView) findViewById(R.id.listview_devices);

        tv = (TextView) findViewById(R.id.value);
        tv.setText(String.valueOf(getIntent().getIntExtra(ARG_VALUE, 0)));

        // ...
        // Do network work on background thread
        Async.executeAsync(ParticleCloudSDK.getCloud(),
                new Async.ApiWork<ParticleCloud, List>() {
                    @Override
                    public List callApi(@NonNull ParticleCloud ParticleCloud)
                            throws ParticleCloudException, IOException {
                        return ParticleCloud.getDevices();
                    }

                    @Override
                    public void onSuccess(@NonNull List devices) { // this goes on the main thread
                        // get names, post on listview
                        mDevices = (ArrayList<ParticleDevice>) devices;
                        ArrayList<String> names = new ArrayList<String>();
                        for (ParticleDevice device : (List<ParticleDevice>) devices) {
                            names.add(device.getName());
                        }

                        updateAdapter(names);
                        // tv.setText(i.get(0).toString());
                    }

                    @Override
                    public void onFailure(@NonNull ParticleCloudException e) {
                        e.printStackTrace();
                    }
                });

        // Syntax replace from previous to get rid of lambda warning.
        // http://stackoverflow.com/questions/30752547/listener-can-be-replaced-with-lambda
        findViewById(R.id.refresh_button).setOnClickListener((View view) -> {

            if (isOnline()) {
                requestData("http://services.hanselandpetal.com/feeds/flowers.xml");
            } else {
                Toast.makeText(getBaseContext(),
                        "Couldn't send request, check internet connection?", Toast.LENGTH_LONG)
                        .show();
            }
        });

    }

    private void updateAdapter(ArrayList<String> names) {
        mDevicesAdapter = new DevicesAdapter(getApplicationContext(), mDevices);
        listView.setAdapter(mDevicesAdapter);

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            Log.d(TAG, "Item Clicked");

            Toast.makeText(ValueActivity.this, "clicked: " + position, Toast.LENGTH_LONG)
                    .show();

        });

        // listView.setOnItemClickListener(
        // (parent, view, position, id) -> Toast.makeText(getBaseContext(),
        // parent.getItemAtPosition(position) + " is selected", Toast.LENGTH_LONG)
        // .show());

        // listView.setOnClickListener(new AdapterView.OnItemClickListener() {
        // @Override
        // public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //
        // ParticleDevice dataModel = (ParticleDevice) mDevices.get(position);
        //
        // }
        // });
    }

    private void requestData(String uri) {
        Toast.makeText(this, "Uri:" + uri, Toast.LENGTH_LONG).show();
        MyTask myTask = new MyTask();
        myTask.execute(uri);
    }

    protected boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

    private class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            tv.setText("Before");
        }

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                tv.setText("My string" + s.substring(0, 10));
            } else {
                tv.setText("Null");
            }
            Toast.makeText(getBaseContext(), "ExecutedFlowersXML", Toast.LENGTH_LONG).show();
        }
    }

}
