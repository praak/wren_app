package io.particle.cloudsdk.example_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

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

    int previouslySelectedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        monday = (Button) findViewById(R.id.button_monday);
        tuesday = (Button) findViewById(R.id.button_tuesday);
        wednesday = (Button) findViewById(R.id.button_wednesday);
        thursday = (Button) findViewById(R.id.button_thursday);
        friday = (Button) findViewById(R.id.button_friday);
        saturday = (Button) findViewById(R.id.button_saturday);
        sunday = (Button) findViewById(R.id.button_sunday);

        setButtonForDayOfWeekAsSelected();
        setupOnClickListeners();

    }

    private void setupOnClickListeners() {
        monday.setOnClickListener(view -> {
            toggleButtons(monday.getId());
        });
        tuesday.setOnClickListener(view -> {
            toggleButtons(tuesday.getId());
        });
        wednesday.setOnClickListener(view -> {
            toggleButtons(wednesday.getId());
        });
        thursday.setOnClickListener(view -> {
            toggleButtons(thursday.getId());
        });
        friday.setOnClickListener(view -> {
            toggleButtons(friday.getId());
        });
        saturday.setOnClickListener(view -> {
            toggleButtons(saturday.getId());
        });
        sunday.setOnClickListener(view -> {
            toggleButtons(sunday.getId());
        });
    }

    private void toggleButtons(int selectedButton) {
        findViewById(previouslySelectedButton).setSelected(false);
        findViewById(selectedButton).setSelected(true);
        previouslySelectedButton = selectedButton;
    }

    private void setButtonForDayOfWeekAsSelected() {
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime()).trim();

        if (weekDay.equals("Monday")) {
            monday.setSelected(true);
            previouslySelectedButton = monday.getId();

        } else if (weekDay.equals("Tuesday")) {
            tuesday.setSelected(true);
            previouslySelectedButton = tuesday.getId();

        } else if (weekDay.equals("Wednesday")) {
            wednesday.setSelected(true);
            previouslySelectedButton = wednesday.getId();

        } else if (weekDay.equals("Thursday")) {
            thursday.setSelected(true);
            previouslySelectedButton = thursday.getId();

        } else if (weekDay.equals("Friday")) {
            friday.setSelected(true);
            previouslySelectedButton = friday.getId();

        } else if (weekDay.equals("Saturday")) {
            saturday.setSelected(true);
            previouslySelectedButton = saturday.getId();

        } else if (weekDay.equals("Sunday")) {
            sunday.setSelected(true);
            previouslySelectedButton = sunday.getId();

        } else {
        }


    }


}
