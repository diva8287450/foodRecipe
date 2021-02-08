package com.example.myapplication1

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.blogspot.atifsoftwares.circularimageview.CircularImageView

class RecordDetailScreen : AppCompatActivity() {


    //actionbar
    private var actionBar: ActionBar?=null

    //helper
    private var dbHelper:MyDbHelper?=null

    private  var recordId:String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_detail_screen)

        //setting up actionbar
        actionBar = supportActionBar
        actionBar!!.title ="Food Details"
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        //init db helper
        dbHelper =MyDbHelper(this)

        //get record id from intent
        val intent = intent
        recordId = intent.getStringExtra("RECORD_ID")
        Toast.makeText(this, "Database ID: "+recordId, Toast.LENGTH_SHORT).show()


        showRecordDetails()



    }

    private fun showRecordDetails() {
    //get record details

        val selectQuery =" SELECT * FROM ${Constants.TABLE_NAME} WHERE ${Constants.C_ID } =\"$recordId\""

        val db = dbHelper!!.writableDatabase
        val cursor =db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()){
            do {
                val id = "" + cursor.getInt(cursor.getColumnIndex(Constants.C_ID))
                val name = "" + cursor.getString(cursor.getColumnIndex(Constants.C_NAME))
                val image = "" + cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE))
                val type = "" + cursor.getString(cursor.getColumnIndex(Constants.C_TYPE))
                val desc = "" + cursor.getString(cursor.getColumnIndex(Constants.C_DESC))
                val ing = "" + cursor.getString(cursor.getColumnIndex(Constants.C_ING))

                //set data
                findViewById<TextView>(R.id.nameTv).text =name
                findViewById<TextView>(R.id.descTv).text =desc
                findViewById<TextView>(R.id.ingTv).text =ing

                //findViewById<TextView>(R.id.foodNameTv).text =name
                //findViewById<TextView>(R.id.foodNameTv).text =name
                //findViewById<TextView>(R.id.foodNameTv).text =name

                //if does not attach image thn imageUri will be null, so default image in that case
                if (image == "null"){
                    //no image in record, set default
                    findViewById<ImageView>(R.id.profileIv).setImageResource(R.drawable.ic_food_white)
                }
                else{
                    //have image in record
                    findViewById<ImageView>(R.id.profileIv).setImageURI(Uri.parse(image))
                }


            }while (cursor.moveToNext())

        }
        //close db collection
        db.close()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}