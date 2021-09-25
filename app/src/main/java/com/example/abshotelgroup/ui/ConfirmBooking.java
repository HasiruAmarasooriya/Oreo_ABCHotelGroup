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
import com.example.abshotelgroup.model.BookingEnt;
import com.example.abshotelgroup.util.AbcHotelApp;
import com.example.abshotelgroup.util.DBHelper;
import com.example.abshotelgroup.util.DialogUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ConfirmBooking extends AppCompatActivity {
    private static final String TAG = "ConfirmBooking";

    Button button, button2;
    DBHelper mMyDB;
    private ProgressDialog mLoadingBar;
    private Handler handler;

    private Integer lastBookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);

        mMyDB = new DBHelper(this);
        handler = new Handler(Looper.getMainLooper());

        button.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity4.class);
            startActivity(intent);
        });

        button2.setOnClickListener(view -> fetchLastBooking());
    }

    private void fetchLastBooking() {
        Log.d(TAG, "<----- Execute Fetch Last Booking ----->");
        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Fetch Last Booking");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_con);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase readDb = mMyDB.getReadableDatabase(); Cursor cursor = readDb.rawQuery("SELECT * FROM room_packages WHERE id= (SELECT MAX(id)\n" +
                    "FROM room_packages WHERE customer_id=?)", new String[]{AbcHotelApp.getLogedUser().getUsername()})) {
                Thread.sleep(2000);
                if (cursor.moveToFirst()) {
                    BookingEnt bookingEnt = new BookingEnt(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("nic")),
                            new SimpleDateFormat("yy/MM/dd", Locale.getDefault()).parse(cursor.getString(cursor.getColumnIndexOrThrow("bok_date"))),
                            cursor.getInt(cursor.getColumnIndexOrThrow("num_of_rom")),
                            cursor.getString(cursor.getColumnIndexOrThrow("bok_type")),
                            AbcHotelApp.getLogedUser()
                    );

                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        setData(bookingEnt);
                    });
                } else {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        DialogUtil.showAlert(this, "Fetch Last Booking", "Couldn't Find Any Booking For Cancel!", R.drawable.ic_baseline_error_24);
                    });
                }
            } catch (ParseException pe) {
                Log.e(TAG, "######### Fetch Last Booking Date Parce Error #########", pe);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Fetch Last Booking", "Invalid Date Format\n" + pe.getMessage(), R.drawable.ic_baseline_error_24);
                });
            } catch (Exception e) {
                Log.e(TAG, "####### Fetch Last Booking Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Fetch Last Booking", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
        });
    }

    private void setData(final BookingEnt bookingEnt) {
        if (bookingEnt != null) {
            this.lastBookingId = bookingEnt.getId();

            String msg = "Are You Confirm Cancel Last Booking ?\n";
            msg = msg.concat("NIC :").concat(bookingEnt.getNic()).concat("\n")
                    .concat("Booking Date :").concat(new SimpleDateFormat("yy/MM/dd", Locale.getDefault()).format(bookingEnt.getBookDate())).concat("\n")
                    .concat("Num Of Room/s :").concat(String.format(Locale.getDefault(), "%03d", bookingEnt.getNumOfRooms())).concat("\n")
                    .concat("Package type :").concat(bookingEnt.getBookType());

            new MaterialAlertDialogBuilder(this, R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog).setTitle("Cancel Last Booking")
                    .setMessage(msg)
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_delete)
                    .setPositiveButton("YES", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        if (lastBookingId != null)
                            confirmDelete();
                        else
                            DialogUtil.showAlert(this, "Cancel Last Booking", "Couldn't Find Any Booking For Cancel !", R.drawable.ic_delete);

                    })
                    .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setNeutralButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        }
    }

    private void confirmDelete() {
        Log.d(TAG, "<----- Execute Confirm Cancel Booking ---->");

        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Cancel Last Booking");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_delete);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase writeDb = mMyDB.getWritableDatabase()) {
                Thread.sleep(2000);

                String whereClause = "id=?";
                String[] whereArgs = {lastBookingId.toString()};
                int items = writeDb.delete(DBHelper.TABLE_ROOM, whereClause, whereArgs);

                if (items > 0) {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        Toast.makeText(ConfirmBooking.this, "Last Booking Cancel Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Homepage.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        DialogUtil.showAlert(this, "Cancel Last Booking", "Last Booking Cancel Failed !", R.drawable.ic_baseline_error_24);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "####### Cancel Last Booking Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Cancel Last Booking", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
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