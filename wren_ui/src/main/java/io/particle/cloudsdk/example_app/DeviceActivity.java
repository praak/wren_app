package io.particle.cloudsdk.example_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

/**
 * Created by charlesscholle on 2/11/17.
 */

public class DeviceActivity extends AppCompatActivity {

    public static final int WALL_UNIT_TEMP = 100;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == WALL_UNIT_TEMP) {
                Bundle bundle = message.getData();
                String eventName = bundle.getString("EventName");
                String payloadData = bundle.getString("Payload");
                TextView textviewCurrent = (TextView) DeviceActivity.this
                        .findViewById(R.id.textview_current);
                textviewCurrent.setText("Current Temperature: \t" + payloadData + " \u2109");
            }
        }
    };

    // Keep track of subscriptions
    List<Long> subscriptions = new ArrayList<Long>();
    TextView textview_temperature;
    ImageButton remotesensors, mode, setschedule;
    Button setTemp;
    ParticleDevice mDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        Bundle bundle = getIntent().getExtras();
        mDevice = bundle.getParcelable("Device");
        setTitle(mDevice.getName());

        setTemp = (Button) findViewById(R.id.button_set_temp);
        textview_temperature = (TextView) findViewById(R.id.textview_temperature);

        remotesensors = (ImageButton) findViewById(R.id.imagebutton_remotesensors);
        mode = (ImageButton) findViewById(R.id.imagebutton_mode);
        setschedule = (ImageButton) findViewById(R.id.imagebutton_setschedule);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("SharedTemp", 0);
        SharedPreferences.Editor editor = pref.edit();

        // TODO: Grab settings from device instead of shared prefs
        // getting the value from shared preferences to update number on Button
        String sharedTemp;
        sharedTemp = pref.getString(mDevice.getID() + "_setTemp", "69");
        setTemp.setText(sharedTemp + " \u2109");

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
            // TODO: Grab settings from device instead of shared prefs
            numberPicker.setValue(Integer.valueOf(pref.getString(mDevice.getID() + "_sharedTemp", "72")));
            numberPicker.setWrapSelectorWheel(false);

            alertDialogBuilder.setTitle("Set Temperature");
            alertDialogBuilder
                    .setView(numberPicker)
                    .setCancelable(true)
                    .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            int userTemp = numberPicker.getValue();
                            setTemp.setText(userTemp + " \u2109");
                            editor.putString(mDevice.getID() + "_setTemp", String.valueOf(userTemp));
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

        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Long>() {

            @Override
            public Long callApi(ParticleCloud particleCloud)
                    throws ParticleCloudException, IOException {
                return ParticleCloudSDK.getCloud().subscribeToDeviceEvents(
                        "wall_temp",
                        mDevice.getID(),
                        new ParticleEventHandler() {
                            public void onEvent(String eventName, ParticleEvent event) {
                                Message message = handler.obtainMessage(WALL_UNIT_TEMP);
                                Bundle bundle = new Bundle();
                                bundle.putString("EventName", eventName);
                                bundle.putString("Payload", event.dataPayload);
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }

                            public void onEventError(Exception e) {
                                Toaster.s(DeviceActivity.this, "Error: " + e.toString());
                            }
                        });
            }

            @Override
            public void onSuccess(Long subId) {
                subscriptions.add(subId);
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Async.executeAsync(ParticleCloudSDK.getCloud(),
                new Async.ApiWork<ParticleCloud, ParticleCloud>() {
                    @Override
                    public ParticleCloud callApi(@NonNull ParticleCloud ParticleCloud)
                            throws ParticleCloudException, IOException {
                        for (Long subscription : subscriptions) {
                            ParticleCloud.unsubscribeFromEventWithID(subscription);
                        }
                        return ParticleCloud;
                    }

                    @Override
                    public void onSuccess(@NonNull ParticleCloud cloud) { // this goes on the main

                    }

                    @Override
                    public void onFailure(@NonNull ParticleCloudException e) {
                        e.printStackTrace();
                    }
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
        Async.executeAsync(ParticleCloudSDK.getCloud(),
                new Async.ApiWork<ParticleCloud, ParticleCloud>() {
                    @Override
                    public ParticleCloud callApi(@NonNull ParticleCloud ParticleCloud)
                            throws ParticleCloudException, IOException {
                        for (Long subscription : subscriptions) {
                            ParticleCloud.unsubscribeFromEventWithID(subscription);
                        }
                        return ParticleCloud;
                    }

                    @Override
                    public void onSuccess(@NonNull ParticleCloud cloud) { // this goes on the main

                    }

                    @Override
                    public void onFailure(@NonNull ParticleCloudException e) {
                        e.printStackTrace();
                    }
                });

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
