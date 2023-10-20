package com.example.happyplaceapp.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaceapp.R
import com.example.happyplaceapp.activity.Add_Happy_Place
import com.example.happyplaceapp.activity.MainActivity
import com.example.happyplaceapp.database.databasehandler
import com.example.happyplaceapp.models.HappyPlaceModel

open class HappyPlaceAdapter(
    private val context : Context,
    private val list : ArrayList<HappyPlaceModel>
)  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.items_happy_place,
                parent,
                false
            )
        )
    }
    fun setOnClickListener(onClickListener : OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder){


            val imageUri = Uri.parse(model.image)
            holder.itemView.findViewById<ImageView>(R.id.place_image).setImageURI(imageUri)

                holder.itemView.findViewById<TextView>(R.id.tittle).text = model.title
                holder.itemView.findViewById<TextView>(R.id.des).text = model.description

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick(position , model)
                }
            }


        }
    }
    fun notifyEditItem(activity: Activity , position: Int , requestCode : Int){
        val intent  = Intent(context , Add_Happy_Place::class.java)
        intent.putExtra(MainActivity.Extra_place_Detail , list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }
    fun onRemove(position: Int){
       val dbHandler = databasehandler(context)
        val isDelete  = dbHandler.deleteHappyPLace(list[position])
        if (isDelete > 0){
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    interface OnClickListener{
        fun onClick(position: Int , model: HappyPlaceModel)
    }
    private class  MyViewHolder(view : View):RecyclerView.ViewHolder(view)
}