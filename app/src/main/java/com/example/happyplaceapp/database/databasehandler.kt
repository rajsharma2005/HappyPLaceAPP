package com.example.happyplaceapp.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.room.Delete
import com.example.happyplaceapp.models.HappyPlaceModel

class databasehandler(context: Context) :
    SQLiteOpenHelper(context , DATABASE_NAME , null , DATABASE_VERSION)
    // SQLiteOpenHelper it take context to obtain application related information
{

    companion object {
        //companion object is used to store constant values
        //to simply the code we use to write those value here
        private const val DATABASE_VERSION = 2 // Database version
        private const val DATABASE_NAME = "HappyPlacesDatabase" // Database name
        private const val TABLE_HAPPY_PLACE = "HappyPlacesTable" // Table Name

        //All the Columns names
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }
//both onCreate and onUpgrade are the members of class
    override fun onCreate(db: SQLiteDatabase?) {
        //It will gonna create a table
    val table = ("CREATE TABLE " +
            TABLE_HAPPY_PLACE + " (" +
            KEY_ID + " INTEGER PRIMARY KEY, " +
            KEY_TITLE + " TEXT, " +
            KEY_IMAGE + " TEXT, " +
            KEY_DESCRIPTION + " TEXT, " +
            KEY_DATE + " TEXT, " +
            KEY_LOCATION + " TEXT, " +
            KEY_LATITUDE + " TEXT, " +
            KEY_LONGITUDE + " TEXT)"
            )
    db?.execSQL(table)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
        onCreate(db)
    }

    fun addHappyPlace(happyPlace: HappyPlaceModel) : Long{
        val db = this.writableDatabase


        val contentValues = ContentValues()

        contentValues.put(KEY_TITLE, happyPlace.title) // HappyPlaceModelClass TITLE
        contentValues.put(KEY_IMAGE, happyPlace.image) // HappyPlaceModelClass IMAGE
        contentValues.put(KEY_DESCRIPTION, happyPlace.description) // HappyPlaceModelClass DESCRIPTION
        contentValues.put(KEY_DATE, happyPlace.date) // HappyPlaceModelClass DATE
        contentValues.put(KEY_LOCATION, happyPlace.location) // HappyPlaceModelClass LOCATION
        contentValues.put(KEY_LATITUDE, happyPlace.latitude) // HappyPlaceModelClass LATITUDE
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude)

          val result =  db.insert(TABLE_HAPPY_PLACE , null , contentValues)
        db.close()
        return result


    }
    fun updateHappyPlace(happyPlace: HappyPlaceModel) : Int{
        val db = this.writableDatabase


        val contentValues = ContentValues()

        contentValues.put(KEY_TITLE, happyPlace.title) // HappyPlaceModelClass TITLE
        contentValues.put(KEY_IMAGE, happyPlace.image) // HappyPlaceModelClass IMAGE
        contentValues.put(KEY_DESCRIPTION, happyPlace.description) // HappyPlaceModelClass DESCRIPTION
        contentValues.put(KEY_DATE, happyPlace.date) // HappyPlaceModelClass DATE
        contentValues.put(KEY_LOCATION, happyPlace.location) // HappyPlaceModelClass LOCATION
        contentValues.put(KEY_LATITUDE, happyPlace.latitude) // HappyPlaceModelClass LATITUDE
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude)

        val success =  db.update(TABLE_HAPPY_PLACE  , contentValues ,
        KEY_ID + "=" + happyPlace.id, null)
        db.close()
        return success


    }
    fun deleteHappyPLace(happyPlace: HappyPlaceModel) : Int{
        val db = this.writableDatabase




        val delete = db.delete(TABLE_HAPPY_PLACE , KEY_ID + "=" + happyPlace.id, null)
        db.close()
        return delete


    }



    @SuppressLint("Range")
    fun getHappyPlacesList(): ArrayList<HappyPlaceModel> {

        // A list is initialize using the data model class in which we will add the values from cursor.
        val happyPlaceList: ArrayList<HappyPlaceModel> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_HAPPY_PLACE" // Database select query

        val db = this.readableDatabase


            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val place = HappyPlaceModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
                    )
                    happyPlaceList.add(place)

                } while (cursor.moveToNext())
            }
            cursor.close()

        return happyPlaceList
    }




}