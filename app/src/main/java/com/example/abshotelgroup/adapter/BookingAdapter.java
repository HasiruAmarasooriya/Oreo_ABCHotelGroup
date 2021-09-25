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
import com.example.abshotelgroup.model.BookingEnt;
import com.example.abshotelgroup.util.AbcHotelConstans;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<BookingEnt> bookingEnts;
    private final SimpleDateFormat dateFormat;
    private Context context;

    public BookingAdapter(List<BookingEnt> bookingEnts, Context context) {
        this.bookingEnts = bookingEnts;
        this.context = context;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View bookingView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_bok_items, parent, false);
        return new BookingViewHolder(bookingView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingEnt bookingEnt = bookingEnts.get(position);
        if (bookingEnt != null) {
            holder.txtNic.setText("NIC : ".concat(bookingEnt.getNic()));
            holder.txtNumOfRooms.setText("Num-Of Rooms : ".concat(String.format(Locale.getDefault(), "%03d", bookingEnt.getNumOfRooms())));
            holder.txtBookDate.setText("Booking Date :".concat(dateFormat.format(bookingEnt.getBookDate())));
            holder.txtBookType.setText("Booking Type : ".concat(bookingEnt.getBookType()));

            if (bookingEnt.getBookType().equalsIgnoreCase(AbcHotelConstans.FULL_BOARD_BOOK))
                holder.imgBook.setImageResource(R.drawable.fully_book_img);
            else if (bookingEnt.getBookType().equalsIgnoreCase(AbcHotelConstans.HALF_BOARD_BOOK))
                holder.imgBook.setImageResource(R.drawable.half_book_img);
            else
                holder.imgBook.setImageResource(R.drawable.no_image);
        }
    }

    @Override
    public int getItemCount() {
        if (bookingEnts == null)
            return 0;
        return bookingEnts.size();
    }

    public void setBookingEnts(List<BookingEnt> bookingEnts) {
        this.bookingEnts = bookingEnts;
        notifyDataSetChanged();
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgBook;
        private final TextView txtNic;
        private final TextView txtNumOfRooms;
        private final TextView txtBookDate;
        private final TextView txtBookType;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBook = itemView.findViewById(R.id.img_book);
            txtNic = itemView.findViewById(R.id.txt_nic);
            txtNumOfRooms = itemView.findViewById(R.id.txt_num_of_room);
            txtBookDate = itemView.findViewById(R.id.txt_book_date);
            txtBookType = itemView.findViewById(R.id.txt_book_type);

        }
    }
}