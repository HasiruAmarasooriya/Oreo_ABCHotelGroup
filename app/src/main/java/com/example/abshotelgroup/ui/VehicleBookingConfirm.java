package com.example.abshotelgroup.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.abshotelgroup.R;
import com.example.abshotelgroup.model.VehicleBookingEnt;
import com.example.abshotelgroup.util.AbcHotelApp;
import com.example.abshotelgroup.util.DBHelper;
import com.example.abshotelgroup.util.DialogUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

public class VehicleBookingConfirm extends AppCompatActivity {
    private static final String TAG = "VehicleBookingConfirm";
    Button button, button2;
    DBHelper mMyDB;

    private ProgressDialog mLoadingBar;
    private Handler handler;

    private Integer lastBookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_booking_confirm);

        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);

        mMyDB = new DBHelper(this);
        handler = new Handler(Looper.getMainLooper());

        button.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), VehicleBookingForm.class);
            startActivity(intent);
        });

        button2.setOnClickListener(view -> {
            fetchLastBooking();
        });
    }

    private void fetchLastBooking() {
        Log.d(TAG, "<----- Execute Fetch Last Vehicle Booking ----->");
        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Fetch Last Vehicle Booking");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_con);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase readDb = mMyDB.getReadableDatabase(); Cursor cursor = readDb.rawQuery("SELECT * FROM vehicle_packages WHERE id= (SELECT MAX(id)\n" +
                    "FROM vehicle_packages WHERE customer_id=?)", new String[]{AbcHotelApp.getLogedUser().getUsername()})) {
                Thread.sleep(1000);
                if (cursor.moveToFirst()) {
                    VehicleBookingEnt vehicleBookingEnt = new VehicleBookingEnt(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("nic")),
                            cursor.getString(cursor.getColumnIndexOrThrow("bok_type")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("num_of_days")),
                            AbcHotelApp.getLogedUser()
                    );

                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        setData(vehicleBookingEnt);
                    });
                } else {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        DialogUtil.showAlert(this, "Fetch Last Vehicle Booking", "Couldn't Find Any Vehicle Booking For Cancel!", R.drawable.ic_baseline_error_24);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "####### Fetch Last Vehicle Booking Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Fetch Last Vehicle Booking", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
        });
    }

    private void setData(final VehicleBookingEnt vehicleBookingEnt) {
        if (vehicleBookingEnt != null) {
            this.lastBookingId = vehicleBookingEnt.getId();

            String msg = "Are You Confirm Cancel Last Vehicle Booking ?\n";
            msg = msg.concat("NIC :").concat(vehicleBookingEnt.getNic()).concat("\n")
                    .concat("Num Of Day/s :").concat(String.format(Locale.getDefault(), "%03d", vehicleBookingEnt.getNumOfDays())).concat("\n")
                    .concat("Package type :").concat(vehicleBookingEnt.getVehType());

            new MaterialAlertDialogBuilder(this, R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog).setTitle("Cancel Last Vehicle Booking")
                    .setMessage(msg)
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_delete)
                    .setPositiveButton("YES", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        if (lastBookingId != null)
                            confirmDelete();
                        else
                            DialogUtil.showAlert(this, "Cancel Last Booking", "Couldn't Find Any Vehicle Booking For Cancel !", R.drawable.ic_delete);

                    })
                    .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setNeutralButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        }
    }

    private void confirmDelete() {
        Log.d(TAG, "<----- Execute Confirm Cancel Vehicle Booking ---->");

        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Cancel Last Vehicle Booking");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_delete);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase writeDb = mMyDB.getWritableDatabase()) {
                Thread.sleep(1000);

                String whereClause = "id=?";
                String[] whereArgs = {lastBookingId.toString()};
                int items = writeDb.delete(DBHelper.TABLEVEHICLE, whereClause, whereArgs);

                if (items > 0) {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        Toast.makeText(VehicleBookingConfirm.this, "Last Vehicle Booking Cancel Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Homepage.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        DialogUtil.showAlert(this, "Cancel Last Vehicle Booking", "Last Vehicle Booking Cancel Failed !", R.drawable.ic_baseline_error_24);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "####### Cancel Last Vehicle Booking Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Cancel Last Vehicle Booking", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMyDB != null)
            mMyDB.close();
    }
}