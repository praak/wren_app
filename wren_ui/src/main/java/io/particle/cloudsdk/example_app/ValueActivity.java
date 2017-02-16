
package io.particle.cloudsdk.example_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;
import io.particle.android.sdk.utils.Async;

public class ValueActivity extends AppCompatActivity {

    private static final String TAG = "ValueActivity";

    private static DevicesAdapter mDevicesAdapter;
    ArrayList<ParticleDevice> mDevices;
    ListView listView;
    ImageButton buttonAddDevice;

    public static Intent buildIntent(Context ctx, Integer value, String deviceid) {
        Intent intent = new Intent(ctx, ValueActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value);

        setTitle("Thermostats");

        listView = (ListView) findViewById(R.id.listview_devices);
        buttonAddDevice = (ImageButton) findViewById(R.id.button_add_device);

        ParticleDeviceSetupLibrary.init(this.getApplicationContext(), ValueActivity.class);

        buttonAddDevice.setOnClickListener(view -> {
            ParticleDeviceSetupLibrary.startDeviceSetup(this, ValueActivity.class);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDeviceListView();
    }

    private void updateDeviceListView() {
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
                        mDevicesAdapter = new DevicesAdapter(getApplicationContext(), mDevices);
                        listView.setAdapter(mDevicesAdapter);
                    }

                    @Override
                    public void onFailure(@NonNull ParticleCloudException e) {
                        e.printStackTrace();
                    }
                });
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_value_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        AlertDialog alertDialog;
        switch (item.getItemId()) {
            case R.id.action_logout:
                // set title
                alertDialogBuilder.setTitle("Are you sure?");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Logout", (dialog, id) -> {
                            ParticleCloudSDK.getCloud().logOut();
                            Intent intent = new Intent(ValueActivity.this,
                                    LoginActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

                // create alert dialog
                alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // Todo: template setup for AsyncTask for potentially talking with ThinkSpeak. (REST API SETUP)
    private class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(), "ExecutedFlowersXML", Toast.LENGTH_LONG).show();
        }
    }

}
