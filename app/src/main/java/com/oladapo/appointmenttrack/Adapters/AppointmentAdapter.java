package com.oladapo.appointmenttrack.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.oladapo.appointmenttrack.Database.Appointments;
import com.oladapo.appointmenttrack.R;

public class AppointmentAdapter extends ListAdapter<Appointments, AppointmentAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public AppointmentAdapter() {
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
                .inflate(R.layout.item_upcoming_recyclerview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointments currentAppointments = getItem(position);

        String dateTime = currentAppointments.getDate() + " at " + currentAppointments.getTime();

        holder.clientName.setText(currentAppointments.getClientName());
        holder.dateAndTime.setText(dateTime);
        holder.dateAdded.setText(currentAppointments.getDateAdded());
    }

    public Appointments getAppointAt(int position) {
        return getItem(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView clientName;
        private TextView dateAndTime;
        private TextView dateAdded;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            clientName = itemView.findViewById(R.id.clientName);
            dateAndTime = itemView.findViewById(R.id.date_and_time);
            dateAdded = itemView.findViewById(R.id.dateAdded);

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

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}
