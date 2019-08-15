package com.oladapo.appointmenttrack.Activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.oladapo.appointmenttrack.Fragments.DatePickerFragment;
import com.oladapo.appointmenttrack.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class CreateEditAppointmentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        EasyPermissions.PermissionCallbacks {

    EditText nameEditText, phoneEditText, emailEditText, descEditText, dateEditText, timeEditText;
    TextInputLayout nameLayout, phoneLayout, emailLayout, descLayout, dateLayout, timeLayout;
    MaterialButton submit;
    SwitchCompat reminderSwitch, clientReminderSwitch;
    ConstraintLayout constraintLayout;

    public static final String EXTRA_INTENT_CODE = "intent_code";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_CLIENT_NAME = "name";
    public static final String EXTRA_PHONE = "phone";
    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_DESC = "desc";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_TIME = "time";
    public static final String EXTRA_REMINDER_TIME = "reminder_time";
    public static final String EXTRA_CLIENT_REMINDER_DATE = "client_reminder_date";
    public static final String EXTRA_CLIENT_REMINDER_TIME = "client_reminder_time";
    public static final String EXTRA_CLIENT_REMINDER_MESSAGE = "client_reminder_message";
    public static final String EXTRA_REMINDER_STATE = "reminder_state";
    public static final String EXTRA_CLIENT_REMINDER_STATE = "client_reminder_state";
    public static final String EXTRA_SMS_REMINDER = "sms_reminder";
    public static final String EXTRA_EMAIL_REMINDER = "email_reminder";
    public static final String EXTRA_BOTH = "both";
    public static final String EXTRA_DATE_TIME = "dateTime";
    public static final String EXTRA_DATE_ADDED = "dateAdded";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 10;

    int reminderTime;
    int reminderState;
    int clientReminderState;
    String clientReminderDate;
    String clientReminderTime;
    String dateAdded;

    private static String TAG = "vkv";

    private boolean SMS_REMINDER;
    private boolean EMAIL_REMINDER;
    private boolean BOTH_TYPES;

    private static final int REMINDER_ON = 1;
    private static final int REMINDER_OFF = 0;
    private static final int CLIENT_REMINDER_OFF = 0;
    private static final int CLIENT_REMINDER_ON = 1;

    private boolean intentHasExtraCode;

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

        checkForCalendarPermission();

        constraintLayout = findViewById(R.id.editAppointmentConstraint);
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

        initDateAndTimeEditTexts();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndSubmitForm();
            }
        });
    }

    private void initDateAndTimeEditTexts() {
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

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime();
            }
        });
    }

    private void initSwitches() {
        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    reminderState = REMINDER_ON;

                    reminderAlertDialog();
                } else {
                    reminderState = REMINDER_OFF;

                    reminderTime = 0;
                }
            }
        });

        clientReminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    clientReminderState = CLIENT_REMINDER_ON;

                    clientReminderAlertDialog();

                } else {
                    clientReminderState = CLIENT_REMINDER_OFF;

                    clientReminderDate = null;
                    clientReminderTime = null;

                    SMS_REMINDER = false;
                    EMAIL_REMINDER = false;
                    BOTH_TYPES = false;
                }
            }
        });
    }

    private void reminderAlertDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_remider, (ViewGroup) findViewById(android.R.id.content), false);

        dialog.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.dialogTimeEditText);
        final MaterialSpinner materialSpinner = dialogView.findViewById(R.id.spinnerDuration);

        materialSpinner.setItems("Minutes", "Hours");

        dialog.setCancelable(false)
                .setTitle("Set reminder details")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!editText.getText().toString().isEmpty()) {
                            int selectedItem = materialSpinner.getSelectedIndex();

                            if (selectedItem == 0) {
                                reminderTime = Integer.parseInt(editText.getText().toString());

                            } else if (selectedItem == 1) {
                                String test = editText.getText().toString();

                                reminderTime = Integer.parseInt(test) * 60;
                            }
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

    private void clientReminderAlertDialog() {
        final CharSequence[] items = {"SMS", "Email", "SMS & Email"};

        final Calendar calendar = Calendar.getInstance();

        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                if (timePicker.isShown()) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    @SuppressLint("DefaultLocale") String chosenTime = (String.format("%02d:%02d", hourOfDay, minute));
                    clientReminderTime = chosenTime;
                }
            }
        };

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                clientReminderDate = DateFormat.getDateInstance().format(calendar.getTime());

                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEditAppointmentActivity.this, timeSetListener, hour, minute, false);
                timePickerDialog.setCancelable(false);
                timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        clientReminderSwitch.setChecked(false);
                    }
                });
                timePickerDialog.show();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle("Select how to send reminder to your client")
                .setCancelable(false)
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        if (item == 0) {
                            SMS_REMINDER = true;
                            EMAIL_REMINDER = false;
                            BOTH_TYPES = false;

                        } else if (item == 1) {
                            SMS_REMINDER = false;
                            EMAIL_REMINDER = true;
                            BOTH_TYPES = false;

                        } else if (item == 2) {
                            SMS_REMINDER = false;
                            EMAIL_REMINDER = false;
                            BOTH_TYPES = true;

                        } else {
                            SMS_REMINDER = false;
                            EMAIL_REMINDER = false;
                            BOTH_TYPES = false;
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (SMS_REMINDER || EMAIL_REMINDER || BOTH_TYPES) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEditAppointmentActivity.this,
                                    dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                            datePickerDialog.setCancelable(false);
                            datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    clientReminderSwitch.setChecked(false);
                                }
                            });
                            datePickerDialog.show();

                        } else {
                            Snackbar.make(constraintLayout, "Please select a method to send client reminder", Snackbar.LENGTH_LONG).show();
                            clientReminderSwitch.setChecked(false);
                        }
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

    private void initToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID) && intent.hasExtra(EXTRA_INTENT_CODE)) {

            reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        reminderState = REMINDER_ON;

                    } else {
                        reminderState = REMINDER_OFF;

                        reminderTime = 0;
                    }
                }
            });

            clientReminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        clientReminderState = CLIENT_REMINDER_ON;

                    } else {
                        clientReminderState = CLIENT_REMINDER_OFF;

                        clientReminderDate = null;
                        clientReminderTime = null;

                        SMS_REMINDER = false;
                        EMAIL_REMINDER = false;
                        BOTH_TYPES = false;
                    }
                }
            });

            intentHasExtraCode = true;

            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit appointment");
            submit.setText(getResources().getText(R.string.save));

            int extraReminderSwitch = intent.getIntExtra(EXTRA_REMINDER_STATE, 0);
            int extraClientReminderSwitch = intent.getIntExtra(EXTRA_CLIENT_REMINDER_STATE, 0);
            boolean extraSms = intent.getBooleanExtra(EXTRA_SMS_REMINDER, false);
            boolean extraEmail = intent.getBooleanExtra(EXTRA_EMAIL_REMINDER, false);
            boolean extraBoth = intent.getBooleanExtra(EXTRA_BOTH, false);

            nameEditText.setText(intent.getStringExtra(EXTRA_CLIENT_NAME));
            phoneEditText.setText(intent.getStringExtra(EXTRA_PHONE));
            emailEditText.setText(intent.getStringExtra(EXTRA_EMAIL));
            descEditText.setText(intent.getStringExtra(EXTRA_DESC));
            dateEditText.setText(intent.getStringExtra(EXTRA_DATE));
            timeEditText.setText(intent.getStringExtra(EXTRA_TIME));
            reminderTime = intent.getIntExtra(EXTRA_REMINDER_TIME, 0);
            clientReminderDate = intent.getStringExtra(EXTRA_CLIENT_REMINDER_DATE);
            clientReminderTime = intent.getStringExtra(EXTRA_CLIENT_REMINDER_TIME);
            dateAdded = intent.getStringExtra(EXTRA_DATE_ADDED);
            //edit message

            if (extraReminderSwitch == REMINDER_ON) {
                reminderSwitch.setChecked(true);
            }

            if (extraClientReminderSwitch == CLIENT_REMINDER_ON) {
                clientReminderSwitch.setChecked(true);
            }

            if (extraSms) {
                SMS_REMINDER = true;
            }

            if (extraEmail) {
                EMAIL_REMINDER = true;
            }

            if (extraBoth) {
                BOTH_TYPES = true;
            }

        } else {

            Objects.requireNonNull(getSupportActionBar()).setTitle("New appointment");
            intentHasExtraCode = false;
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

                    if (hourOfDay >= 12) {

                        @SuppressLint("DefaultLocale") String chosenTime = (String.format("%02d:%02d", hourOfDay, minute) + " PM");
                        timeEditText.setText(chosenTime);
                    } else {
                        @SuppressLint("DefaultLocale") String chosenTime = (String.format("%02d:%02d", hourOfDay, minute) + " AM");
                        timeEditText.setText(chosenTime);
                    }
                }
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, listener, hour, minute, false);
        timePickerDialog.show();
    }

    private void validateAndSubmitForm() {

        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String desc = descEditText.getText().toString();

        String rawDateTimeString = date + time;

        SimpleDateFormat oldDateTimeFormat = new SimpleDateFormat("MMM dd, yyyyHH:mm", Locale.getDefault());
        SimpleDateFormat newDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        String dateTimeString = null;
        try {
            dateTimeString = newDateTimeFormat.format(Objects.requireNonNull(oldDateTimeFormat.parse(rawDateTimeString)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean basicDataIsAvailable = false;
        boolean reminderDataIsAvailable = false;
        boolean clientReminderDataIsAvailable = false;

        if (!name.isEmpty() && !date.isEmpty() && !time.isEmpty()) {
            nameLayout.setErrorEnabled(false);
            dateLayout.setErrorEnabled(false);
            timeLayout.setErrorEnabled(false);

            basicDataIsAvailable = true;

        } else {
            if (name.isEmpty()) {
                nameLayout.setError("Name is required");
            }
            if (date.isEmpty()) {
                dateLayout.setError("Date is required");
            }
            if (time.isEmpty()) {
                timeLayout.setError("Time is required");
            }
        }

        if (basicDataIsAvailable && reminderState == REMINDER_ON) {
            if (reminderTime > 0) {
                reminderDataIsAvailable = true;
            }
        }

        if (basicDataIsAvailable && clientReminderState == CLIENT_REMINDER_ON) {

            if (SMS_REMINDER) {
                if (phone.isEmpty()) {
                    phoneEditText.setError("Client phone number is needed to send SMS reminder");
                } else {
                    phoneLayout.setErrorEnabled(false);
                    // TODO: schedule client reminder
                    clientReminderDataIsAvailable = true;

                }

            } else if (EMAIL_REMINDER) {
                if (email.isEmpty()) {
                    emailEditText.setError("Client email is needed to send email reminder");
                } else {
                    emailLayout.setErrorEnabled(false);
                    // TODO: schedule client reminder
                    clientReminderDataIsAvailable = true;
                }

            } else if (BOTH_TYPES) {
                if (email.isEmpty() || phone.isEmpty()) {
                    if (phone.isEmpty()) {
                        phoneEditText.setError("Client phone number is needed to send SMS reminder");

                    } else {
                        phoneLayout.setErrorEnabled(false);
                    }
                    if (email.isEmpty()) {
                        emailEditText.setError("Client email is needed to send email reminder");
                    } else {
                        emailLayout.setErrorEnabled(false);
                    }

                } else {
                    // TODO: schedule client reminder
                    clientReminderDataIsAvailable = true;
                }
            }
        }

        if (basicDataIsAvailable) {
            if (reminderState == REMINDER_ON && clientReminderState == CLIENT_REMINDER_ON) {
                if (reminderDataIsAvailable && clientReminderDataIsAvailable) {
                    addFormDataToIntent(name, phone, email, desc, date, time, reminderTime,
                            clientReminderDate, clientReminderTime, reminderState, clientReminderState, SMS_REMINDER, EMAIL_REMINDER, BOTH_TYPES, dateTimeString);
                }

            } else if (reminderState == REMINDER_ON && clientReminderState == CLIENT_REMINDER_OFF) {
                if (reminderDataIsAvailable) {
                    addFormDataToIntent(name, phone, email, desc, date, time, reminderTime,
                            clientReminderDate, clientReminderTime, reminderState, clientReminderState, SMS_REMINDER, EMAIL_REMINDER, BOTH_TYPES, dateTimeString);
                }

            } else if (reminderState == REMINDER_OFF && clientReminderState == CLIENT_REMINDER_ON) {
                if (clientReminderDataIsAvailable) {
                    addFormDataToIntent(name, phone, email, desc, date, time, reminderTime,
                            clientReminderDate, clientReminderTime, reminderState, clientReminderState, SMS_REMINDER, EMAIL_REMINDER, BOTH_TYPES, dateTimeString);
                }

            } else if (reminderState == REMINDER_OFF && clientReminderState == CLIENT_REMINDER_OFF) {
                addFormDataToIntent(name, phone, email, desc, date, time, reminderTime,
                        clientReminderDate, clientReminderTime, reminderState, clientReminderState, SMS_REMINDER, EMAIL_REMINDER, BOTH_TYPES, dateTimeString);
            }
        }
    }

    private void addFormDataToIntent(String name, String phone, String email, String desc, String date, String time, int reminderTime, String clientReminderDate,
                                     String clientReminderTime, int reminderState, int clientReminderState, boolean isSms, boolean isEmail, boolean isBoth, String dateTime) {

        if (intentHasExtraCode) {

            int intentExtraCode = getIntent().getIntExtra(EXTRA_INTENT_CODE, 0);

            if (intentExtraCode == 1) {

                Intent intent = new Intent(CreateEditAppointmentActivity.this, MainActivity.class);

                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                intent.putExtra("email", email);
                intent.putExtra("desc", desc);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("reminderTime", reminderTime);
                intent.putExtra("clientReminderDate", clientReminderDate);
                intent.putExtra("clientReminderTime", clientReminderTime);
                intent.putExtra("reminderState", reminderState);
                intent.putExtra("clientReminderState", clientReminderState);
                intent.putExtra("clientReminderMessage", "message");
                intent.putExtra("is_sms", isSms);
                intent.putExtra("is_email", isEmail);
                intent.putExtra("is_both", isBoth);
                intent.putExtra("dateTime", dateTime);
                intent.putExtra("dateAdded", dateAdded);

//                addAppointmentToCalendar();

                int id = getIntent().getIntExtra(EXTRA_ID, -1);
                if (id != -1) {
                    intent.putExtra(EXTRA_ID, id);
                }

                setResult(2, intent);
                finish();

            } else if (intentExtraCode == 2) {

                Intent intent = new Intent(CreateEditAppointmentActivity.this, AppointmentDetailsActivity.class);

                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                intent.putExtra("email", email);
                intent.putExtra("desc", desc);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("reminderTime", reminderTime);
                intent.putExtra("clientReminderDate", clientReminderDate);
                intent.putExtra("clientReminderTime", clientReminderTime);
                intent.putExtra("reminderState", reminderState);
                intent.putExtra("clientReminderState", clientReminderState);
                intent.putExtra("clientReminderMessage", "message");
                intent.putExtra("is_sms", isSms);
                intent.putExtra("is_email", isEmail);
                intent.putExtra("is_both", isBoth);
                intent.putExtra("dateTime", dateTime);
                intent.putExtra("dateAdded", dateAdded);

                addAppointmentToCalendar();

                int id = getIntent().getIntExtra(EXTRA_ID, -1);
                if (id != -1) {
                    intent.putExtra(EXTRA_ID, id);
                }

                setResult(2, intent);
                finish();
            }
        } else {

            Intent intent = new Intent(CreateEditAppointmentActivity.this, MainActivity.class);

            SimpleDateFormat format = new SimpleDateFormat("dd MMM", Locale.getDefault());
            Date today = new Date();

            String dateAdded = format.format(today);

            intent.putExtra("name", name);
            intent.putExtra("phone", phone);
            intent.putExtra("email", email);
            intent.putExtra("desc", desc);
            intent.putExtra("date", date);
            intent.putExtra("time", time);
            intent.putExtra("reminderTime", reminderTime);
            intent.putExtra("clientReminderDate", clientReminderDate);
            intent.putExtra("clientReminderTime", clientReminderTime);
            intent.putExtra("reminderState", reminderState);
            intent.putExtra("clientReminderState", clientReminderState);
            intent.putExtra("dateAdded", dateAdded);
            intent.putExtra("clientReminderMessage", "message");
            intent.putExtra("is_sms", isSms);
            intent.putExtra("is_email", isEmail);
            intent.putExtra("is_both", isBoth);
            intent.putExtra("dateTime", dateTime);

//            addAppointmentToCalendar();

            int id = getIntent().getIntExtra(EXTRA_ID, -1);
            if (id != -1) {
                intent.putExtra(EXTRA_ID, id);
            }

            setResult(2, intent);
            finish();
        }
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(imm).hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsGranted: permission granted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            checkForCalendarPermission();
        }
    }

    @AfterPermissionGranted(MY_PERMISSIONS_REQUEST_WRITE_CALENDAR)
    private void checkForCalendarPermission() {
        String[] perms = {Manifest.permission.WRITE_CALENDAR};

        if (EasyPermissions.hasPermissions(this, perms)) {
            Log.d(TAG, "onPermissionsGranted: permission granted");

        } else {
            EasyPermissions.requestPermissions(this, "Calendar permission is required to create an appointment.",
                    MY_PERMISSIONS_REQUEST_WRITE_CALENDAR, perms);
        }
    }

    private void addAppointmentToCalendar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        try {

            String eventTitle = nameEditText.getText().toString();
            String dateTimeString = dateEditText.getText().toString() + timeEditText.getText().toString();

            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyyHH:mm", Locale.getDefault());
            Date date = format.parse(dateTimeString);

            long dateTimeInMillis = Objects.requireNonNull(date).getTime();

            Calendar beginTime = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();

            beginTime.setTimeInMillis(dateTimeInMillis);

            ContentValues event = new ContentValues();

            event.put(CalendarContract.Events.CALENDAR_ID, 1);
            event.put(CalendarContract.Events.TITLE, eventTitle);
            event.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
            event.put(CalendarContract.Events.DTEND, endCalendar.getTimeInMillis());
            event.put(CalendarContract.Events.HAS_ALARM, true);
            event.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC");

            Uri uri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, event);

            if (reminderState == REMINDER_ON) {

                Long eventId = Long.parseLong(Objects.requireNonNull(uri).getLastPathSegment());

                ContentValues reminder = new ContentValues();

                reminder.put(CalendarContract.Reminders.EVENT_ID, eventId);
                reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                reminder.put(CalendarContract.Reminders.MINUTES, reminderTime);

                getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, reminder);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleClientReminder() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        initSwitches();
    }
}
