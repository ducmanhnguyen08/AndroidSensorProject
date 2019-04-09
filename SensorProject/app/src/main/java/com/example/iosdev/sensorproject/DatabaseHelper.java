package com.example.iosdev.sensorproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;

/**
 * Created by thanhbinhtran on 02/10/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "reward_database";
    public static final String TABLE_NAME = "reward_table";
    public static final String COL_ID = "_id";
    public static final String COL_REWARD = "reward";
    public static final String COL_GOAL = "goal";
    public static final String COL_CURRENT_STEP = "current_step";
    public static final String COL_SPEED = "speed";
    public static final String COL_IS_COMPLETED = "isCompleted";
    public static final String COL_REWARD_CODE = "reward_code";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_REWARD +" TEXT, "
                + COL_GOAL + " INTEGER, "
                + COL_CURRENT_STEP + " INTEGER, "
                + COL_SPEED + " DOUBLE, "
                + COL_IS_COMPLETED + " INTEGER, "
                + COL_REWARD_CODE + " TEXT)");

        ContentValues contentValues = new ContentValues();
        this.prePopulateDatabase("Free coffee from Sodexo", "MUC123", 30, 0, 0, db, contentValues);
        this.prePopulateDatabase("50% off from Luhta winter jackets from Intersport", "IL912", 40, 0, 0, db, contentValues);
        this.prePopulateDatabase("25% off from any meal in Amarillo", "A3139", 1500, 0, 0, db, contentValues);
        this.prePopulateDatabase("Helsingin Sanomat for 6 months 20,00€", "HS0900", 2000, 0, 0, db, contentValues);
        this.prePopulateDatabase("Mens haircut 12€ in Style Workshop Kruununhaka", "SWK2922", 2500, 0, 0, db, contentValues);
        this.prePopulateDatabase("Free car wash in Espoon Starwash", "EST8889", 3000, 0, 0, db, contentValues);
        this.prePopulateDatabase("Chefs menu 10€ in Töölön Sävel", "TS1231", 3500, 0, 0, db, contentValues);
        this.prePopulateDatabase("Free Gym membership in Fitness 24/7", "F1223", 4000, 0, 0, db, contentValues);
        this.prePopulateDatabase("Exit room game for 1-6 people 9€ in Exit Room Helsinki", "F1223", 4500, 0, 0, db, contentValues);
        this.prePopulateDatabase("Free bucket from Tokmanni", "FB9942", 5000, 0, 0, db, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    private void prePopulateDatabase (String reward, String reward_code, Integer goal, Integer currentStep, Integer speed, SQLiteDatabase db, ContentValues contentValues) {
        contentValues.put(COL_REWARD, reward);
        contentValues.put(COL_REWARD_CODE, reward_code);
        contentValues.put(COL_GOAL, goal);
        contentValues.put(COL_CURRENT_STEP, currentStep);
        contentValues.put(COL_SPEED, speed);
        contentValues.put(COL_IS_COMPLETED, 0);

        db.insert(TABLE_NAME, null, contentValues);
        contentValues.clear();
    }

    public Boolean insertData(String reward, Integer goal, Integer currentStep, Integer speed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_REWARD, reward);
        contentValues.put(COL_GOAL, goal);
        contentValues.put(COL_CURRENT_STEP, currentStep);
        contentValues.put(COL_SPEED, speed);
        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Get all data from the database and return a {@link Cursor} over the result set.
     * @return A {@link Cursor} object, which is positioned before the first entry.
     */
    public Cursor getAllData () {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }

    /*
     * Update current_step column in accordance with the _id
     * @param id the id of the value to update
     * @param currentStep the current_step for updating
     *
     * @return the boolean value indicating if the update was successful.
     */
    public Boolean updateCurrentStep (String id, Integer currentStep) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, id);
        contentValues.put(COL_CURRENT_STEP, currentStep);
        db.update(TABLE_NAME, contentValues, COL_ID + " = ?", new String[] {id});
        db.close();
        return true;
    }

    /*
     * Update isCompleted column in accordance with the _id
     * @param id the id of the value to update
     * @param value the data for the value to update
     *
     * @return the boolean value indicating if the update was successful.
     */
    public Boolean updateIsCompleted (String id, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, id);
        contentValues.put(COL_IS_COMPLETED, value);
        db.update(TABLE_NAME, contentValues, COL_ID + " = ?", new String[] {id});
        db.close();
        return true;
    }

    /*
     * Update speed column in accordance with the _id
     * @param id the id of the value to update
     * @param speed the speed value for updating
     *
     * @return the boolean value indicating if the update was successful.
     */
    public Boolean updateSpeed (String id, double speed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, id);
        contentValues.put(COL_SPEED, speed);
        db.update(TABLE_NAME, contentValues, COL_ID + " = ?", new String[] {id});
        db.close();
        return true;
    }

    /*
     * Get one row of data from the database based on the _id and return a {@link Cursor} over the result set.
     * @return A {@link Cursor} object, which is positioned before the first entry.
     */
    public Cursor getDataById (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID + " = ?", new String[]{id});
        return cursor;
    }

    /*
     * Get all data from the database based on the isCompleted and return a {@link Cursor} over the result set.
     * @return A {@link Cursor} object, which is positioned before the first entry.
     */
    public Cursor getDataByIsCompleted () {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_IS_COMPLETED + " = ?", new String[]{"1"});
        return cursor;
    }

    public int getCurrentStep (String id) {
        int currentStep;
        Cursor cursor = this.getDataById(id);
        if (cursor.moveToFirst()) {
            currentStep = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CURRENT_STEP));
        } else {
            currentStep = 0;
        }
        cursor.close();
        return currentStep;
    }

    public int getSpeed (String id) {
        int speed;
        Cursor cursor = this.getDataById(id);
        if (cursor.moveToFirst()) {
            speed = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_SPEED));
        } else {
            speed = 0;
        }
        cursor.close();
        return speed;
    }

    public int getGoal (String id) {
        int goal;
        Cursor cursor = this.getDataById(id);
        if (cursor.moveToFirst()) {
            goal = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_GOAL));
        } else {
            goal = 0;
        }
        cursor.close();
        return goal;
    }

    public String getReward (String id) {
         String reward;
        Cursor cursor = this.getDataById(id);
        if (cursor.moveToFirst()) {
            reward = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_REWARD));
        } else {
            reward = null;
        }
        cursor.close();
        return reward;
    }

    public String getRewardCode (String id) {
        String code;
        Cursor cursor = this.getDataById(id);
        if (cursor.moveToFirst()) {
            code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_REWARD_CODE));
        } else {
            code = null;
        }
        cursor.close();
        return code;
    }



    public void showMessage(String title, String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


}
