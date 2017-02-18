
package io.particle.cloudsdk.example_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class DevicesAdapter extends ArrayAdapter<ParticleDevice> {

    public static final int WALL_UNIT_TEMP = 100;
    private static final String TAG = "DevicesAdapter";
    private static final int CONNECTED = 1;
    private static final int NOT_CONNECTED = 0;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == WALL_UNIT_TEMP) {
                Bundle bundle = message.getData();
                String eventName = bundle.getString("EventName");
                String payloadData = bundle.getString("Payload");
                // TextView textviewCurrent;
                // textviewCurrent = viewHolder.temperature.setText(payloadData);
            }
        }
    };
    ViewHolder viewHolder; // view lookup cache stored in tag
    Context mContext;
    Activity callingActivity;
    // Keep track of subscriptions
    List<Long> subscriptions = new ArrayList<Long>();
    private ArrayList<ParticleDevice> dataSet;
    private int lastPosition = -1;

    public DevicesAdapter(Context context, ArrayList<ParticleDevice> data,
            Activity callingActivity) {
        super(context, R.layout.device_template, data);
        this.dataSet = data;
        this.mContext = context;
        this.callingActivity = callingActivity;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public ParticleDevice getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ParticleDevice dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        // ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.device_template, parent, false);
            viewHolder.temperature = (TextView) convertView.findViewById(R.id.textview_temperature);
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.textview_device_name);
            viewHolder.warning = (ImageButton) convertView.findViewById(R.id.button_warning);
            viewHolder.status = (ImageButton) convertView.findViewById(R.id.button_status);

            // // Todo: Better way for changing view with click on either temperature, or name
            // viewHolder.temperature.setOnClickListener(new View.OnClickListener() {
            // @Override
            // public void onClick(View v) {
            // Toast.makeText(mContext, "temperature", Toast.LENGTH_SHORT).show();
            //
            // }
            // });

            viewHolder.deviceName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(mContext, "temperature", Toast.LENGTH_SHORT).show();
                    // Todo: Needs to have a check if the device is online before moving into device
                    // screen
                    // if (device.isConnected){
                    Intent intent = new Intent(mContext, DeviceActivity.class);
                    intent.putExtra("Device", dataModel);
                    Log.d(TAG, "Before startactivity");
                    mContext.startActivity(intent);
                    // else {
                    // Toast.makeText(mContext, "Device is not online", Toast.LENGTH_SHORT).show();
                    // }

                }
            });

            viewHolder.warning.setOnClickListener(
                    view -> Toast.makeText(mContext, "Warning", Toast.LENGTH_SHORT).show());

            viewHolder.status.setOnClickListener(
                    view -> Toast.makeText(mContext, "Status", Toast.LENGTH_SHORT).show());

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.temperature.setText("-- \u2109");
        viewHolder.deviceName.setText(dataModel.getName());
        viewHolder.warning.setVisibility(dataModel.isFlashing() ? View.VISIBLE : View.INVISIBLE);
        viewHolder.status.setVisibility(!dataModel.isConnected() ? View.VISIBLE : View.INVISIBLE);

        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Long>() {

            @Override
            public Long callApi(ParticleCloud particleCloud)
                    throws ParticleCloudException, IOException {
                return ParticleCloudSDK.getCloud().subscribeToMyDevicesEvents(
                        "wall_temp",
                        new ParticleEventHandler() {
                            public void onEvent(String eventName, ParticleEvent event) {
                                // Message message = handler.obtainMessage(WALL_UNIT_TEMP);
                                // Bundle bundle = new Bundle();
                                //
                                // bundle.putInt("ViewId", viewHolder.temperature.getId());
                                // bundle.putString("DeviceId", event.deviceId);
                                // bundle.putString("EventName", eventName);
                                // bundle.putString("Payload", event.dataPayload);
                                // message.setData(bundle);
                                // handler.sendMessage(message);
                                callingActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toaster.s(callingActivity, "Ppostiio: " + position);
                                    }
                                });

                            }

                            public void onEventError(Exception e) {
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

        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView temperature;
        TextView deviceName;
        ImageButton warning;
        ImageButton status;
    }
}
