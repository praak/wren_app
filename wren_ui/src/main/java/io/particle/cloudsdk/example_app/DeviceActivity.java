
package io.particle.cloudsdk.example_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.io.IOException;

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

/**
 * Created by charlesscholle on 2/11/17.
 */

public class DeviceActivity extends AppCompatActivity {

    TextView textView;

    ParticleDevice mDevice;

    NumberPicker numberPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        textView = (TextView) findViewById(R.id.textview_devicename);

        Bundle bundle = getIntent().getExtras();

        mDevice = bundle.getParcelable("Device");

        setTitle(mDevice.getName());

        // textView.setText("Text" + device.getName());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_device_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        AlertDialog alertDialog;
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.action_rename:
                final EditText input = new EditText(this);
                input.setHint("Enter new name");

                // set title
                alertDialogBuilder.setTitle("Rename Thermostat");

                // set dialog message
                alertDialogBuilder
                        .setView(input)
                        .setCancelable(true)
                        .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String newName = input.getText().toString().trim();
                                Async.executeAsync(mDevice,
                                        new Async.ApiWork<ParticleDevice, Integer>() {

                                            public Integer callApi(ParticleDevice particleDevice)
                                                    throws ParticleCloudException, IOException {
                                                mDevice.setName(newName);
                                                return 1;
                                            }

                                            @Override
                                            public void onSuccess(Integer value) {
                                                Toaster.s(DeviceActivity.this,
                                                        "New name " + newName + " set");
                                                setTitle(newName);
                                            }

                                            @Override
                                            public void onFailure(ParticleCloudException e) {
                                                Log.e("some tag",
                                                        "Something went wrong making an SDK call: ",
                                                        e);
                                                Toaster.l(DeviceActivity.this,
                                                        "Uh oh, something went wrong.");
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                break;
            case R.id.action_remove:
                // set title
                alertDialogBuilder.setTitle("Remove " + mDevice.getName() + "?");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Async.executeAsync(mDevice,
                                        new Async.ApiWork<ParticleDevice, Integer>() {

                                            public Integer callApi(ParticleDevice particleDevice)
                                                    throws ParticleCloudException, IOException {
                                                mDevice.unclaim();
                                                return 1;
                                            }

                                            @Override
                                            public void onSuccess(Integer value) {
                                                Toaster.s(DeviceActivity.this,
                                                        "Device Removed!");
                                                Intent intent = new Intent(DeviceActivity.this,
                                                        ValueActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onFailure(ParticleCloudException e) {
                                                Log.e("some tag",
                                                        "Something went wrong making an SDK call: ",
                                                        e);
                                                Toaster.l(DeviceActivity.this,
                                                        "Uh oh, something went wrong.");
                                            }
                                        });

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                break;
            case R.id.action_logout:
                // set title
                alertDialogBuilder.setTitle("Are you sure?");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ParticleCloudSDK.getCloud().logOut();
                                Intent intent = new Intent(DeviceActivity.this,
                                        LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

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
}
