package com.oladapo.appointmenttrack.Activities;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.oladapo.appointmenttrack.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CreateEditAppointmentActivity extends AppCompatActivity {

    EditText nameEditText, phoneEditText, emailEditText, descEditText, dateEditText, timeEditText;
    TextInputLayout nameLayout, phoneLayout, emailLayout, descLayout, dateLayout, timeLayout;
    Button submit;
    SwitchCompat reminderSwitch, clientReminderSwitch;
    CoordinatorLayout coordinatorLayout;

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_CLIENT_NAME = "name";
    public static final String EXTRA_PHONE = "phone";
    public static final String EXTRA_EMAIL = "phone";
    public static final String EXTRA_DESC = "desc";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_TIME = "time";

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

        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                String format = "dd MMM yy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
                dateEditText.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    new DatePickerDialog(CreateEditAppointmentActivity.this, date, calendar
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
                new DatePickerDialog(CreateEditAppointmentActivity.this, date, calendar
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

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {

                    reminderState = REMINDER_ON;

                    final DatePickerDialog.OnDateSetListener clientReminderDate = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, day);

                            String format = "dd/MM/yy";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
                            reminderDate = simpleDateFormat.format(calendar.getTime());

                            pickReminderTime();
                        }
                    };

                    new DatePickerDialog(CreateEditAppointmentActivity.this,clientReminderDate, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                            .show();
                } else {
                    reminderState = REMINDER_OFF;
                }
            }
        });

        clientReminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    clientReminderState = CLIENT_REMINDER_ON;

                    remindClientAlertDialog();
                } else {
                    clientReminderState = CLIENT_REMINDER_OFF;
                }
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

                    @SuppressLint("DefaultLocale") String time = (String.format("%02d:%02d", hourOfDay, minute));
                    timeEditText.setText(time);
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, listener, hour, minute, false);
        timePickerDialog.show();
    }

    private void pickReminderTime() {
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                if (timePicker.isShown()) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    reminderTime = String.format("%02d:%02d", hourOfDay, minute);
                    Snackbar.make(coordinatorLayout, "Reminder set for " + reminderDate + " at " + reminderTime, Snackbar.LENGTH_LONG).show();
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, listener, hour, minute, false);
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                reminderSwitch.setChecked(false);
            }
        });
        timePickerDialog.setCanceledOnTouchOutside(true);
        timePickerDialog.show();
    }

    private void pickClientReminderTime() {
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                if (timePicker.isShown()) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    clientReminderTime = String.format("%02d:%02d", hourOfDay, minute);
                    Snackbar.make(coordinatorLayout, "Client reminder set for " + clientReminderDate + " at " + clientReminderTime, Snackbar.LENGTH_LONG).show();
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, listener, hour, minute, false);
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                clientReminderSwitch.setChecked(false);
            }
        });
        timePickerDialog.setCanceledOnTouchOutside(true);
        timePickerDialog.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    private void remindClientAlertDialog() {
        final CharSequence[] items = {"SMS", "Email", "Both"};

        new AlertDialog.Builder(this)
                .setTitle("Select how to send reminder to your client")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        if (item == 0) {
                            SMS_REMINDER = true;
                            remindClientDatePicker();
                        } else if (item == 1) {
                            EMAIL_REMINDER = true;
                            remindClientDatePicker();
                        } else if (item == 2) {
                            BOTH_TYPES = true;
                            remindClientDatePicker();
                        }
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        clientReminderSwitch.setChecked(false);
                    }
                })
        .show();
    }

    private void remindClientDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                String format = "dd/MM/yy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
                clientReminderDate = simpleDateFormat.format(calendar.getTime());

                pickClientReminderTime();
            }
        };
        new DatePickerDialog(CreateEditAppointmentActivity.this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void validateAndSubmitForm() {

        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String desc = descEditText.getText().toString();

        SimpleDateFormat format = new SimpleDateFormat("dd MMM", Locale.getDefault());
        Date today = new Date();

        String dateAdded = format.format(today);

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
                    if (clientReminderState == CLIENT_REMINDER_ON && SMS_REMINDER) {

                        if (phoneEditText.getText().toString().trim().isEmpty()) {
                            phoneLayout.setError("Phone is required");

                        } else {
                            phoneLayout.setErrorEnabled(false);
                            submitForm(name, phone, email, desc, date, time, reminderDate, reminderTime,
                                    clientReminderDate, clientReminderTime, reminderState, clientReminderState, dateAdded);
                        }
                    } else if (clientReminderState == CLIENT_REMINDER_ON && EMAIL_REMINDER) {

                        if (emailEditText.getText().toString().trim().isEmpty()) {
                            emailLayout.setError("Email is required");

                        } else {
                            emailLayout.setErrorEnabled(false);
                            submitForm(name, phone, email, desc, date, time, reminderDate, reminderTime,
                                    clientReminderDate, clientReminderTime, reminderState, clientReminderState, dateAdded);
                        }
                    } else if (clientReminderState == CLIENT_REMINDER_ON && BOTH_TYPES) {

                        if (emailEditText.getText().toString().trim().isEmpty() && phoneEditText.getText().toString().trim().isEmpty()) {
                            emailLayout.setError("Email is required");
                            phoneLayout.setError("Phone is required");

                        } else {
                            emailLayout.setErrorEnabled(false);
                            phoneLayout.setErrorEnabled(false);
                            submitForm(name, phone, email, desc, date, time, reminderDate, reminderTime,
                                    clientReminderDate, clientReminderTime, reminderState, clientReminderState, dateAdded);
                        }
                    } else {
                        submitForm(name, phone, email, desc, date, time, reminderDate, reminderTime,
                                clientReminderDate, clientReminderTime, reminderState, clientReminderState, dateAdded);
                    }
                }
            }
        }
    }

    private void submitForm(String name, String phone, String email, String desc,
                            String date, String time, String reminderDate, String reminderTime,
                            String clientReminderDate, String clientReminderTime, int reminderState,
                            int clientReminderState, String dateAdded) {
        Intent intent = new Intent(CreateEditAppointmentActivity.this, MainActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("email", email);
        intent.putExtra("desc", desc);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("reminderDate", reminderDate);
        intent.putExtra("reminderTime", reminderTime);
        intent.putExtra("clientReminderDate", clientReminderDate);
        intent.putExtra("clientReminderTime", clientReminderTime);
        intent.putExtra("reminderState", reminderState);
        intent.putExtra("clientReminderState", clientReminderState);
        intent.putExtra("dateAdded", dateAdded);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            intent.putExtra(EXTRA_ID, id);
        }

        setResult(2, intent);
        finish();
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

    @Override
    protected void onResume() {
        super.onResume();
    }
}
