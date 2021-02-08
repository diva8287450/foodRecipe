package com.example.myapplication1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


//database helper class that contain all CRUD methods
class MyDbHelper(context: Context?): SQLiteOpenHelper(
    context,
    Constants.DB_NAME,
    null,
    Constants.DB_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        //create table on db
            db.execSQL(Constants.CREATE_TABLE)
        }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //upgrade db if got changes
        //drop older table if exist

        db.execSQL("DROP TABLE IF EXISTS" + Constants.TABLE_NAME)
        onCreate(db)

    }

//insert record to db
    fun insertRecord(
        name:String?,
        image:String?,
        type:String?,
        desc:String?,
        ing:String?
    ):Long{
    //get variable database to write data
    val db =this.writableDatabase
    val values = ContentValues()
    //id will be inset automatically AUTOINCREMENT
    //insert data
    values.put (Constants.C_NAME, name)
    values.put (Constants.C_IMAGE, image)
    values.put (Constants.C_TYPE, type)
    values.put (Constants.C_DESC, desc)
    values.put (Constants.C_ING, ing)

    //insert row will return record id of saved record
    val id = db.insert(Constants.TABLE_NAME, null, values)
    //close db connection
    db.close()
    return id
    }


    //update record to db
    fun updateRecord(id:String,
            name:String?,
            image:String?,
            type:String?,
            desc:String?,
            ing:String?):Long
    {
        //get variable database to write data
        val db = this.writableDatabase
        val values = ContentValues()

        //insert data
        values.put(Constants.C_NAME, name)
        values.put(Constants.C_IMAGE, image)
        values.put(Constants.C_TYPE, type)
        values.put(Constants.C_DESC, desc)
        values.put(Constants.C_ING, ing)

        //update
        return db.update(Constants.TABLE_NAME,
        values,
        "${Constants.C_ID}=?",
        arrayOf(id)).toLong()
        //*****************************db.close()
    }

    //get total number of records
    fun recordCount():Int{
        val countQuery = " SELECT * FROM ${Constants.TABLE_NAME}"
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        val count = cursor.count
        cursor.close()
        return count
    }

    //delete(single) record using record id
    fun deleteRecord(id:String){
        val db =writableDatabase
        db.delete(
                Constants.TABLE_NAME,
                "${Constants.C_ID} = ?",
                arrayOf(id)
        )
        db.close()
    }

    //delete all records
    fun deleteAllRecords(){
        val db = writableDatabase
        db.execSQL(" DELETE FROM ${Constants.TABLE_NAME}")
        db.close()

    }









    //get all data
    fun getAllRecords(orderBy:String):ArrayList<ModelRecord>{
        //oderby querry will allow to sort data e.g oldest first, name ascending/descending
        //it will return list of records since we have used return type ArrayList<ModelRecord>
        val recordList =ArrayList<ModelRecord>()
        //query to select all records
        val selectQuery = " SELECT * FROM ${Constants.TABLE_NAME} ORDER BY $orderBy "


        val db = this.writableDatabase
        val cursor =db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()){
            do {
                val modelRecord =ModelRecord(
                        id = ""+cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                        name = ""+cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                        image = ""+cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                        type = ""+cursor.getString(cursor.getColumnIndex(Constants.C_TYPE)),
                        desc = ""+cursor.getString(cursor.getColumnIndex(Constants.C_DESC)),
                        ing = ""+cursor.getString(cursor.getColumnIndex(Constants.C_ING))
                )
                //add record to list
                recordList.add(modelRecord)
            }while (cursor.moveToNext())
        }
        //close db connection
        db.close()
        //return to the queried result List
        return recordList
    }

    //search data
    fun searchRecords(query:String):ArrayList<ModelRecord>{
        //it will return list of records since we have used return type ArrayList<ModelRecord>
        val recordList =ArrayList<ModelRecord>()
        //query to select all records
        val selectQuery = " SELECT * FROM ${Constants.TABLE_NAME} WHERE ${Constants.C_NAME} LIKE '%$query%' "
        val db = this.writableDatabase
        val cursor =db.rawQuery(selectQuery, null)
        //looping through all records and add to list
        if (cursor.moveToFirst()){
            do {
                val modelRecord =ModelRecord(
                        id = ""+cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                        name = ""+cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                        image = ""+cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                        type = ""+cursor.getString(cursor.getColumnIndex(Constants.C_TYPE)),
                        desc = ""+cursor.getString(cursor.getColumnIndex(Constants.C_DESC)),
                        ing = ""+cursor.getString(cursor.getColumnIndex(Constants.C_ING))
                )
                //add record to list
                recordList.add(modelRecord)
            }while (cursor.moveToNext())
        }
        //close db connection
        db.close()
        //return to the queried result List
        return recordList
    }

    //filter records by type
    fun filterRecords(query:String):ArrayList<ModelRecord>{
        //it will return list of records since we have used return type ArrayList<ModelRecord>
        val recordList =ArrayList<ModelRecord>()
        //query to select all records
        val selectQuery = " SELECT * FROM ${Constants.TABLE_NAME} WHERE ${Constants.C_TYPE} LIKE '%$query%' "
        val db = this.writableDatabase
        val cursor =db.rawQuery(selectQuery, null)
        //looping through all records and add to list
        if (cursor.moveToFirst()){
            do {
                val modelRecord =ModelRecord(
                        id = ""+cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                        name = ""+cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                        image = ""+cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                        type = ""+cursor.getString(cursor.getColumnIndex(Constants.C_TYPE)),
                        desc = ""+cursor.getString(cursor.getColumnIndex(Constants.C_DESC)),
                        ing = ""+cursor.getString(cursor.getColumnIndex(Constants.C_ING))
                )
                //add record to list
                recordList.add(modelRecord)
            }while (cursor.moveToNext())
        }
        //close db connection
        db.close()
        //return to the queried result List
        return recordList
    }



}