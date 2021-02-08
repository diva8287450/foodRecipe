package com.example.myapplication1

object Constants {

    //dBName
    const val DB_NAME ="MY_RECORDS_DB"
    const val DB_VERSION =1
    //table name
    const val TABLE_NAME ="MY_RECORDS_TABLE"
    //columns/fields of table
    const val C_ID ="ID"
    const val C_NAME ="NAME"
    const val C_IMAGE ="IMAGE"
    const val C_TYPE ="TYPE"
    const val C_DESC ="DESC"
    const val C_ING ="ING"

    // CREATE TABLE QUERRY
    const val CREATE_TABLE=(
            "CREATE TABLE " + TABLE_NAME + "("
            + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + C_NAME + " TEXT,"
            + C_IMAGE + " TEXT,"
            + C_TYPE + " TEXT,"
            + C_DESC + " TEXT,"
            + C_ING + " TEXT"
            + ")"

    )








}