
package io.particle.cloudsdk.example_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.particle.android.sdk.cloud.ParticleDevice;

/**
 * Created by charlesscholle on 2/11/17.
 */

public class DevicesAdapter extends ArrayAdapter<ParticleDevice> {

    private static final String TAG = "DevicesAdapter";

    ViewHolder viewHolder; // view lookup cache stored in tag
    Context mContext;
    Activity callingActivity;

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

        SharedPreferences pref = mContext.getSharedPreferences("DeviceAdapter",
                Context.MODE_PRIVATE);

        String sharedString;
        Log.d(TAG, "Get ID: " + dataModel.getID());
        sharedString = pref.getString(dataModel.getID() + "_wall_temp", "null");

        final View result;

        // Check if an existing view is being reused, otherwise inflate the view
        // ViewHolder viewHolder; // view lookup cache stored in tag
        viewHolder = new ViewHolder();

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.device_template, parent, false);
            viewHolder.temperature = (TextView) convertView.findViewById(R.id.textview_temperature);
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.textview_device_name);
            viewHolder.warning = (ImageButton) convertView.findViewById(R.id.button_warning);
            viewHolder.status = (ImageButton) convertView.findViewById(R.id.button_status);

            viewHolder.deviceName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DeviceActivity.class);
                    intent.putExtra("Device", dataModel);
                    Log.d(TAG, "Before startactivity");
                    mContext.startActivity(intent);
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

        // Animation animation = AnimationUtils.loadAnimation(mContext,
        // (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        // result.startAnimation(animation);
        // lastPosition = position;

        Log.d(TAG, "ID " + dataModel.getID() + "SharedString: " + sharedString);
        if (sharedString == "null") {
            viewHolder.temperature.setText("-- \u2109");
        } else {
            viewHolder.temperature.setText(sharedString + " \u2109");
        }
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
