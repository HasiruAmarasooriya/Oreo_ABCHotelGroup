package com.example.abshotelgroup.ui;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.abshotelgroup.R;
import com.example.abshotelgroup.model.FeedbackEnt;
import com.example.abshotelgroup.util.AbcHotelApp;
import com.example.abshotelgroup.util.DBHelper;
import com.example.abshotelgroup.util.DialogUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class FeedbackViewFragment extends BottomSheetDialogFragment {
    private static final String TAG = "FeedbackViewFragment";
    private ListView lstAllFeeds;

    private DBHelper mDbHelper;

    private ProgressDialog mLoadingBar;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        mDbHelper = new DBHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback_view, container, false);
        lstAllFeeds = view.findViewById(R.id.lst_all_feeds);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchAllFeeds();
    }

    private void fetchAllFeeds() {
        Log.d(TAG, "<----- Execute Fetch All Feedbacks ----->");
        mLoadingBar = new ProgressDialog(requireContext());
        mLoadingBar.setTitle("Fetch All Feedbacks");
        mLoadingBar.setMessage("Please Waite...");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.setIcon(R.drawable.ic_con);
        mLoadingBar.show();

        DBHelper.databaseWriterService.execute(() -> {
            try (SQLiteDatabase readDb = mDbHelper.getReadableDatabase(); Cursor cursor = readDb.rawQuery("SELECT * FROM feedback ", null)) {
                Thread.sleep(500);
                List<FeedbackEnt> feedbackEntList = new ArrayList<>();
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        FeedbackEnt feedback = new FeedbackEnt(
                                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("feed")),
                                AbcHotelApp.getLogedUser()
                        );
                        feedback.setStrUser(cursor.getString(cursor.getColumnIndexOrThrow("customer_id")));

                        feedbackEntList.add(feedback);
                    }
                }
                if (feedbackEntList.size() > 0) {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        setData(feedbackEntList);
                    });
                } else {
                    handler.post(() -> {
                        mLoadingBar.dismiss();
                        DialogUtil.showAlert(requireContext(), "Fetch All Feedbacks", "Couldn't Find Any Feedback/s !", R.drawable.ic_baseline_error_24);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "####### Fetch All Feedback Error #######", e);
                handler.post(() -> {
                    mLoadingBar.dismiss();
                    DialogUtil.showAlert(requireContext(), "Fetch All Feedbacks", "Error Occurred\n" + e.getMessage(), R.drawable.ic_baseline_error_24);
                });
            }
        });
    }

    private void setData(List<FeedbackEnt> feedbackEntList) {
        ArrayAdapter<FeedbackEnt> feedsAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1,
                feedbackEntList);
        lstAllFeeds.setAdapter(feedsAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDbHelper != null)
            mDbHelper.close();
    }
}