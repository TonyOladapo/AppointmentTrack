package com.oladapo.appointmenttrack.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.oladapo.appointmenttrack.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CreateAppointmentActivity extends AppCompatActivity {

    EditText nameEditText, phoneEditText, descEditText, dateEditText, timeEditText;
    TextInputLayout nameLayout, phoneLayout, descLayout, dateLayout, timeLayout;
    Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        nameEditText = findViewById(R.id.input_name);
        phoneEditText = findViewById(R.id.input_phone);
        descEditText = findViewById(R.id.input_desc);
        dateEditText = findViewById(R.id.input_date);
        timeEditText = findViewById(R.id.input_time);
        nameLayout = findViewById(R.id.input_layout_name);
        phoneLayout = findViewById(R.id.input_layout_phone);
        descLayout = findViewById(R.id.input_layout_desc);
        dateLayout = findViewById(R.id.input_layout_date);
        timeLayout = findViewById(R.id.input_layout_time);
        submit = findViewById(R.id.submit);

        dateEditText.setInputType(InputType.TYPE_NULL);
        timeEditText.setInputType(InputType.TYPE_NULL);

        initToolbar();

        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                String format = "dd/MM/yy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
                dateEditText.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    new DatePickerDialog(CreateAppointmentActivity.this, date, calendar
                            .get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                            .show();
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

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CreateAppointmentActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndSubmitForm();
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

                    String time = hourOfDay + ":" + minute;
                    timeEditText.setText(time);
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, listener, hour, minute, false);
        timePickerDialog.show();
    }

    private void validateAndSubmitForm() {
        if (nameEditText.getText().toString().trim().isEmpty()) {
            nameLayout.setError("Name is required");
        } else {
            nameLayout.setErrorEnabled(false);
            if (dateEditText.getText().toString().trim().isEmpty()) {
                dateLayout.setError("Date is required");
            } else {
                dateLayout.setErrorEnabled(false);
                if (timeEditText.getText().toString().trim().isEmpty()) {
                    timeLayout.setError("Time is required");
                } else {
                    dateLayout.setErrorEnabled(false);

                    String name = nameEditText.getText().toString();
                    String phone = phoneEditText.getText().toString();
                    String desc = descEditText.getText().toString();
                    String date = dateEditText.getText().toString();
                    String time = timeEditText.getText().toString();

                    submitForm(name, phone, desc, date, time);
                }
            }
        }
    }

    private void submitForm(String name, String phone, String desc,
                            String date, String time) {
        Intent intent = new Intent(CreateAppointmentActivity.this, MainActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("desc", desc);
        intent.putExtra("date", date);
        intent.putExtra("time", time);

        setResult(2, intent);
        finish();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("New appointment");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
