
package io.particle.cloudsdk.example_app;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;

import io.particle.android.sdk.cloud.ParticleDevice;

/**
 * Created by charlesscholle on 2/11/17.
 */

public class DevicesAdapter extends ArrayAdapter<ParticleDevice> implements View.OnClickListener {

    private static final String TAG = "DevicesAdapter";
    private static final int CONNECTED = 1;
    private static final int NOT_CONNECTED = 0;

    Context mContext;
    private ArrayList<ParticleDevice> dataSet;
    private int lastPosition = -1;

    public DevicesAdapter(Context context, ArrayList<ParticleDevice> data) {
        super(context, R.layout.device_template, data);
        this.dataSet = data;
        this.mContext = context;
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
    public void onClick(View view) {
        int position = (Integer) view.getTag();
        Log.d(TAG, String.valueOf(position));
        Object object = getItem(position);
        ParticleDevice dataModel = (ParticleDevice) object;

        switch (view.getId()) {

            case R.id.textview_temperature:

                Toast.makeText(mContext, dataModel.getName(), Toast.LENGTH_LONG).show();

                break;
            case R.id.textview_device_name:

                Toast.makeText(mContext, dataModel.getName(), Toast.LENGTH_LONG).show();

                break;
            // case R.id.button_warning:
            //
            // Toast.makeText(mContext, dataModel.getName(), Toast.LENGTH_LONG).show();
            //
            // break;
            //
            // case R.id.button_status:
            //
            // Toast.makeText(mContext, dataModel.getName(), Toast.LENGTH_LONG).show();
            //
            // break;
            //
            // case R.id.button_device_edit:
            //
            // Toast.makeText(mContext, dataModel.getName(), Toast.LENGTH_LONG).show();
            //
            // break;

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ParticleDevice dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.device_template, parent, false);
            viewHolder.temperature = (TextView) convertView.findViewById(R.id.textview_temperature);
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.textview_device_name);
            viewHolder.warning = (ImageButton) convertView.findViewById(R.id.button_warning);
            viewHolder.status = (ImageButton) convertView.findViewById(R.id.button_status);

            viewHolder.temperature.setOnClickListener(new View.OnClickListener() {
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

            viewHolder.deviceName.setOnClickListener(view -> Toast
                    .makeText(mContext, dataModel.getName(), Toast.LENGTH_SHORT).show());

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

        viewHolder.temperature.setText("72" + (char) 0x00B0 + " F");
        viewHolder.deviceName.setText(dataModel.getName());
        viewHolder.warning.setVisibility(dataModel.isFlashing() ? View.VISIBLE : View.INVISIBLE);
        viewHolder.status.setVisibility(!dataModel.isConnected() ? View.VISIBLE : View.INVISIBLE);
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
