
package io.particle.cloudsdk.example_app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import io.backgroundtask.wren.HttpManager;
import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;

public class ValueActivity extends AppCompatActivity {

    private static final String ARG_VALUE = "ARG_VALUE";
    private static final String ARG_DEVICEID = "ARG_DEVICEID";
    private static final String TAG = "ValueActivity";
    ListView listView;
    ArrayAdapter<String> adapter;
    List devices;
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

        listView = (ListView) findViewById(R.id.list_view);

        tv = (TextView) findViewById(R.id.value);
        tv.setText(String.valueOf(getIntent().getIntExtra(ARG_VALUE, 0)));

        findViewById(R.id.refresh_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ...
                // Do network work on background thread
                Async.executeAsync(ParticleCloud.get(ValueActivity.this),
                        new Async.ApiWork<ParticleCloud, List>() {
                            @Override
                            public List callApi(ParticleCloud ParticleCloud)
                                    throws ParticleCloudException, IOException {
                                List<ParticleDevice> devices = ParticleCloud.getDevices();
                                if (devices.size() > 0) {
                                    ParticleDevice myDevice = ParticleCloudSDK.getCloud()
                                            .getDevice(devices.get(0).getID());

                                    // OkHttpClient cliecnt = new OkHttpClient();
                                    //
                                    // String run(String url) throws IOException {
                                    // Request request = new Request.Builder()
                                    // .url("https://api.particle.io/v1/de")
                                    // .build();
                                    // }
                                    //
                                    // String response = run("");

                                    Log.d(TAG, "myDevice: " + myDevice.toString());
                                } else {
                                    Log.d(TAG, "Did not get anything");
                                }

                                return devices;
                            }

                            @Override
                            public void onSuccess(List i) { // this goes on the main thread
                                tv.setText(i.get(0).toString());
                            }

                            @Override
                            public void onFailure(ParticleCloudException e) {
                                e.printStackTrace();
                            }
                        });

                if (isOnline()) {
                    requestData("http://services.hanselandpetal.com/feeds/flowers.xml");
                } else {
                    // toast it
                }

            }

        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, test_strings);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(),
                        parent.getItemAtPosition(position) + " is selected", Toast.LENGTH_LONG)
                        .show();
            }
        });

        String url = "http://my-json-feed";

    }

    private void requestData(String uri) {
        MyTask myTask = new MyTask();
        myTask.execute(uri);
    }

    protected boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }

    }

    private class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            tv.setText("Before");
        }

        @Override
        protected String doInBackground(String... params) {

            String content = HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String s) {
            tv.setText(s);
            Toast.makeText(getBaseContext(), "ExecutedFlowersXML", Toast.LENGTH_LONG).show();

        }
    }

}
