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
import com.example.abshotelgroup.adapter.VehBookingAdapter;
import com.example.abshotelgroup.model.VehicleBookingEnt;
import com.example.abshotelgroup.util.AbcHotelApp;
import com.example.abshotelgroup.util.DBHelper;
import com.example.abshotelgroup.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;

public class VehBookingViewActivity extends AppCompatActivity {
    private static final String TAG = "VehBookingViewActivity";

    private RecyclerView mRecyclerView;
    private VehBookingAdapter mVehBookingAdapter;

    private ProgressDialog mLoadingBar;
    private Handler handler;

    private DBHelper mMyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veh_booking_view);

        mRecyclerView = findViewById(R.id.veh_book_rec_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayout);

        mMyDB = new DBHelper(this);
        handler = new Handler(Looper.getMainLooper());

        fetchBookingHistory();
    }

    private void fetchBookingHistory() {
        Log.d(TAG, "<----- Execute Fetch Vehicle Booking History ----->");
        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Fetch Vehicle Booking History");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_con);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase readDb = mMyDB.getReadableDatabase(); Cursor cursor = readDb.rawQuery("SELECT * FROM vehicle_packages WHERE customer_id=?", new String[]{AbcHotelApp.getLogedUser().getUsername()})) {
                Thread.sleep(1000);
                List<VehicleBookingEnt> vehicleBookingEnts = new ArrayList<>();
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        VehicleBookingEnt vehicleBookingEnt = new VehicleBookingEnt(
                                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("nic")),
                                cursor.getString(cursor.getColumnIndexOrThrow("bok_type")),
                                cursor.getInt(cursor.getColumnIndexOrThrow("num_of_days")),
                                null
                        );
                        vehicleBookingEnts.add(vehicleBookingEnt);
                    }

                }
                if (vehicleBookingEnts.size() > 0) {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        setData(vehicleBookingEnts);
                    });
                } else {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        DialogUtil.showAlert(this, "Fetch Vehicle Booking History", "Couldn't Find Any Vehicle Booking!", R.drawable.ic_baseline_error_24);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "####### Fetch Vehicle Booking History Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Fetch Vehicle Booking History", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
        });
    }

    private void setData(final List<VehicleBookingEnt> vehicleBookingEnts) {
        mVehBookingAdapter = new VehBookingAdapter(vehicleBookingEnts, this);
        mRecyclerView.setAdapter(mVehBookingAdapter);
        mVehBookingAdapter.setVehicleBookingEnts(vehicleBookingEnts);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMyDB != null)
            mMyDB.close();
    }
}