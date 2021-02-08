package com.example.myapplication1

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blogspot.atifsoftwares.circularimageview.CircularImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.jar.Manifest

class AddScreen : AppCompatActivity() {
    //permission constants
    private val CAMERA_REQUEST_CODE =100;
    private val STORAGE_REQUEST_CODE =101;
    //image pick constants
    private val IMAGE_PICK_CAMERA_CODE =102;
    private val IMAGE_PICK_GALLERY_CODE =103;
    //arrays of permissions
    private lateinit var cameraPermission: Array<String> //camera and storage
    private lateinit var storagePermission: Array<String> //only storage


    //variables that will contain data to save in database
    private var imageUri: Uri? =null
    private var name:String? =""
    private var type:String? =""
    private var ing:String? =""
    private var desc:String? =""
    private var id:String? =""

    private var isEditMode = false

    //actionBar
    private  var actionBar:ActionBar? =null;

    lateinit var dbHelper:MyDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_screen)

        actionBar = supportActionBar
        //tittle of actionbar
        actionBar!!.title = "Add Food Recipe"
        //back button in actionbar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)

        //spinner
        val foodType = resources.getStringArray(R.array.FoodType)
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, foodType
            )
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    findViewById<EditText>(R.id.foodType).setText(spinner.selectedItem.toString())

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }





        //get data from intent
        val intent =intent
        isEditMode = intent.getBooleanExtra("isEditMode",false)
        if (isEditMode){
           //editting data came here from adapter
            actionBar!!.title = "Update Food Details"

            id = intent.getStringExtra("ID")
            name= intent.getStringExtra("NAME")
            imageUri = Uri.parse(intent.getStringExtra("IMAGE"))
            type = intent.getStringExtra("TYPE")
            desc = intent.getStringExtra("DESC")
            ing = intent.getStringExtra("ING")

            //set data to views -> image
            if (imageUri.toString() == "null"){
                //no image
            findViewById<ImageView>(R.id.profileIv).setImageResource(R.drawable.ic_food)
            }
            else{
                //have image
                findViewById<ImageView>(R.id.profileIv).setImageURI(imageUri)
            }
            findViewById<EditText>(R.id.foodName).setText(name)
            findViewById<EditText>(R.id.foodType).setText(type)
            findViewById<EditText>(R.id.foodIng).setText(ing)
            findViewById<EditText>(R.id.foodDesc).setText(desc)
        }
        else{
            //adding new data come here from HomeScreen
            actionBar!!.title = "Add Food Recipe"

        }


        // init dbHelper class
        dbHelper = MyDbHelper(this)

        //init permission arrays
        cameraPermission = arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //click imageview to pick image
        findViewById<CircularImageView>(R.id.profileIv).setOnClickListener {
            //show img pick dialog
            imagePickDialog()
        }
        //click savebtn to save record
        findViewById<FloatingActionButton>(R.id.saveBtn).setOnClickListener {
            inputData()
            finish()
        }

    }

    private fun inputData() {
        //get data
        name = "" + findViewById<EditText>(R.id.foodName).text.toString().trim()
        type = "" + findViewById<EditText>(R.id.foodType).text.toString().trim()
        ing = "" + findViewById<EditText>(R.id.foodIng).text.toString().trim()
        desc = "" + findViewById<EditText>(R.id.foodDesc).text.toString().trim()

        if (isEditMode){
            //editing
            dbHelper?.updateRecord(
                    id = "$id",
                    name = "$name",
                    image = "$imageUri",
                    type = "$type",
                    desc = "$desc",
                    ing = "$ing"
            )
            Toast.makeText(this, "Updated...", Toast.LENGTH_SHORT).show()
        }
        else{
            //adding new
            //save data to db
            val id = dbHelper.insertRecord(
                    name = ""+name,
                    image = ""+imageUri,
                    type = ""+type,
                    desc = ""+desc,
                    ing = ""+ing
            )
            Toast.makeText(this, "Record Added against ID $id", Toast.LENGTH_SHORT).show()
        }
    }

    private fun imagePickDialog() {
        //option to display in dialog
        val options= arrayOf("Camera", "Gallery")
        //dialog
        val builder =AlertDialog.Builder(this)
        //title
        builder.setTitle("Pick Image From")
        //set item options
        builder.setItems(options) { dialog, which ->
            //handle item check
            if (which == 0) {
                //camera clicked
                if (!checkCameraPermission()) {
                    //permission not granted
                    requestCameraPermission()
                }
                else {
                    pickFromCamera()
                }

            } else {
                //gallery closed
                if (!checkStoragePermission()){
                    //permission not granted
                    requestStoragePermission()
                }
                else{
                    //permission already granted
                    pickFromGallery()
                }
            }
        }
        //show dialog
        builder.show()
    }

    private fun pickFromGallery() {
        val galleryIntent =Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*" //only image to be picked
        startActivityForResult(
                galleryIntent,
                IMAGE_PICK_GALLERY_CODE
        )
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission, STORAGE_REQUEST_CODE)
    }

    private fun pickFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Image Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description")
        //put image uri
        imageUri =contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //intent to open camera
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(
                cameraIntent,
                IMAGE_PICK_GALLERY_CODE
        )

    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission, CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermission(): Boolean {
        val results =ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) ==PackageManager.PERMISSION_GRANTED
        val results1 =ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) ==PackageManager.PERMISSION_GRANTED
        return  results && results1
    }

    private fun checkStoragePermission(): Boolean {
        return  ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()// go back to home
        return super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    //if allowed to return true otherwise false
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera()
                    } else {
                        Toast.makeText(this, "Camera and Storage Permission are required", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    //if allowed to return true otherwise false
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted) {
                        pickFromCamera()
                    } else {
                        Toast.makeText(this, "Storage Permission are required", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //image picked from camera or gallery will be received here
        if (resultCode== Activity.RESULT_OK){
            //image is picked
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //pick from gallery
                //crop image
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this)
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //picked from camera
                //crop Image
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this)
            }
            else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                //cropped image received
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK){
                    val resultUri = result.uri
                    imageUri = resultUri
                    //set image
                    findViewById<CircularImageView>(R.id.profileIv).setImageURI(resultUri)
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    //error
                    val error = result.error
                    Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show()

                }

            }
        }





        super.onActivityResult(requestCode, resultCode, data)
    }



}



