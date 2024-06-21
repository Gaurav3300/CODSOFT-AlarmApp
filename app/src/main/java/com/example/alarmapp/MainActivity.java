package com.example.alarmapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.alarmapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView timeTextView;
    private EditText timeHour;
    private EditText timeMinute;
    private Button setTime;
    private Button setAlarm;
    private Handler handler;
    private Runnable runnable;
    private TimePickerDialog timePickerDialog;
    private Calendar calendar;
    private int currentHour;
    private int currentMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the views
        timeTextView = findViewById(R.id.timeTextView);
        timeHour = findViewById(R.id.etHour);
        timeMinute = findViewById(R.id.etMinute);
        setTime = findViewById(R.id.btnTime);
        setAlarm = findViewById(R.id.btnAlarm);

        // Set up the handler for updating the time
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updateTime();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);

        // Set up the time picker dialog
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        timeHour.setText(String.format("%02d", hourOfDay));
                        timeMinute.setText(String.format("%02d", minutes));
                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();
            }
        });

        // Set up the alarm button
        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timeHour.getText().toString().isEmpty() && !timeMinute.getText().toString().isEmpty()) {
                    int hour = Integer.parseInt(timeHour.getText().toString());
                    int minute = Integer.parseInt(timeMinute.getText().toString());

                    // Set multiple alarms
                    setMultipleAlarms(hour, minute);
                } else {
                    Toast.makeText(MainActivity.this, "Please choose a time", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        timeTextView.setText(currentTime);
    }

    private void setMultipleAlarms(int hour, int minute) {
        Intent intent1 = createAlarmIntent(hour, minute, "1st Alarm");
        Intent intent2 = createAlarmIntent(hour, minute + 5, "2nd Alarm");
        Intent intent3 = createAlarmIntent(hour, minute + 10, "3rd Alarm");

        if (intent1.resolveActivity(getPackageManager()) != null) {
            startActivity(intent1);

            final Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent2);
                }
            }, 300);

            final Handler handler3 = new Handler();
            handler3.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent3);
                }
            }, 700);
        } else {
            Toast.makeText(this, "There is no app that supports this action", Toast.LENGTH_SHORT).show();
        }
    }

    private Intent createAlarmIntent(int hour, int minute, String message) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, message);
        return intent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
