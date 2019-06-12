package com.example.iosdev.sensorproject

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.v7.app.AlertDialog
import com.example.iosdev.sensorproject.DatabaseHelper.Companion.COL_ID
import com.example.iosdev.sensorproject.DatabaseHelper.Companion.COL_IS_COMPLETED
import com.example.iosdev.sensorproject.DatabaseHelper.Companion.TABLE_NAME

/**
 * Created by thanhbinhtran on 02/10/16.
 */

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    /*
     * Get all data from the database and return a {@link Cursor} over the result set.
     * @return A {@link Cursor} object, which is positioned before the first entry.
     */
    val allData: Cursor
        get() {
            val db = this.getWritableDatabase()
            return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        }

    /*
     * Get all data from the database based on the isCompleted and return a {@link Cursor} over the result set.
     * @return A {@link Cursor} object, which is positioned before the first entry.
     */
    val dataByIsCompleted: Cursor
        get() {
            val db = this.getWritableDatabase()
            return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_IS_COMPLETED = ?", arrayOf("1"))
        }

    @Override
    fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_REWARD + " TEXT, "
                + COL_GOAL + " INTEGER, "
                + COL_CURRENT_STEP + " INTEGER, "
                + COL_SPEED + " DOUBLE, "
                + COL_IS_COMPLETED + " INTEGER, "
                + COL_REWARD_CODE + " TEXT)")

        val contentValues = ContentValues()
        this.prePopulateDatabase("Free coffee from Sodexo", "MUC123", 30, 0, 0, db, contentValues)
        this.prePopulateDatabase("50% off from Luhta winter jackets from Intersport", "IL912", 40, 0, 0, db, contentValues)
        this.prePopulateDatabase("25% off from any meal in Amarillo", "A3139", 1500, 0, 0, db, contentValues)
        this.prePopulateDatabase("Helsingin Sanomat for 6 months 20,00€", "HS0900", 2000, 0, 0, db, contentValues)
        this.prePopulateDatabase("Mens haircut 12€ in Style Workshop Kruununhaka", "SWK2922", 2500, 0, 0, db, contentValues)
        this.prePopulateDatabase("Free car wash in Espoon Starwash", "EST8889", 3000, 0, 0, db, contentValues)
        this.prePopulateDatabase("Chefs menu 10€ in Töölön Sävel", "TS1231", 3500, 0, 0, db, contentValues)
        this.prePopulateDatabase("Free Gym membership in Fitness 24/7", "F1223", 4000, 0, 0, db, contentValues)
        this.prePopulateDatabase("Exit room game for 1-6 people 9€ in Exit Room Helsinki", "F1223", 4500, 0, 0, db, contentValues)
        this.prePopulateDatabase("Free bucket from Tokmanni", "FB9942", 5000, 0, 0, db, contentValues)
    }

    @Override
    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    private fun prePopulateDatabase(reward: String, reward_code: String, goal: Integer, currentStep: Integer, speed: Integer, db: SQLiteDatabase, contentValues: ContentValues) {
        contentValues.put(COL_REWARD, reward)
        contentValues.put(COL_REWARD_CODE, reward_code)
        contentValues.put(COL_GOAL, goal)
        contentValues.put(COL_CURRENT_STEP, currentStep)
        contentValues.put(COL_SPEED, speed)
        contentValues.put(COL_IS_COMPLETED, 0)

        db.insert(TABLE_NAME, null, contentValues)
        contentValues.clear()
    }

    fun insertData(reward: String, goal: Integer, currentStep: Integer, speed: Integer): Boolean {
        val db = this.getWritableDatabase()
        val contentValues = ContentValues()
        contentValues.put(COL_REWARD, reward)
        contentValues.put(COL_GOAL, goal)
        contentValues.put(COL_CURRENT_STEP, currentStep)
        contentValues.put(COL_SPEED, speed)
        val result = db.insert(TABLE_NAME, null, contentValues)

        return if (result == -1) {
            false
        } else {
            true
        }
    }

    /*
     * Update current_step column in accordance with the _id
     * @param id the id of the value to update
     * @param currentStep the current_step for updating
     *
     * @return the boolean value indicating if the update was successful.
     */
    fun updateCurrentStep(id: String, currentStep: Integer): Boolean {
        val db = this.getWritableDatabase()
        val contentValues = ContentValues()
        contentValues.put(COL_ID, id)
        contentValues.put(COL_CURRENT_STEP, currentStep)
        db.update(TABLE_NAME, contentValues, "$COL_ID = ?", arrayOf(id))
        db.close()
        return true
    }

    /*
     * Update isCompleted column in accordance with the _id
     * @param id the id of the value to update
     * @param value the data for the value to update
     *
     * @return the boolean value indicating if the update was successful.
     */
    fun updateIsCompleted(id: String, value: Int): Boolean {
        val db = this.getWritableDatabase()
        val contentValues = ContentValues()
        contentValues.put(COL_ID, id)
        contentValues.put(COL_IS_COMPLETED, value)
        db.update(TABLE_NAME, contentValues, "$COL_ID = ?", arrayOf(id))
        db.close()
        return true
    }

    /*
     * Update speed column in accordance with the _id
     * @param id the id of the value to update
     * @param speed the speed value for updating
     *
     * @return the boolean value indicating if the update was successful.
     */
    fun updateSpeed(id: String, speed: Double): Boolean {
        val db = this.getWritableDatabase()
        val contentValues = ContentValues()
        contentValues.put(COL_ID, id)
        contentValues.put(COL_SPEED, speed)
        db.update(TABLE_NAME, contentValues, "$COL_ID = ?", arrayOf(id))
        db.close()
        return true
    }

    /*
     * Get one row of data from the database based on the _id and return a {@link Cursor} over the result set.
     * @return A {@link Cursor} object, which is positioned before the first entry.
     */
    fun getDataById(id: String): Cursor {
        val db = this.getWritableDatabase()
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_ID = ?", arrayOf(id))
    }

    fun getCurrentStep(id: String): Int {
        val currentStep: Int
        val cursor = this.getDataById(id)
        if (cursor.moveToFirst()) {
            currentStep = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_CURRENT_STEP))
        } else {
            currentStep = 0
        }
        cursor.close()
        return currentStep
    }

    fun getSpeed(id: String): Int {
        val speed: Int
        val cursor = this.getDataById(id)
        if (cursor.moveToFirst()) {
            speed = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_SPEED))
        } else {
            speed = 0
        }
        cursor.close()
        return speed
    }

    fun getGoal(id: String): Int {
        val goal: Int
        val cursor = this.getDataById(id)
        if (cursor.moveToFirst()) {
            goal = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_GOAL))
        } else {
            goal = 0
        }
        cursor.close()
        return goal
    }

    fun getReward(id: String): String? {
        val reward: String?
        val cursor = this.getDataById(id)
        if (cursor.moveToFirst()) {
            reward = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_REWARD))
        } else {
            reward = null
        }
        cursor.close()
        return reward
    }

    fun getRewardCode(id: String): String? {
        val code: String?
        val cursor = this.getDataById(id)
        if (cursor.moveToFirst()) {
            code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_REWARD_CODE))
        } else {
            code = null
        }
        cursor.close()
        return code
    }


    fun showMessage(title: String, message: String, context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.show()
    }

    companion object {
        val DATABASE_NAME = "reward_database"
        val TABLE_NAME = "reward_table"
        val COL_ID = "_id"
        val COL_REWARD = "reward"
        val COL_GOAL = "goal"
        val COL_CURRENT_STEP = "current_step"
        val COL_SPEED = "speed"
        val COL_IS_COMPLETED = "isCompleted"
        val COL_REWARD_CODE = "reward_code"
    }


}
