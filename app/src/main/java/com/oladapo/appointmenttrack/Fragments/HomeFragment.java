package com.oladapo.appointmenttrack.Fragments;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class HomeFragment extends Fragment {

    private ViewModel viewModel;

    private static final int RC_ADD_CLIENT = 2;
    private static final int RC_APPOINTMENT_DETAILS = 3;

    private ConstraintLayout constraintLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateEditAppointmentActivity.class);
                startActivityForResult(intent, RC_ADD_CLIENT);
            }
        });

        constraintLayout = view.findViewById(R.id.homeLayout);

        final AppointmentAdapter adapter = new AppointmentAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.home_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));

        recyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModel.class);
        viewModel.getAllAppointments().observe(getViewLifecycleOwner(), new Observer<List<Appointments>>() {
            @Override
            public void onChanged(List<Appointments> appointments) {
                adapter.submitList(appointments);
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

                startActivityForResult(intent, RC_APPOINTMENT_DETAILS);
            }
        });

        adapter.setOnItemLongClickListener(new AppointmentAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Appointments appointments) {
                new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                        .show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_ADD_CLIENT && resultCode == 2) {

            String name = data.getStringExtra("name");
            String phone = data.getStringExtra("phone");
            String email = data.getStringExtra("email");
            String desc = data.getStringExtra("desc");
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");
            int reminderTime = data.getIntExtra("reminderTime", 0);
            String clientReminderDate = data.getStringExtra("clientReminderDate");
            String clientReminderTime = data.getStringExtra("clientReminderTime");
            int reminderState = data.getIntExtra("reminderState", 0);
            int clientReminderState = data.getIntExtra("clientReminderState", 0);
            String reminderMessage = "message";
            String dateAdded = data.getStringExtra("dateAdded");
            boolean isSms = data.getBooleanExtra("is_sms", false);
            boolean isEmail = data.getBooleanExtra("is_email", false);
            boolean isBoth = data.getBooleanExtra("is_both", false);
            String dateTime = data.getStringExtra("dateTime");

            Appointments appointments = new Appointments(name, phone, email, desc, date, time, reminderTime, reminderState, clientReminderState,
                    clientReminderDate, clientReminderTime, reminderMessage, dateAdded, isSms, isEmail, isBoth, dateTime);

            viewModel.insert(appointments);

            Snackbar.make(constraintLayout, "Appointment added", Snackbar.LENGTH_LONG).show();
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
            Snackbar.make(constraintLayout, "All appointments deleted", Snackbar.LENGTH_LONG)
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
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModel.class);
            viewModel.getAllAppointments().observe(getViewLifecycleOwner(), new Observer<List<Appointments>>() {
                @Override
                public void onChanged(List<Appointments> appointments) {
                    mAdapter.submitList(appointments);
                }
            });

            Snackbar.make(constraintLayout, "Appointment deleted", Snackbar.LENGTH_LONG)
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
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).setTitle("AppointmentTrack");
    }
}