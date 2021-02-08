package com.example.myapplication1

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.circularimageview.CircularImageView

class AdapterRecord() : RecyclerView.Adapter<AdapterRecord.HolderRecord>(){

    //helper
    //private var dbHelper:MyDbHelper?=null
    lateinit var dbHelper: MyDbHelper


    private var context:Context?=null
    private var recordList:ArrayList<ModelRecord>?=null

    constructor(context: Context?, recordList: ArrayList<ModelRecord>) : this(){
        this.context = context
        this.recordList = recordList

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecord {



    //inflate the layout record
        return HolderRecord(
                LayoutInflater.from(context).inflate(R.layout.content_home_screen, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return recordList!!.size

    }

    override fun onBindViewHolder(holder: HolderRecord, position: Int) {
    //get data, set data, handle clicks
        //get data
        val model = recordList!!.get(position)

        val id = model.id
        val name =model.name
        val image =model.image
        val type = model.type
        val ing = model.ing
        val desc = model.desc

        //set data to views
        holder.nameTv.text = name
        //if does not attach image thn imageUri will be null, so default image in that case
        if (image == "null"){
            //no image in record, set default
            holder.profileIv.setImageResource(R.drawable.ic_food)
        }
        else{
            //have image in record
            holder.profileIv.setImageURI(Uri.parse(image))
        }

        //show record in new activity on clicking record
        holder.itemView.setOnClickListener{
            //pass id to next activity to show record
            val intent = Intent(context, RecordDetailScreen::class.java)
            intent.putExtra("RECORD_ID", id)
            context!!.startActivity(intent)
        }

        //handle more button click show delete/edit options
        holder.moreBtn.setOnClickListener{
            //show more options edit, delete
            //Toast.makeText(context, "btn more clicked", Toast.LENGTH_SHORT).show()
            showMoreOptions(
                    position,
                    id,
                    name,
                    ing,
                    type,
                    desc,
                    image

            )
        }

    }
    //to update or delete options
    private fun showMoreOptions(
            position: Int,
            id: String,
            name: String,
            ing: String,
            type: String,
            desc: String,
            image: String) {
        //options to display in dialog
        val options = arrayOf("Edit","Delete")
        //dialog
        val dialog:AlertDialog.Builder = AlertDialog.Builder(context)
        //set items and click listener
        dialog.setItems(options) { dialog, which ->
            //hand item click
            if (which==0){
                //edit click
                val intent =Intent(context, AddScreen::class.java)
                intent.putExtra("ID",id)
                intent.putExtra("NAME",name)
                intent.putExtra("IMAGE",image)
                intent.putExtra("TYPE",type)
                intent.putExtra("DESC",desc)
                intent.putExtra("ING",ing)
                intent.putExtra("isEditMode",true)
                context!!.startActivity(intent)
            } else{
                //delete clicked
                //init db helper
                dbHelper = MyDbHelper(context)

                dbHelper.deleteRecord(id)
                (context as HomeScreen)!!.onResume();
            }
        }
        //show dialog
        dialog.show()

    }



    inner class HolderRecord(itemView: View): RecyclerView.ViewHolder(itemView){
        //views from content_home.xml
        var profileIv:ImageView = itemView.findViewById(R.id.profileIv)
        var nameTv: TextView = itemView.findViewById(R.id.nameTv)
        //var ingTv: TextView = itemView.findViewById(R.id.ingTv)
        //var descTv: TextView = itemView.findViewById(R.id.descTv)
        var moreBtn:ImageButton = itemView.findViewById(R.id.moreBtn)

    }


}