
package io.particle.cloudsdk.example_app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by charlesscholle on 2/11/17.
 */

public class SensorsAdapter extends ArrayAdapter<Sensor> {

    private static final String TAG = "DevicesAdapter";

    ViewHolder viewHolder; // view lookup cache stored in tag
    Context mContext;
    Activity mCallingActivity;

    private ArrayList<Sensor> mSensors;

    public SensorsAdapter(Context context, ArrayList<Sensor> jsonString,
            Activity callingActivity) {
        super(context, R.layout.sensor_template, jsonString);
        mSensors = jsonString;
        mContext = context;
        mCallingActivity = callingActivity;
    }

    @Override
    public int getCount() {
        return mSensors.size();
    }

    @Override
    public Sensor getItem(int position) {
        return mSensors.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Sensor sensor = getItem(position);

        final View result;

        // Check if an existing view is being reused, otherwise inflate the view
        viewHolder = new ViewHolder();

        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.sensor_template, parent, false);
            viewHolder.temperature = (TextView) convertView.findViewById(R.id.textview_temperature);
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.textview_device_name);
            viewHolder.status = (ImageButton) convertView.findViewById(R.id.button_status);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.temperature.setText(sensor.mTemp + " \u2109");
        viewHolder.deviceName.setText(sensor.mId);
        viewHolder.status.setVisibility(sensor.mBattStatus ? View.INVISIBLE : View.VISIBLE);

        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView temperature;
        TextView deviceName;
        ImageButton status;
    }
}
