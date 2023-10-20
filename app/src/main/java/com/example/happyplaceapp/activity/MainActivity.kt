package com.example.happyplaceapp.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaceapp.R
import com.example.happyplaceapp.Trail.SwipeToDeleteCallback
import com.example.happyplaceapp.Trail.SwipeToEditCallBack
import com.example.happyplaceapp.adapters.HappyPlaceAdapter
import com.example.happyplaceapp.database.databasehandler
import com.example.happyplaceapp.databinding.ActivityMainBinding
import com.example.happyplaceapp.models.HappyPlaceModel

class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding!!.btnAdd.setOnClickListener {
            val intent = Intent(this , Add_Happy_Place::class.java)
            startActivityForResult(intent , ADD_PLACE_ACTIVITY_REQUEST_CODE)

        }
        getHappyPLace()

    }
    private fun getHappyPLace(){
        val dbHandler = databasehandler(this)
        val getHappyPLaceList = dbHandler.getHappyPlacesList()

        if (getHappyPLaceList.size > 0){
            binding!!.recycle.visibility = View.VISIBLE
            binding!!.noRecord.visibility = View.GONE
            setHappyPlaceRecycleView(getHappyPLaceList)

        }else{
            binding!!.recycle.visibility = View.GONE
            binding!!.noRecord.visibility = View.VISIBLE
        }
    }
    private fun setHappyPlaceRecycleView(
        happyPLaceList : ArrayList<HappyPlaceModel>
    ){
        binding!!.recycle.layoutManager= LinearLayoutManager(this)
        binding!!.recycle.setHasFixedSize(true)


        val placeAdapter = HappyPlaceAdapter(this , happyPLaceList  )
        placeAdapter.notifyDataSetChanged()
        binding!!.recycle.adapter = placeAdapter


        placeAdapter.setOnClickListener(object : HappyPlaceAdapter.OnClickListener{
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity , HappyPlaceDetails::class.java)
                intent.putExtra(Extra_place_Detail , model)
                startActivity(intent)
            }
        })
        val editSwipeHandler = object  : SwipeToEditCallBack(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding!!.recycle.adapter as HappyPlaceAdapter
                adapter.notifyEditItem(this@MainActivity , viewHolder.adapterPosition , ADD_PLACE_ACTIVITY_REQUEST_CODE)

            }
        }
        val editItemHelper = ItemTouchHelper(editSwipeHandler)
        editItemHelper.attachToRecyclerView(binding!!.recycle)

        val deleteSwipeHandler = object  : SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding!!.recycle.adapter as HappyPlaceAdapter
               adapter.onRemove(viewHolder.adapterPosition)
                getHappyPLace()

            }
        }
        val deleteItemHandler = ItemTouchHelper(deleteSwipeHandler)
        deleteItemHandler.attachToRecyclerView(binding!!.recycle)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK ){
                getHappyPLace()
            }else{
                Log.e("Activity" , "Cancelled or backPressed")
            }
        }
    }

    companion object{
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        const val Extra_place_Detail = "extra_detail"
    }
}