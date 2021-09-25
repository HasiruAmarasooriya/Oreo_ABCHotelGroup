package com.example.abshotelgroup.ui;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abshotelgroup.R;
import com.example.abshotelgroup.adapter.BookingAdapter;
import com.example.abshotelgroup.model.BookingEnt;
import com.example.abshotelgroup.util.AbcHotelApp;
import com.example.abshotelgroup.util.DBHelper;
import com.example.abshotelgroup.util.DialogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomBookingActivity extends AppCompatActivity {
    private static final String TAG = "RoomBookingActivity";

    private RecyclerView mRecyclerView;
    private BookingAdapter mBookingAdapter;

    private ProgressDialog mLoadingBar;
    private Handler handler;

    private DBHelper mMyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking);

        mRecyclerView = findViewById(R.id.room_book_rec_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayout);

        mMyDB = new DBHelper(this);
        handler = new Handler(Looper.getMainLooper());

        fetchBookingHistory();
    }

    private void fetchBookingHistory() {
        Log.d(TAG, "<----- Execute Fetch  Booking History ----->");
        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Fetch Booking History");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_con);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase readDb = mMyDB.getReadableDatabase(); Cursor cursor = readDb.rawQuery("SELECT * FROM room_packages WHERE customer_id=?", new String[]{AbcHotelApp.getLogedUser().getUsername()})) {
                Thread.sleep(1000);
                List<BookingEnt> bookingEnts = new ArrayList<>();
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        BookingEnt bookingEnt = new BookingEnt(
                                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("nic")),
                                new SimpleDateFormat("yy/MM/dd", Locale.getDefault()).parse(cursor.getString(cursor.getColumnIndexOrThrow("bok_date"))),
                                cursor.getInt(cursor.getColumnIndexOrThrow("num_of_rom")),
                                cursor.getString(cursor.getColumnIndexOrThrow("bok_type")),
                                null
                        );
                        bookingEnts.add(bookingEnt);
                    }

                }
                if (bookingEnts.size() > 0) {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        setData(bookingEnts);
                    });
                } else {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        DialogUtil.showAlert(this, "Fetch Booking History", "Couldn't Find Any Booking!", R.drawable.ic_baseline_error_24);
                    });
                }
            } catch (ParseException pe) {
                Log.e(TAG, "######### Fetch Booking History Date Parce Error #########", pe);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Fetch Booking History", "Invalid Date Format\n" + pe.getMessage(), R.drawable.ic_baseline_error_24);
                });
            } catch (Exception e) {
                Log.e(TAG, "####### Fetch Booking History Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Fetch Booking History", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
        });
    }

    private void setData(final List<BookingEnt> bookingEnts) {
        mBookingAdapter = new BookingAdapter(bookingEnts, this);
        mRecyclerView.setAdapter(mBookingAdapter);
        mBookingAdapter.setBookingEnts(bookingEnts);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMyDB != null)
            mMyDB.close();
    }
}