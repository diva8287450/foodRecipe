package com.example.myapplication1

//import android.widget.SearchView
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import www.sanju.motiontoast.MotionToast

class HomeScreen : AppCompatActivity() {


    //db helper
    lateinit var dbHelper: MyDbHelper

    //orderby/sor queries
    private val NEWEST_FIRST =  " ${Constants.C_ID} DESC"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        setSupportActionBar(findViewById(R.id.toolbar))


        //init db helper
        dbHelper = MyDbHelper(this)

        loadRecords()
        //Toast.makeText(this, "load records done", Toast.LENGTH_SHORT).show()

        // click floating btn to add record
        findViewById<FloatingActionButton>(R.id.addRecordBtn).setOnClickListener {
            val intent =Intent(this, AddScreen::class.java)
            intent.putExtra("isEditMode",false) //want to add new record set it false
            startActivity(intent)
        }
    }

    private fun loadRecords() {
       val adapterRecord =AdapterRecord(this, dbHelper.getAllRecords((NEWEST_FIRST)))

        findViewById<RecyclerView>(R.id.recordsRv).adapter = adapterRecord
        //Toast.makeText(this, "load records works", Toast.LENGTH_SHORT).show()
    }

    private fun searchRecords(query:String) {
        val adapterRecord =AdapterRecord(this, dbHelper.searchRecords((query)))

        findViewById<RecyclerView>(R.id.recordsRv).adapter = adapterRecord
    }
    private fun filterRecords(query:String) {
        val adapterRecord =AdapterRecord(this, dbHelper.filterRecords((query)))

        findViewById<RecyclerView>(R.id.recordsRv).adapter = adapterRecord
    }


    public override fun onResume() {
        super.onResume()
        loadRecords()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //inflate menu
        menuInflater.inflate(R.menu.menu_home, menu)

        //searchview
        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView

        searchView.setOnQueryTextListener(object :
        SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //search whn btn on keyboard is clicked
                if (query != null) {
                    searchRecords(query)
                            //Toast.makeText(applicationContext, "search works"+query, Toast.LENGTH_SHORT).show()
                }
                //Toast.makeText(applicationContext, "next", Toast.LENGTH_SHORT).show()
                return true
                //Toast.makeText(applicationContext, "done submit", Toast.LENGTH_SHORT).show()

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //search as you type
                if (newText != null) {
                    searchRecords(newText)
                }
                return true
            }

        }
        )


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle menu item click
        val id = item.itemId
        if (id==R.id.action_type){
            sortDialog()
        }
        else{
            //more menu btn function
        }


        return super.onOptionsItemSelected(item)
    }

    private fun sortDialog() {
        val options = arrayOf("Drinks", "Bread", "Cookies", "Cake", "Rice", "All")
        //dialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Food Type")
                .setItems(options){_, which ->
                    //handle filters
                    when (which) {
                        0 -> {
                            filterRecords("Drinks")
                        }
                        1 -> {
                            filterRecords("Bread")
                        }
                        2 -> {
                            filterRecords("Cookies")
                        }
                        3 -> {
                            filterRecords("Cake")
                        }
                        4 -> {
                            filterRecords("Rice")
                        }
                        5 ->{
                            loadRecords()
                        }
                    }

                }
                .show()
    }


}