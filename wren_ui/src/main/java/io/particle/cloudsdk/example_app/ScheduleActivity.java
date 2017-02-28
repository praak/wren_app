
package io.particle.cloudsdk.example_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    Button monday;
    Button tuesday;
    Button wednesday;
    Button thursday;
    Button friday;
    Button saturday;
    Button sunday;

    TimePicker timepickerSchedule;
    ToggleButton toggleButtonOpenClosed;
    String toggleButtonTextOn;
    String toggleButtonTextOff;

    Button currentlySelectedButton;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        monday = (Button) findViewById(R.id.button_monday);
        tuesday = (Button) findViewById(R.id.button_tuesday);
        wednesday = (Button) findViewById(R.id.button_wednesday);
        thursday = (Button) findViewById(R.id.button_thursday);
        friday = (Button) findViewById(R.id.button_friday);
        saturday = (Button) findViewById(R.id.button_saturday);
        sunday = (Button) findViewById(R.id.button_sunday);
        timepickerSchedule = (TimePicker) findViewById(R.id.timepicker_schedule);
        toggleButtonOpenClosed = (ToggleButton) findViewById(R.id.toggle_button_open_closed);
        toggleButtonTextOn = (String) toggleButtonOpenClosed.getTextOn();
        toggleButtonTextOff = (String) toggleButtonOpenClosed.getTextOff();

        monday.setOnClickListener(view -> {
            toggleButtons(monday);
        });
        tuesday.setOnClickListener(view -> {
            toggleButtons(tuesday);
        });
        wednesday.setOnClickListener(view -> {
            toggleButtons(wednesday);
        });
        thursday.setOnClickListener(view -> {
            toggleButtons(thursday);
        });
        friday.setOnClickListener(view -> {
            toggleButtons(friday);
        });
        saturday.setOnClickListener(view -> {
            toggleButtons(saturday);
        });
        sunday.setOnClickListener(view -> {
            toggleButtons(sunday);
        });

        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime()).trim();

        if (weekDay.equals("Monday")) {
            initializeButtons(monday);
        } else if (weekDay.equals("Tuesday")) {
            initializeButtons(tuesday);
        } else if (weekDay.equals("Wednesday")) {
            initializeButtons(wednesday);
        } else if (weekDay.equals("Thursday")) {
            initializeButtons(thursday);
        } else if (weekDay.equals("Friday")) {
            initializeButtons(friday);
        } else if (weekDay.equals("Saturday")) {
            initializeButtons(saturday);
        } else if (weekDay.equals("Sunday")) {
            initializeButtons(sunday);
        } else {
            // Have we made it to another planet and now have different names for days of the week?
        }

        toggleButtonOpenClosed.setOnCheckedChangeListener((compoundButton, b) -> {
            setTimePicker(currentlySelectedButton);
        });

        timepickerSchedule.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                String toggleButtonText = (toggleButtonOpenClosed.isChecked()
                        ? toggleButtonTextOn : toggleButtonTextOff);
                SharedPreferences.Editor editor = sharedPref.edit();
                int buttonId = currentlySelectedButton.getId();
                editor.putInt(buttonId + toggleButtonText + "hour", hour);
                editor.putInt(buttonId + toggleButtonText + "minute", minute);
                editor.commit();
            }
        });
    }

    private void initializeButtons(Button day) {
        day.setSelected(true);
        currentlySelectedButton = day;
        setTimePicker(day);
    }

    private boolean setTimePicker(Button day) {
        String toggleButtonText = (toggleButtonOpenClosed.isChecked()
                ? toggleButtonTextOn : toggleButtonTextOff);
        int defaultOpenCloseTime = toggleButtonOpenClosed.isChecked() ? 9 : 17;
        int hour = sharedPref
                .getInt(String.valueOf(day.getId()) + toggleButtonText + "hour",
                        defaultOpenCloseTime);
        int minute = sharedPref.getInt(
                String.valueOf(day.getId()) + toggleButtonText + "minute", 0);
        timepickerSchedule.setHour(hour);
        timepickerSchedule.setMinute(minute);
        return true;
    }

    private void toggleButtons(Button selectedButton) {
        currentlySelectedButton.setSelected(false);
        selectedButton.setSelected(true);
        currentlySelectedButton = selectedButton;
        setTimePicker(selectedButton);
    }
}
// Write
// SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
// SharedPreferences.Editor editor = sharedPref.edit();
// editor.putInt(getString(R.string.saved_high_score), newHighScore);
// editor.commit();

// Read
// SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
// int defaultValue = getResources().getInteger(R.string.saved_high_score_default);
// long highScore = sharedPref.getInt(getString(R.string.saved_high_score), defaultValue);
