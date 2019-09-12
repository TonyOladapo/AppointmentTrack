package com.oladapo.appointmenttrack.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.oladapo.appointmenttrack.Adapters.RemindersAdapter;
import com.oladapo.appointmenttrack.Database.Appointments;
import com.oladapo.appointmenttrack.Database.ViewModel;
import com.oladapo.appointmenttrack.R;

import java.util.List;
import java.util.Objects;

public class RemindersFragment extends Fragment {

    private ViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RemindersAdapter adapter = new RemindersAdapter();

        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewModel.class);
        viewModel.getAllAppointments().observe(getViewLifecycleOwner(), new Observer<List<Appointments>>() {
            @Override
            public void onChanged(List<Appointments> appointments) {
                adapter.submitList(appointments);

                //TODO: set no reminders textView visible
            }
        });

        adapter.setOnItemClickListener(new RemindersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Appointments appointments) {
                //TODO: start reminder details intent
            }
        });

        adapter.setOnItemLongClickListener(new RemindersAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Appointments appointments) {
                //TODO: start reminder dialog
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}
