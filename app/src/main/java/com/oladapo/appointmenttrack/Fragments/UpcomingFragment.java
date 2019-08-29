package com.oladapo.appointmenttrack.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.oladapo.appointmenttrack.Activities.AppointmentDetailsActivity;
import com.oladapo.appointmenttrack.Activities.CreateEditAppointmentActivity;
import com.oladapo.appointmenttrack.Adapters.AppointmentAdapter;
import com.oladapo.appointmenttrack.Database.Appointments;
import com.oladapo.appointmenttrack.Database.ViewModel;
import com.oladapo.appointmenttrack.R;

import java.util.List;
import java.util.Objects;

public class UpcomingFragment extends Fragment {

    private ViewModel viewModel;

    private static final int RC_ADD_CLIENT = 2;
    private static final int RC_APPOINTMENT_DETAILS = 3;
    private static final int RC_EDIT_APPOINTMENT = 5;
    private static final int RC_RENEW_APPOINTMENT = 6;

    private CoordinatorLayout coordinatorLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upcoming, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateEditAppointmentActivity.class);
                startActivityForResult(intent, RC_ADD_CLIENT);
            }
        });

        coordinatorLayout = view.findViewById(R.id.upcomingLayout);

        final TextView noAppointmentsTextView = view.findViewById(R.id.no_appointments);

        final AppointmentAdapter adapter = new AppointmentAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.home_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    if (fab.isShown()) {
                        fab.hide();
                    }
                } else if (dy < 0){
                    if (!fab.isShown()) {
                        fab.show();
                    }
                }
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));

        recyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModel.class);
        viewModel.getUpcomingAppointments().observe(getViewLifecycleOwner(), new Observer<List<Appointments>>() {
            @Override
            public void onChanged(List<Appointments> appointments) {
                adapter.submitList(appointments);

                if (appointments.size() < 1) {
                    noAppointmentsTextView.setVisibility(View.VISIBLE);
                } else {
                    noAppointmentsTextView.setVisibility(View.GONE);
                }
            }
        });

        adapter.setOnItemClickListener(new AppointmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Appointments appointments) {

                Intent intent = new Intent(getContext(), AppointmentDetailsActivity.class);

                intent.putExtra(AppointmentDetailsActivity.EXTRA_ID, appointments.getId());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_CLIENT_NAME, appointments.getClientName());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_PHONE, appointments.getClientPhone());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_EMAIL, appointments.getClientEmail());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_DATE, appointments.getDate());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_TIME, appointments.getTime());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_DESC, appointments.getDescription());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_REMINDER_TIME, appointments.getReminderTime());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_CLIENT_REMINDER_DATE, appointments.getClientReminderDate());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_CLIENT_REMINDER_TIME, appointments.getClientReminderTime());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_CLIENT_REMINDER_MESSAGE, appointments.getClientReminderMessage());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_REMINDER_STATE, appointments.getReminderState());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_CLIENT_REMINDER_STATE, appointments.getClientReminderState());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_SMS_REMINDER, appointments.isSms());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_EMAIL_REMINDER, appointments.isEmail());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_BOTH, appointments.isBoth());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_DATE_TIME, appointments.getDateTime());
                intent.putExtra(AppointmentDetailsActivity.EXTRA_DATE_ADDED, appointments.getDateAdded());

                startActivityForResult(intent, RC_APPOINTMENT_DETAILS);
            }
        });

        adapter.setOnItemLongClickListener(new AppointmentAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Appointments appointments) {
                longClickAlertDialog(appointments);
            }
        });
    }

    private void longClickAlertDialog(final Appointments appointments) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));

        String[] options = {"Renew", "Edit", "Delete"};

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        renewAppointment(appointments);
                        break;

                    case 1:
                        editAppointment(appointments);
                        break;

                    case 2:
                        deleteAppointment(appointments);
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void editAppointment(Appointments appointments) {
        Intent intent = new Intent(getContext(), CreateEditAppointmentActivity.class);

        intent.putExtra(CreateEditAppointmentActivity.EXTRA_INTENT_CODE, 1);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_ID, appointments.getId());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_NAME, appointments.getClientName());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_PHONE, appointments.getClientPhone());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_EMAIL, appointments.getClientEmail());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_DATE, appointments.getDate());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_TIME, appointments.getTime());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_DESC, appointments.getDescription());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_REMINDER_TIME, appointments.getReminderTime());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_DATE, appointments.getClientReminderDate());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_TIME, appointments.getClientReminderTime());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_MESSAGE, appointments.getClientReminderMessage());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_REMINDER_STATE, appointments.getReminderState());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_STATE, appointments.getClientReminderState());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_SMS_REMINDER, appointments.isSms());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_EMAIL_REMINDER, appointments.isEmail());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_BOTH, appointments.isBoth());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_DATE_TIME, appointments.getDateTime());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_DATE_ADDED, appointments.getDateAdded());

        startActivityForResult(intent, RC_EDIT_APPOINTMENT);
    }

    private void deleteAppointment(Appointments appointments) {
        Snackbar.make(coordinatorLayout, "Appointment deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorPrimaryDark))
                .show();
        viewModel.delete(appointments);
    }

    private void renewAppointment(Appointments appointments) {
        Intent intent = new Intent(getContext(), CreateEditAppointmentActivity.class);

        intent.putExtra(CreateEditAppointmentActivity.EXTRA_INTENT_CODE, 3);
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_ID, appointments.getId());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_NAME, appointments.getClientName());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_PHONE, appointments.getClientPhone());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_EMAIL, appointments.getClientEmail());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_DATE, appointments.getDate());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_TIME, appointments.getTime());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_DESC, appointments.getDescription());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_REMINDER_TIME, appointments.getReminderTime());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_DATE, appointments.getClientReminderDate());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_TIME, appointments.getClientReminderTime());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_MESSAGE, appointments.getClientReminderMessage());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_REMINDER_STATE, appointments.getReminderState());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_CLIENT_REMINDER_STATE, appointments.getClientReminderState());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_SMS_REMINDER, appointments.isSms());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_EMAIL_REMINDER, appointments.isEmail());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_BOTH, appointments.isBoth());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_DATE_TIME, appointments.getDateTime());
        intent.putExtra(CreateEditAppointmentActivity.EXTRA_DATE_ADDED, appointments.getDateAdded());

        startActivityForResult(intent, RC_RENEW_APPOINTMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_ADD_CLIENT && resultCode == 2) {

            int reminderTime = data.getIntExtra("reminderTime", 0);
            int reminderState = data.getIntExtra("reminderState", 0);
            int clientReminderState = data.getIntExtra("clientReminderState", 0);

            String name = data.getStringExtra("name");
            String phone = data.getStringExtra("phone");
            String email = data.getStringExtra("email");
            String desc = data.getStringExtra("desc");
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");
            String clientReminderDate = data.getStringExtra("clientReminderDate");
            String clientReminderTime = data.getStringExtra("clientReminderTime");
            String reminderMessage = data.getStringExtra("clientReminderMessage");
            String dateTime = data.getStringExtra("dateTime");
            String dateAdded = data.getStringExtra("dateAdded");

            boolean isSms = data.getBooleanExtra("is_sms", false);
            boolean isEmail = data.getBooleanExtra("is_email", false);
            boolean isBoth = data.getBooleanExtra("is_both", false);

            Appointments appointments = new Appointments(name, phone, email, desc, date, time, reminderTime, reminderState, clientReminderState,
                    clientReminderDate, clientReminderTime, reminderMessage, dateAdded, isSms, isEmail, isBoth, dateTime);

            viewModel.insert(appointments);

            Snackbar.make(coordinatorLayout, "Appointment added", Snackbar.LENGTH_LONG).show();

        } else if (requestCode == RC_EDIT_APPOINTMENT && resultCode == 2) {

            int id = data.getIntExtra("id", -1);
            int reminderTime = data.getIntExtra("reminderTime", 0);
            int reminderState = data.getIntExtra("reminderState", 0);
            int clientReminderState = data.getIntExtra("clientReminderState", 0);

            String name = data.getStringExtra("name");
            String phone = data.getStringExtra("phone");
            String email = data.getStringExtra("email");
            String desc = data.getStringExtra("desc");
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");
            String clientReminderDate = data.getStringExtra("clientReminderDate");
            String clientReminderTime = data.getStringExtra("clientReminderTime");
            String reminderMessage = data.getStringExtra("clientReminderMessage");
            String dateTime = data.getStringExtra("dateTime");
            String dateAdded = data.getStringExtra("dateAdded");

            boolean isSms = data.getBooleanExtra("is_sms", false);
            boolean isEmail = data.getBooleanExtra("is_email", false);
            boolean isBoth = data.getBooleanExtra("is_both", false);

            Appointments appointments = new Appointments(name, phone, email, desc, date, time, reminderTime, reminderState, clientReminderState,
                    clientReminderDate, clientReminderTime, reminderMessage, dateAdded, isSms, isEmail, isBoth, dateTime);

            appointments.setId(id);

            viewModel.update(appointments);

            Snackbar.make(coordinatorLayout, "Appointment updated", Snackbar.LENGTH_LONG).show();

        } else if (requestCode == RC_RENEW_APPOINTMENT && resultCode == 2) {

            int reminderTime = data.getIntExtra("reminderTime", 0);
            int reminderState = data.getIntExtra("reminderState", 0);
            int clientReminderState = data.getIntExtra("clientReminderState", 0);

            String name = data.getStringExtra("name");
            String phone = data.getStringExtra("phone");
            String email = data.getStringExtra("email");
            String desc = data.getStringExtra("desc");
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");
            String clientReminderDate = data.getStringExtra("clientReminderDate");
            String clientReminderTime = data.getStringExtra("clientReminderTime");
            String reminderMessage = data.getStringExtra("clientReminderMessage");
            String dateTime = data.getStringExtra("dateTime");
            String dateAdded = data.getStringExtra("dateAdded");

            boolean isSms = data.getBooleanExtra("is_sms", false);
            boolean isEmail = data.getBooleanExtra("is_email", false);
            boolean isBoth = data.getBooleanExtra("is_both", false);

            Appointments appointments = new Appointments(name, phone, email, desc, date, time, reminderTime, reminderState, clientReminderState,
                    clientReminderDate, clientReminderTime, reminderMessage, dateAdded, isSms, isEmail, isBoth, dateTime);

            viewModel.insert(appointments);

            Snackbar.make(coordinatorLayout, "Appointment added", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deleteAllNotes) {
            viewModel.deleteAllAppointments();
            Snackbar.make(coordinatorLayout, "All appointments deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //undo delete
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.colorPrimaryDark))
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SwipeToDeleteCallback extends
            ItemTouchHelper.SimpleCallback {

        private AppointmentAdapter mAdapter;
        private ViewModel viewModel;
        private Drawable icon;
        private final ColorDrawable background;

        SwipeToDeleteCallback(AppointmentAdapter adapter) {
            super(0,ItemTouchHelper.LEFT);
            mAdapter = adapter;
            icon = ContextCompat.getDrawable(Objects.requireNonNull(getContext()),
                    R.drawable.ic_delete_white_24dp);
            background = new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark));
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModel.class);
            viewModel.getUpcomingAppointments().observe(getViewLifecycleOwner(), new Observer<List<Appointments>>() {
                @Override
                public void onChanged(List<Appointments> appointments) {
                    mAdapter.submitList(appointments);
                }
            });

            Snackbar.make(coordinatorLayout, "Appointment deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.colorPrimaryDark))
                        .show();
            viewModel.delete(mAdapter.getAppointAt(viewHolder.getAdapterPosition()));
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (dX < 0) { // Swiping to the left
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
            }

            background.draw(c);
            icon.draw(c);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}