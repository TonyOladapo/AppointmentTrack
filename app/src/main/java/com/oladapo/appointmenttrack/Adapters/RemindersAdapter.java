package com.oladapo.appointmenttrack.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.oladapo.appointmenttrack.Database.Appointments;
import com.oladapo.appointmenttrack.R;

public class RemindersAdapter extends ListAdapter<Appointments, RemindersAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public RemindersAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Appointments> DIFF_CALLBACK = new DiffUtil.ItemCallback<Appointments>() {
        @Override
        public boolean areItemsTheSame(@NonNull Appointments oldItem, @NonNull Appointments newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Appointments oldItem, @NonNull Appointments newItem) {
            return oldItem.getClientName().equals(newItem.getClientName()) &&
                    oldItem.getDate().equals(newItem.getDate()) &&
                    oldItem.getTime().equals(newItem.getTime());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminders_recycler_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointments currentAppointments = getItem(position);


    }

    public Appointments getAppointmentAt(int position) {
        return getItem(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        longClickListener.onItemLongClick(getItem(position));
                    }
                    return true;
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Appointments appointments);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Appointments appointments);
    }

    public void setOnItemClickListener(RemindersAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(RemindersAdapter.OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}
