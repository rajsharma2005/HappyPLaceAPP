package com.example.happyplaceapp.activity

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaceapp.R
import com.example.happyplaceapp.databinding.ActivityHappyPlaceDetailsBinding
import com.example.happyplaceapp.models.HappyPlaceModel

class HappyPlaceDetails : AppCompatActivity() {
    private var binding : ActivityHappyPlaceDetailsBinding? = null
    private var happyPlaceModel : HappyPlaceModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailsBinding.inflate(layoutInflater)

        setContentView(binding!!.root)

        if (intent.hasExtra(MainActivity.Extra_place_Detail)){
            happyPlaceModel = intent.getSerializableExtra(MainActivity.Extra_place_Detail) as HappyPlaceModel
        }
        if (happyPlaceModel != null){
            setSupportActionBar(binding!!.tool)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlaceModel!!.title
            binding!!.tool.setNavigationOnClickListener {
                onBackPressed()
            }
        }
        binding!!.ivPlaceImageDetails.setImageURI(Uri.parse(happyPlaceModel!!.image))
        binding!!.tvDescription.text = happyPlaceModel!!.description
        binding!!.tvLocation.text = happyPlaceModel!!.location
        binding!!.tvDate.text = happyPlaceModel!!.date
    }
}