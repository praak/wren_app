package io.particle.cloudsdk.example_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;

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

    ImageButton remotesensors, mode, setschedule;
    Button setTemp;

    ParticleDevice mDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        setTemp = (Button) findViewById(R.id.button_set_temp);

        remotesensors = (ImageButton) findViewById(R.id.imagebutton_remotesensors);
        mode = (ImageButton) findViewById(R.id.imagebutton_mode);
        setschedule = (ImageButton) findViewById(R.id.imagebutton_setschedule);

        Bundle bundle = getIntent().getExtras();
        mDevice = bundle.getParcelable("Device");
        // Set the title name for the page
        setTitle(mDevice.getName());

        SharedPreferences pref = getApplicationContext().getSharedPreferences(mDevice.getID(), 0);
        SharedPreferences.Editor editor = pref.edit();

        // getting the value from shared preferences to update number on Button
        int sharedTemp;
        sharedTemp = pref.getInt(mDevice.getID(), 69);
        setTemp.setText("" + sharedTemp + " \u2109");

        int sharedMode;
        sharedMode = pref.getInt(mDevice.getID() + "_mode", 0);
        Drawable modeIcon = mode.getBackground();

        changeModeBackground(sharedMode, modeIcon);

        setTemp.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            AlertDialog alertDialog;

            final NumberPicker numberPicker = new NumberPicker(this);
            numberPicker.setMinValue(40);
            numberPicker.setMaxValue(110);
            numberPicker.setValue(pref.getInt(mDevice.getID(), 72));
            numberPicker.setWrapSelectorWheel(false);

            alertDialogBuilder.setTitle("Set Temperature");
            alertDialogBuilder
                    .setView(numberPicker)
                    .setCancelable(true)
                    .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            int userTemp = numberPicker.getValue();
                            /*
                            Toast to see the value set by user
                            Toast.makeText(getBaseContext(), "Temp:" + userTemp,
                            Toast.LENGTH_SHORT)
                            .show();
                            Todo: Needs to be setup on wall unit.
                            \u2109 = degreesF
                            */
                            setTemp.setText(userTemp + " \u2109");
                            editor.putInt(mDevice.getID(), userTemp);
                            editor.apply();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> {
                        // close the dialog box and do nothing
                        dialog.cancel();
                    });

            // create alert dialog
            alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        });

        mode.setOnClickListener(v -> {
            final CharSequence[] items = {"Auto", "Heat", "Cool", "OFF"};
            AlertDialog.Builder builder = new AlertDialog.Builder(DeviceActivity.this);
            builder.setTitle("Select Mode:");
            builder.setItems(items, (dialog, item) -> {
                // Do something with the selection
//                Toast.makeText(getBaseContext(), "Mode: " + items[item] + "num:" + item, Toast.LENGTH_SHORT)
//                        .show();
                //Todo: need preferences to save for MODE
                editor.putInt(mDevice.getID() + "_mode", item);
                editor.apply();
                changeModeBackground(item, modeIcon);
            });

            AlertDialog alert = builder.create();
            alert.show();
            alert.getWindow().setLayout(600, 750);
        });

        setschedule.setOnClickListener(view -> {
            Intent intent = new Intent(this, ScheduleActivity.class);
            startActivity(intent);
        });

    }

    public void changeModeBackground(int sharedMode, Drawable modeIcon) {
        switch (sharedMode) {
            case 0:
                if (modeIcon != getResources().getDrawable(R.drawable.ic_heat_cold)) {
                    mode.setBackground(getResources().getDrawable(R.drawable.ic_heat_cold));
                } else {// do nothing
                }
                break;
            case 1:
                if (modeIcon != getResources().getDrawable(R.drawable.ic_red_heat)) {
                    mode.setBackground(getResources().getDrawable(R.drawable.ic_red_heat));
                } else {// do nothing
                }
                break;
            case 2:
                if (modeIcon != getResources().getDrawable(R.drawable.ic_blue_cold)) {
                    mode.setBackground(getResources().getDrawable(R.drawable.ic_blue_cold));
                } else {// do nothing
                }
                break;
            case 3:
                if (modeIcon != getResources().getDrawable(R.drawable.ic_remote_sensors)) {
                    mode.setBackground(getResources().getDrawable(R.drawable.ic_remote_sensors));
                } else {// do nothing
                }
                break;
        }
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
                        .setNegativeButton("Cancel", (dialog, id) -> {
                            // close the dialog box and do nothing
                            dialog.cancel();
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
                        .setNegativeButton("Cancel", (dialog, id) -> {
                            // close the dialog box and do nothing
                            dialog.cancel();
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
                        .setNegativeButton("Cancel", (dialog, id) -> {
                            // close the dialog box and do nothing
                            dialog.cancel();
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
