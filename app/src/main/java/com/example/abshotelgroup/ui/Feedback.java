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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.abshotelgroup.R;
import com.example.abshotelgroup.model.FeedbackEnt;
import com.example.abshotelgroup.util.AbcHotelApp;
import com.example.abshotelgroup.util.DBHelper;
import com.example.abshotelgroup.util.DialogUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Feedback extends AppCompatActivity {
    private static final String TAG = "Feedback";

    private EditText ediFeedback;

    private DBHelper mDbHelper;

    private Integer lastFeedbackId;
    private ProgressDialog mLoadingBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ediFeedback = findViewById(R.id.editTextTextMultiLine);

        handler = new Handler(Looper.getMainLooper());
        mDbHelper = new DBHelper(this);

        ediFeedback.setOnLongClickListener(view -> {
            fetchLastFeedback();
            return false;
        });
    }

    public void createFeeback(View view) {
        Log.d(TAG, "<---- Create Feedback Called ---->");

        String feedback = ediFeedback.getText().toString();
        if (TextUtils.isEmpty(feedback)) {
            ediFeedback.setError("Feedback Is Required !");
            return;
        }
        FeedbackEnt feedbackEnt = new FeedbackEnt(1, feedback, AbcHotelApp.getLogedUser());

        new MaterialAlertDialogBuilder(this).setTitle("Create New Feedback")
                .setMessage("Are You Confirm Create New Feedback ?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_con)
                .setPositiveButton("YES", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    confirmCreateFeedback(feedbackEnt);
                })
                .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss())
                .setNeutralButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    public void updateFeeback(View view) {
        Log.d(TAG, "<---- Update Feedback Called ---->");
        if (lastFeedbackId == null) {
            DialogUtil.showAlert(this, "Edit Last Feedback", "Couldn't Find Any Feedback To Edit!", R.drawable.ic_baseline_error_24);
            return;
        }

        String feedback = ediFeedback.getText().toString();
        if (TextUtils.isEmpty(feedback)) {
            ediFeedback.setError("Feedback Is Required !");
            return;
        }
        FeedbackEnt feedbackEnt = new FeedbackEnt(1, feedback, AbcHotelApp.getLogedUser());

        new MaterialAlertDialogBuilder(this).setTitle("Update Feedback")
                .setMessage("Are You Confirm Update Feedback ?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_con)
                .setPositiveButton("YES", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    confirmUpdateLastFeedback(feedbackEnt);
                })
                .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss())
                .setNeutralButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    public void deleteFeeback(View view) {
        Log.d(TAG, "<---- Delete Feedback Called ---->");
        if (lastFeedbackId == null) {
            DialogUtil.showAlert(this, "Cancel Last Feedback", "Couldn't Find Any Feedback To Cancel!", R.drawable.ic_baseline_error_24);
            return;
        }
        new MaterialAlertDialogBuilder(this).setTitle("Cancel Feedback")
                .setMessage("Are You Confirm Cancel Feedback ?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_con)
                .setPositiveButton("YES", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    confirmDeleteFeedback();
                })
                .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss())
                .setNeutralButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    private void confirmCreateFeedback(final FeedbackEnt feedbackEnt) {
        Log.d(TAG, "<----- Execute Confirm New Feedback ----->");
        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Confirm New Feedback");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_con);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase saveDb = mDbHelper.getWritableDatabase()) {
                Thread.sleep(1000);
                ContentValues feedbackContentVal = new ContentValues();
                feedbackContentVal.put("customer_id", feedbackEnt.getUser().getUsername());
                feedbackContentVal.put("feed", feedbackEnt.getDesc());

                long result = saveDb.insert(DBHelper.TABLEFEEDBACK, null, feedbackContentVal);

                handler.post(() -> {
                    mLoadingBar.dismiss();
                    if (result > -1) {
                        Toast.makeText(Feedback.this, "Feedback Send Successful!", Toast.LENGTH_SHORT).show();
                        clearAll();
                        Intent intent = new Intent(getApplicationContext(), Homepage.class);
                        startActivity(intent);
                    } else {
                        DialogUtil.showAlert(this, "Create New Feedback", "Create New Feedback Failed!", R.drawable.ic_baseline_error_24);
                    }

                });
            } catch (Exception e) {
                Log.e(TAG, "####### Create New Feedback Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Create New Feedback", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
        });
    }

    private void confirmUpdateLastFeedback(final FeedbackEnt feedbackEnt) {
        Log.d(TAG, "<----- Execute Update Last Feedback ----->");
        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Confirm Update Last Feedback");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_con);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase writeDb = mDbHelper.getWritableDatabase()) {
                Thread.sleep(1000);
                ContentValues feedbackUpdContentVal = new ContentValues();
                feedbackUpdContentVal.put("feed", feedbackEnt.getDesc());

                String whereClause = "id=?";
                String[] whereArgs = {lastFeedbackId.toString()};
                int result = writeDb.update(DBHelper.TABLEFEEDBACK, feedbackUpdContentVal, whereClause, whereArgs);

                handler.post(() -> {
                    mLoadingBar.dismiss();
                    if (result > -1) {
                        Toast.makeText(Feedback.this, "Last Feedback Update Successful!", Toast.LENGTH_SHORT).show();
                        clearAll();
                        Intent intent = new Intent(getApplicationContext(), Homepage.class);
                        startActivity(intent);
                    } else {
                        DialogUtil.showAlert(this, "Update Last Feedback", "Last Feedback Update Failed!", R.drawable.ic_baseline_error_24);
                    }

                });
            } catch (Exception e) {
                Log.e(TAG, "####### Last Feedback Update Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Last Feedback  Update", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
        });
    }

    private void confirmDeleteFeedback() {
        Log.d(TAG, "<----- Execute Confirm Cancel Feedback ---->");

        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Cancel Last Feedback");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_delete);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase writeDb = mDbHelper.getWritableDatabase()) {
                Thread.sleep(2000);

                String whereClause = "id=?";
                String[] whereArgs = {lastFeedbackId.toString()};
                int items = writeDb.delete(DBHelper.TABLEFEEDBACK, whereClause, whereArgs);

                if (items > 0) {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        lastFeedbackId = null;
                        Toast.makeText(Feedback.this, "Last Feedback Cancel Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Homepage.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        DialogUtil.showAlert(this, "Cancel Last Feedback", "Last Feedback Cancel Failed !", R.drawable.ic_baseline_error_24);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "####### Cancel Last Feedback Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Cancel Last Feedback", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
        });
    }

    private void fetchLastFeedback() {
        Log.d(TAG, "<----- Execute Fetch Last Feedback ----->");
        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Fetch Last Feedback");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_con);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase readDb = mDbHelper.getReadableDatabase(); Cursor cursor = readDb.rawQuery("SELECT * FROM feedback WHERE id= (SELECT MAX(id)\n" +
                    "FROM feedback WHERE customer_id=?)", new String[]{AbcHotelApp.getLogedUser().getUsername()})) {
                Thread.sleep(1000);
                if (cursor.moveToFirst()) {
                    FeedbackEnt feedback = new FeedbackEnt(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("feed")),
                            AbcHotelApp.getLogedUser()
                    );

                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        setData(feedback);
                    });
                } else {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        DialogUtil.showAlert(this, "Fetch Last Feedback", "Couldn't Find Any Feedback For Update!", R.drawable.ic_baseline_error_24);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "####### Fetch Last Feedback Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(this, "Fetch Last Feedback", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
        });
    }

    private void setData(final FeedbackEnt feedbackEnt) {
        if (feedbackEnt != null) {
            this.lastFeedbackId = feedbackEnt.getId();
            ediFeedback.setText(feedbackEnt.getDesc());
        }
    }

    private void clearAll() {
        Log.d(TAG, "<---- Execute Clear All ---->");
        ediFeedback.getText().clear();
        this.lastFeedbackId = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDbHelper != null)
            mDbHelper.close();
    }
}