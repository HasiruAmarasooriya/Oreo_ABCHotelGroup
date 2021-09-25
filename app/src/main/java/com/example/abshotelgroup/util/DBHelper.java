package com.example.abshotelgroup.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.abshotelgroup.model.User;
import com.example.abshotelgroup.ui.Feedback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBHelper extends SQLiteOpenHelper {

    public static final int NUMBER_OF_THREAD = 4;
    public static final ExecutorService databaseWriterService = Executors.newFixedThreadPool(NUMBER_OF_THREAD);

    //initialize variable
    private static final int VERSION = 4;
    private static final String DBNAME = "abs_hotel_group";
    private static final String TABLE1 = "users";
    public static final String TABLE_ROOM = "room_packages";
    public static final String TABLEVEHICLE = "vehicle_packages";
    public static final String TABLEFEEDBACK = "feedback";


    public DBHelper(Context context) {
        super(context, DBNAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {

        //Create Tables

        String users = "create Table users(username TEXT PRIMARY KEY ,email TEXT,password TEXT)";

        String tableRooms = "CREATE TABLE room_packages (\n" +
                "    id INTEGER PRIMARY KEY autoincrement,\n" +
                "    customer_id TEXT,\n" +
                "    nic TEXT,\n" +
                "    bok_date TEXT,\n" +
                "    bok_type TEXT,\n" +
                "    num_of_rom INTEGER,\n" +
                "    FOREIGN KEY(customer_id) REFERENCES users(username)\n" +
                ");";

        String tableVehicle = "CREATE TABLE vehicle_packages (\n" +
                "    id INTEGER PRIMARY KEY autoincrement,\n" +
                "    customer_id TEXT,\n" +
                "    nic TEXT,\n" +
                "    bok_type TEXT,\n" +
                "    num_of_days INTEGER,\n" +
                "    FOREIGN KEY(customer_id) REFERENCES users(username)\n" +
                ");";


        String tableFeedback = "CREATE TABLE feedback (\n" +
                "    id INTEGER PRIMARY KEY autoincrement,\n" +
                "    customer_id TEXT,\n" +
                "    feed TEXT,\n" +
                "    FOREIGN KEY(customer_id) REFERENCES users(username)\n" +
                ");";

        MyDB.execSQL(users);
        MyDB.execSQL(tableRooms);
        MyDB.execSQL(tableVehicle);
        MyDB.execSQL(tableFeedback);

    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {

        //Drop Existing Table

        MyDB.execSQL(" DROP TABLE IF EXISTS " + TABLE1);
        MyDB.execSQL(" DROP TABLE IF EXISTS " + TABLE_ROOM);
        MyDB.execSQL(" DROP TABLE IF EXISTS " + TABLEVEHICLE);
        MyDB.execSQL(" DROP TABLE IF EXISTS " + TABLEFEEDBACK);
        onCreate(MyDB);

    }

    //Create Insert method

    public Boolean insertData(String username, String email, String password) {

        //Get Writeable Database
        SQLiteDatabase MyDB = this.getWritableDatabase();

        //Create ContentValues

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("username", username);
        contentValues1.put("email", email);
        contentValues1.put("password", password);

        //Insert Data into Database

        long result = MyDB.insert(TABLE1, null, contentValues1);


        if (result == -1) {
            return false;
        } else {
            return true;
        }


    }

    public Boolean checkUsername(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("select * from users where username = ?", new String[]{username});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public Boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("select * from users where username = ? and password = ?", new String[]{username, password});
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            User user = new User(cursor.getString(cursor.getColumnIndexOrThrow("username")), cursor.getString(cursor.getColumnIndexOrThrow("email")), cursor.getString(cursor.getColumnIndexOrThrow("password")));
            AbcHotelApp.setLogedUser(user);
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }


}
