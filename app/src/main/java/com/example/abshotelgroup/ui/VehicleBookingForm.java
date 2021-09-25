package com.example.abshotelgroup.ui;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.abshotelgroup.R;
import com.example.abshotelgroup.model.VehicleBookingEnt;
import com.example.abshotelgroup.util.AbcHotelApp;
import com.example.abshotelgroup.util.AbcHotelConstans;
import com.example.abshotelgroup.util.DBHelper;
import com.example.abshotelgroup.util.DialogUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

public class VehicleBookingForm extends AppCompatActivity {
    private static final String TAG = "VehicleBookingForm";
    EditText username, email, NIC, Duration, packagetype;
    Button btnBooking;
    DBHelper mMyDB;

    private ProgressDialog mLoadingBar;
    private Handler handler;
    private int flag;
    private Integer lastBookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_booking_form);

        username = findViewById(R.id.PersonName);
        email = findViewById(R.id.EmailAddress);
        NIC = findViewById(R.id.NIC2);
        Duration = findViewById(R.id.Date);
        packagetype = findViewById(R.id.Packagetype);
        btnBooking = findViewById(R.id.btnBooking);

        if (AbcHotelApp.getLogedUser() != null) {
            username.setText(AbcHotelApp.getLogedUser().getUsername());
            email.setText(AbcHotelApp.getLogedUser().getEmail());
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            packagetype.setText(extras.getString(AbcHotelConstans.VEH_CONS));
            btnBooking.setText(R.string.btnBooking);
            flag = 1;
        } else {
            btnBooking.setText(R.string.btnUpdateBooking);
            flag = 2;
            btnBooking.setEnabled(false);
            fetchLastBooking();
        }
        handler = new Handler(Looper.getMainLooper());
        mMyDB = new DBHelper(this);

        btnBooking.setOnClickListener(view -> {
            String user = username.getText().toString();
            String emai = email.getText().toString();
            String nic = NIC.getText().toString();
            String dat = Duration.getText().toString();
            String packagetyp = packagetype.getText().toString();

            if (TextUtils.isEmpty(user)) {
                Toast.makeText(VehicleBookingForm.this, "Please enter your User Name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(emai)) {
                Toast.makeText(VehicleBookingForm.this, "Please enter Your email", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(nic)) {
                Toast.makeText(VehicleBookingForm.this, "Please enter Your NIC", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dat)) {
                Toast.makeText(VehicleBookingForm.this, "Please enter  Date", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(packagetyp)) {
                Toast.makeText(VehicleBookingForm.this, "Please enter Package Type", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    int numberOfDays = Integer.parseInt(dat);
                    if (numberOfDays <= 0) {
                        DialogUtil.showAlert(this, "Create New Vehicle Booking", "Duration Cant Zero Or Minus ", R.drawable.ic_baseline_error_24);
                        return;
                    }
                    VehicleBookingEnt vehicleBookingEnt = new VehicleBookingEnt(
                            1, nic, packagetyp, numberOfDays, AbcHotelApp.getLogedUser()
                    );

                    String title = "Create New Vehicle Booking";
                    String msg = "Are You Confirm New Vehicle Booking ?";
                    if (flag == 2) {
                        title = "Update Last Vehicle Booking";
                        msg = "Are You Confirm Update Last Vehicle Booking ?";
                    }
                    new MaterialAlertDialogBuilder(this).setTitle(title)
                            .setMessage(msg)
                            .setCancelable(false)
                            .setIcon(R.drawable.ic_con)
                            .setPositiveButton("YES", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                if (flag == 1)
                                    confirmBooking(vehicleBookingEnt);
                                else
                                    confirmUpdateLastBooking(vehicleBookingEnt);
                            })
                            .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss())
                            .setNeutralButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                            .show();

                } catch (NumberFormatException npe) {
                    Log.e(TAG, "######### Vehicle Booking Date Parce Error #########", npe);
                    DialogUtil.showAlert(this, "Create New Vehicle Booking", "Invalid Number Format", R.drawable.ic_baseline_error_24);
                } catch (Exception e) {
                    Log.e(TAG, "######### Booking Vehicle Error #########", e);
                    DialogUtil.showAlert(this, "Create New Vehicle Booking", "Invalid Input Format", R.drawable.ic_baseline_error_24);
                }
            }


        });
    }

    private void confirmBooking(final VehicleBookingEnt vehicleBookingEnt) {
        Log.d(TAG, "<----- Execute Confirm New Vehicle Booking ----->");
        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Confirm New Vehicle Booking");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_con);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase saveDb = mMyDB.getWritableDatabase()) {
                Thread.sleep(1000);
                ContentValues bookingContentVal = new ContentValues();
                bookingContentVal.put("customer_id", vehicleBookingEnt.getUser().getUsername());
                bookingContentVal.put("nic", vehicleBookingEnt.getNic());
                bookingContentVal.put("bok_type", vehicleBookingEnt.getVehType());
                bookingContentVal.put("num_of_days", vehicleBookingEnt.getNumOfDays());

                long result = saveDb.insert(DBHelper.TABLEVEHICLE, null, bookingContentVal);

                handler.post(() -> {
                    mLoadingBar.dismiss();
                    if (result > -1) {
                        Toast.makeText(VehicleBookingForm.this, "Vehicle Booking Successful!", Toast.LENGTH_SHORT).show();
                        clearAll();
                        Intent intent = new Intent(getApplicationContext(), VehicleBookingConfirm.class);
                        startActivity(intent);
                    } else {
                        DialogUtil.showAlert(this, "Create New Vehicle Booking", "Vehicle Booking Failed!", R.drawable.ic_baseline_error_24);
                    }

                });
            } catch (Exception e) {
                Log.e(TAG, "####### Create New Vehicle Booking Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Create New Vehicle Booking", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
        });
    }

    private void confirmUpdateLastBooking(final VehicleBookingEnt vehicleBookingEnt) {
        Log.d(TAG, "<----- Execute Update Last Vehicle Booking ----->");
        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Confirm Update Last Vehicle Booking");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_con);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase writeDb = mMyDB.getWritableDatabase()) {
                Thread.sleep(1000);
                ContentValues bookingUpdContentVal = new ContentValues();
                bookingUpdContentVal.put("nic", vehicleBookingEnt.getNic());
                bookingUpdContentVal.put("bok_type", vehicleBookingEnt.getVehType());
                bookingUpdContentVal.put("num_of_days", vehicleBookingEnt.getNumOfDays());

                String whereClause = "id=?";
                String[] whereArgs = {lastBookingId.toString()};
                int result = writeDb.update(DBHelper.TABLEVEHICLE, bookingUpdContentVal, whereClause, whereArgs);

                handler.post(() -> {
                    mLoadingBar.dismiss();
                    if (result > -1) {
                        Toast.makeText(VehicleBookingForm.this, "Last Vehicle Booking Update Successful!", Toast.LENGTH_SHORT).show();
                        clearAll();
                        Intent intent = new Intent(getApplicationContext(), VehicleBookingConfirm.class);
                        startActivity(intent);
                    } else {
                        DialogUtil.showAlert(this, "Update Last Vehicle Booking", "Last Vehicle Booking Update Failed!", R.drawable.ic_baseline_error_24);
                    }

                });
            } catch (Exception e) {
                Log.e(TAG, "####### Last Vehicle Booking Update Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Last Vehicle Booking Update", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
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
                        DialogUtil.showAlert(this, "Fetch Last Vehicle Booking", "Couldn't Find Any Vehicle Booking For Update!", R.drawable.ic_baseline_error_24);
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

            username.setText(AbcHotelApp.getLogedUser().getUsername());
            email.setText(AbcHotelApp.getLogedUser().getEmail());
            NIC.setText(vehicleBookingEnt.getNic());
            Duration.setText(String.format(Locale.getDefault(), "%02d", vehicleBookingEnt.getNumOfDays()));
            packagetype.setText(vehicleBookingEnt.getVehType());
            btnBooking.setEnabled(true);
        }
    }

    private void clearAll() {
        Log.d(TAG, "<---- Execute Clear All ---->");
        NIC.getText().clear();
        Duration.getText().clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMyDB != null)
            mMyDB.close();
    }
}