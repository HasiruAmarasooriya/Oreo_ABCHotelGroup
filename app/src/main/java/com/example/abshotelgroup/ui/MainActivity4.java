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
import com.example.abshotelgroup.model.BookingEnt;
import com.example.abshotelgroup.util.AbcHotelApp;
import com.example.abshotelgroup.util.AbcHotelConstans;
import com.example.abshotelgroup.util.DBHelper;
import com.example.abshotelgroup.util.DialogUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity4 extends AppCompatActivity {
    private static final String TAG = "MainActivity4";

    EditText username, email, NIC, date, numberofRooms, packagetype;
    Button btnBooking;
    DBHelper mMyDb;

    private ProgressDialog mLoadingBar;
    private Handler handler;
    private int flag;
    private Integer lastBookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        username = findViewById(R.id.PersonName);
        email = findViewById(R.id.EmailAddress);
        NIC = findViewById(R.id.NIC2);
        date = findViewById(R.id.Date);
        numberofRooms = findViewById(R.id.NumberOfRooms);
        packagetype = findViewById(R.id.Packagetype);

        btnBooking = findViewById(R.id.btnBooking);
        handler = new Handler(Looper.getMainLooper());
        if (AbcHotelApp.getLogedUser() != null) {
            username.setText(AbcHotelApp.getLogedUser().getUsername());
            email.setText(AbcHotelApp.getLogedUser().getEmail());
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            packagetype.setText(extras.getString(AbcHotelConstans.BOOKING_CONS));
            btnBooking.setText(R.string.btnBooking);
            flag = 1;
        } else {
            btnBooking.setText(R.string.btnUpdateBooking);
            flag = 2;
            btnBooking.setEnabled(false);
            fetchLastBooking();
        }

        mMyDb = new DBHelper(this);

        btnBooking.setOnClickListener(view -> {
            String user = username.getText().toString();
            String emai = email.getText().toString();
            String nic = NIC.getText().toString();
            String dat = date.getText().toString();
            String numberofrooms = numberofRooms.getText().toString();
            String packagetyp = packagetype.getText().toString();

            if (TextUtils.isEmpty(user)) {

                Toast.makeText(MainActivity4.this, "Please enter your User Name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(emai)) {

                Toast.makeText(MainActivity4.this, "Please enter Your email", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(nic)) {

                Toast.makeText(MainActivity4.this, "Please enter Your NIC", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dat)) {

                Toast.makeText(MainActivity4.this, "Please enter  Date", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(numberofrooms)) {

                Toast.makeText(MainActivity4.this, "Please enter Number of Rooms", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(packagetyp)) {

                Toast.makeText(MainActivity4.this, "Please enter Package Type", Toast.LENGTH_SHORT).show();
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd", Locale.getDefault());
                Date bookDate;
                try {
                    bookDate = formatter.parse(dat);
                    BookingEnt bookingEnt = new BookingEnt(1, nic, bookDate, Integer.parseInt(numberofrooms), packagetyp, AbcHotelApp.getLogedUser());

                    String title = "Create New Booking";
                    String msg = "Are You Confirm New Booking ?";
                    if (flag == 2) {
                        title = "Update Last Booking";
                        msg = "Are You Confirm Update Last Booking ?";
                    }
                    new MaterialAlertDialogBuilder(this).setTitle(title)
                            .setMessage(msg)
                            .setCancelable(false)
                            .setIcon(R.drawable.ic_con)
                            .setPositiveButton("YES", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                if (flag == 1)
                                    confirmBooking(bookingEnt);
                                else
                                    confirmUpdateLastBooking(bookingEnt);
                            })
                            .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss())
                            .setNeutralButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                            .show();

                } catch (ParseException pe) {
                    Log.e(TAG, "######### Booking Date Parce Error #########", pe);
                    DialogUtil.showAlert(this, "Create New Booking", "Invalid Date Format", R.drawable.ic_baseline_error_24);
                } catch (Exception e) {
                    Log.e(TAG, "######### Booking  Error #########", e);
                    DialogUtil.showAlert(this, "Create New Booking", "Invalid Input Format", R.drawable.ic_baseline_error_24);
                }

            }


        });


    }

    private void confirmBooking(final BookingEnt bookingEnt) {
        Log.d(TAG, "<----- Execute Confirm Booking ----->");
        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Confirm New Booking");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_con);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase saveDb = mMyDb.getWritableDatabase()) {
                Thread.sleep(2000);
                ContentValues bookingContentVal = new ContentValues();
                bookingContentVal.put("customer_id", bookingEnt.getUser().getUsername());
                bookingContentVal.put("nic", bookingEnt.getNic());
                bookingContentVal.put("bok_date", new SimpleDateFormat("yy/MM/dd", Locale.getDefault()).format(bookingEnt.getBookDate()));
                bookingContentVal.put("bok_type", bookingEnt.getBookType());
                bookingContentVal.put("num_of_rom", bookingEnt.getNumOfRooms());

                long result = saveDb.insert(DBHelper.TABLE_ROOM, null, bookingContentVal);

                handler.post(() -> {
                    mLoadingBar.dismiss();
                    if (result > -1) {
                        Toast.makeText(MainActivity4.this, "Booking Successful!", Toast.LENGTH_SHORT).show();
                        clearAll();
                        Intent intent = new Intent(getApplicationContext(), ConfirmBooking.class);
                        startActivity(intent);
                    } else {
                        DialogUtil.showAlert(this, "Create New Booking", "Booking Failed!", R.drawable.ic_baseline_error_24);
                    }

                });
            } catch (Exception e) {
                Log.e(TAG, "####### Create New Booking Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Create New Booking", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
        });
    }

    private void confirmUpdateLastBooking(final BookingEnt bookingEnt) {
        Log.d(TAG, "<----- Execute Update Last Booking ----->");
        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Confirm Update Last Booking");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_con);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase writeDb = mMyDb.getWritableDatabase()) {
                Thread.sleep(2000);
                ContentValues bookingUpdContentVal = new ContentValues();
                bookingUpdContentVal.put("nic", bookingEnt.getNic());
                bookingUpdContentVal.put("bok_date", new SimpleDateFormat("yy/MM/dd", Locale.getDefault()).format(bookingEnt.getBookDate()));
                bookingUpdContentVal.put("bok_type", bookingEnt.getBookType());
                bookingUpdContentVal.put("num_of_rom", bookingEnt.getNumOfRooms());

                String whereClause = "id=?";
                String[] whereArgs = {lastBookingId.toString()};
                int result = writeDb.update(DBHelper.TABLE_ROOM, bookingUpdContentVal, whereClause, whereArgs);

                handler.post(() -> {
                    mLoadingBar.dismiss();
                    if (result > -1) {
                        Toast.makeText(MainActivity4.this, "Last Booking Update Successful!", Toast.LENGTH_SHORT).show();
                        clearAll();
                        Intent intent = new Intent(getApplicationContext(), ConfirmBooking.class);
                        startActivity(intent);
                    } else {
                        DialogUtil.showAlert(this, "Update Last Booking", "Last Booking Update Booking Failed!", R.drawable.ic_baseline_error_24);
                    }

                });
            } catch (Exception e) {
                Log.e(TAG, "####### Last Booking Update Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Last Booking Update", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
        });
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
            try (SQLiteDatabase readDb = mMyDb.getReadableDatabase(); Cursor cursor = readDb.rawQuery("SELECT * FROM room_packages WHERE id= (SELECT MAX(id)\n" +
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
                        DialogUtil.showAlert(this, "Fetch Last Booking", "Couldn't Find Any Booking For Update!", R.drawable.ic_baseline_error_24);
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
            username.setText(AbcHotelApp.getLogedUser().getUsername());
            email.setText(AbcHotelApp.getLogedUser().getEmail());
            NIC.setText(bookingEnt.getNic());
            date.setText(new SimpleDateFormat("yy/MM/dd", Locale.getDefault()).format(bookingEnt.getBookDate()));
            numberofRooms.setText(String.format(Locale.getDefault(), "%03d", bookingEnt.getNumOfRooms()));
            packagetype.setText(bookingEnt.getBookType());
            btnBooking.setEnabled(true);
        }
    }

    private void clearAll() {
        Log.d(TAG, "<---- Execute Clear All ---->");
        NIC.getText().clear();
        date.getText().clear();
        numberofRooms.getText().clear();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMyDb != null)
            mMyDb.close();
    }
}