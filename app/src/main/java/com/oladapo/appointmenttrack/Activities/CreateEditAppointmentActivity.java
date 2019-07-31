package com.oladapo.appointmenttrack.Activities;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.oladapo.appointmenttrack.DatePickerFragment;
import com.oladapo.appointmenttrack.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class CreateEditAppointmentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "vkv";
    EditText nameEditText, phoneEditText, emailEditText, descEditText, dateEditText, timeEditText;
    TextInputLayout nameLayout, phoneLayout, emailLayout, descLayout, dateLayout, timeLayout;
    MaterialButton submit;
    SwitchCompat reminderSwitch, clientReminderSwitch;
    CoordinatorLayout coordinatorLayout;

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_CLIENT_NAME = "name";
    public static final String EXTRA_PHONE = "phone";
    public static final String EXTRA_EMAIL = "phone";
    public static final String EXTRA_DESC = "desc";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_TIME = "time";
    public static final String EXTRA_REMINDER_DATE = "reminder_date";
    public static final String EXTRA_REMINDER_TIME = "reminder_time";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 10;

    String reminderDate;
    String reminderTime;
    int reminderState;
    int clientReminderState;
    String clientReminderDate;
    String clientReminderTime;

    private static final int REMINDER_ON = 1;
    private static final int REMINDER_OFF = 0;
    private static final int CLIENT_REMINDER_OFF = 0;
    private static final int CLIENT_REMINDER_ON = 1;
    private static Boolean SMS_REMINDER = false;
    private static Boolean EMAIL_REMINDER = false;
    private static Boolean BOTH_TYPES = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int startColor = ContextCompat.getColor(this, R.color.colorPrimary);
            int endColor = ContextCompat.getColor(this, R.color.colorPrimary);
            ObjectAnimator.ofArgb(getWindow(), "statusBarColor", startColor, endColor).start();
        } else {

            int startColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            int endColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            ObjectAnimator.ofArgb(getWindow(), "statusBarColor", startColor, endColor).start();
        }

        coordinatorLayout = findViewById(R.id.createAppCoordinator);
        nameEditText = findViewById(R.id.input_name);
        phoneEditText = findViewById(R.id.input_phone);
        emailEditText = findViewById(R.id.input_email);
        descEditText = findViewById(R.id.input_desc);
        dateEditText = findViewById(R.id.input_date);
        timeEditText = findViewById(R.id.input_time);
        nameLayout = findViewById(R.id.input_layout_name);
        phoneLayout = findViewById(R.id.input_layout_phone);
        emailLayout = findViewById(R.id.input_layout_email);
        descLayout = findViewById(R.id.input_layout_desc);
        dateLayout = findViewById(R.id.input_layout_date);
        timeLayout = findViewById(R.id.input_layout_time);
        reminderSwitch = findViewById(R.id.reminderSwitch);
        clientReminderSwitch = findViewById(R.id.clientReminderSwitch);
        submit = findViewById(R.id.submit);

        dateEditText.setInputType(InputType.TYPE_NULL);
        timeEditText.setInputType(InputType.TYPE_NULL);

        initToolbar();



        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    DialogFragment datePicker = new DatePickerFragment();
                    datePicker.show(getSupportFragmentManager(), "date picker");
                }
            }
        });

        timeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    pickTime();
                }
            }
        });

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    reminderState = REMINDER_ON;

                    reminderAlertDialog();
                } else {
                    reminderState = REMINDER_OFF;

                    reminderDate = null;
                    reminderTime = null;
                }
            }
        });
    }

    private void reminderAlertDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_remider, (ViewGroup) findViewById(android.R.id.content), false);

        dialog.setView(dialogView);

        final SwitchCompat allDayReminderSwitch = dialogView.findViewById(R.id.reminderAllDaySwitch);
        final TextView dateText = dialogView.findViewById(R.id.reminderDate);
        final TextView timeText = dialogView.findViewById(R.id.reminderTime);

        dateText.setClickable(true);
        timeText.setClickable(true);

        allDayReminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    timeText.setVisibility(View.GONE);
                } else {
                    timeText.setVisibility(View.VISIBLE);
                }
            }
        });

        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                String chosenDate = DateFormat.getDateInstance().format(calendar.getTime());
                dateText.setText(chosenDate);
            }
        };

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(dialogView.getContext(), dateSetListener, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                if (timePicker.isShown()) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    @SuppressLint("DefaultLocale")String chosenTime = (String.format("%02d:%02d", hourOfDay, minute));
                    timeText.setText(chosenTime);
                }
            }
        };

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(dialogView.getContext(), timeSetListener, hour, minute, false);
                timePickerDialog.setCanceledOnTouchOutside(false);
                timePickerDialog.show();
            }
        });

        dialog.setCancelable(false)
                .setTitle("Set reminder details")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dateText.getText().toString().matches("Select date") || !allDayReminderSwitch.isChecked() && timeText.getText().toString().matches("Select time")) {
                            Snackbar.make(coordinatorLayout, "Date and time is needed to set a reminder", Snackbar.LENGTH_LONG).show();
                            reminderSwitch.setChecked(false);
                        } else {
                            reminderDate = dateText.getText().toString();
                        }

                        if (!allDayReminderSwitch.isChecked() && !timeText.getText().toString().matches("Select time")) {
                            reminderTime = timeText.getText().toString();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        reminderSwitch.setChecked(false);
                    }
                });

        dialog.create().show();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID)) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit appointment");
            nameEditText.setText(intent.getStringExtra(EXTRA_CLIENT_NAME));
            phoneEditText.setText(intent.getStringExtra(EXTRA_PHONE));
            emailEditText.setText(intent.getStringExtra(EXTRA_EMAIL));
            descEditText.setText(intent.getStringExtra(EXTRA_DESC));
            dateEditText.setText(intent.getStringExtra(EXTRA_DATE));
            timeEditText.setText(intent.getStringExtra(EXTRA_TIME));

        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle("New appointment");
        }

        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void pickTime() {

        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                if (timePicker.isShown()) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    @SuppressLint("DefaultLocale")String chosenTime = (String.format("%02d:%02d", hourOfDay, minute));
                    timeEditText.setText(chosenTime);
                }
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, listener, hour, minute, false);
        timePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        String chosenDate = DateFormat.getDateInstance().format(calendar.getTime());
        dateEditText.setText(chosenDate);
    }
}
