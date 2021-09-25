package com.example.abshotelgroup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abshotelgroup.R;
import com.example.abshotelgroup.model.VehicleBookingEnt;
import com.example.abshotelgroup.util.AbcHotelConstans;

import java.util.List;
import java.util.Locale;

public class VehBookingAdapter extends RecyclerView.Adapter<VehBookingAdapter.VehicleViewHolder> {
    private List<VehicleBookingEnt> vehicleBookingEnts;
    private Context context;

    public VehBookingAdapter(List<VehicleBookingEnt> vehicleBookingEnts, Context context) {
        this.vehicleBookingEnts = vehicleBookingEnts;
        this.context = context;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vehicleView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.veh_bok_items, parent, false);
        return new VehicleViewHolder(vehicleView);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        VehicleBookingEnt vehicleBookingEnt = vehicleBookingEnts.get(position);
        if (vehicleBookingEnt != null) {
            holder.txtNic.setText("NIC : ".concat(vehicleBookingEnt.getNic()));
            holder.txtNumOfDays.setText("Num-Of Days : ".concat(String.format(Locale.getDefault(), "%03d", vehicleBookingEnt.getNumOfDays())));
            holder.txtBookType.setText("Vehicle Type : ".concat(vehicleBookingEnt.getVehType()));

            if (vehicleBookingEnt.getVehType().equalsIgnoreCase(AbcHotelConstans.VAN_BOOK))
                holder.imgBook.setImageResource(R.drawable.ic_van);
            else if (vehicleBookingEnt.getVehType().equalsIgnoreCase(AbcHotelConstans.BUS_BOOK))
                holder.imgBook.setImageResource(R.drawable.ic_bus);
            else if (vehicleBookingEnt.getVehType().equalsIgnoreCase(AbcHotelConstans.CAR_BOOK))
                holder.imgBook.setImageResource(R.drawable.ic_car);
            else
                holder.imgBook.setImageResource(R.drawable.no_image);
        }
    }

    @Override
    public int getItemCount() {
        if (vehicleBookingEnts == null)
            return 0;
        return vehicleBookingEnts.size();
    }

    public void setVehicleBookingEnts(List<VehicleBookingEnt> vehicleBookingEnts) {
        this.vehicleBookingEnts = vehicleBookingEnts;
        notifyDataSetChanged();
    }

    public class VehicleViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgBook;
        private final TextView txtNic;
        private final TextView txtNumOfDays;
        private final TextView txtBookType;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBook = itemView.findViewById(R.id.img_veh);
            txtNic = itemView.findViewById(R.id.txt_nic);
            txtNumOfDays = itemView.findViewById(R.id.txt_num_of_days);
            txtBookType = itemView.findViewById(R.id.txt_veh_type);

        }
    }
}