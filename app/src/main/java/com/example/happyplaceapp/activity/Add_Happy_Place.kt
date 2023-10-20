package com.example.happyplaceapp.activity

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.happyplaceapp.R
import com.example.happyplaceapp.adapters.HappyPlaceAdapter
import com.example.happyplaceapp.database.databasehandler
import com.example.happyplaceapp.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplaceapp.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


class Add_Happy_Place : AppCompatActivity()  , View.OnClickListener{
    private var savedImagePath : String? = null

    private var cal = Calendar.getInstance()

    private var binding : ActivityAddHappyPlaceBinding? = null

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    private var saveImageToInternalStorage: Uri? = null

    private var mLatitude: Double = 0.0 // A variable which will hold the latitude value.
    private var mLongitude: Double = 0.0 // A variable which will hold the longitude value.

    private var happyPlaceDetails : HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.tool)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding!!.tool.setNavigationOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra(MainActivity.Extra_place_Detail)){
            happyPlaceDetails  = intent.getSerializableExtra(MainActivity.Extra_place_Detail) as HappyPlaceModel
        }

        dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                updateDateInView()
            }

        updateDateInView() // Here the calender instance what we have created before will give us the current date which is formatted in the format in function

        if (happyPlaceDetails != null){
            supportActionBar!!.title = "Edit Happy Place"
            binding!!.tittle.setText(happyPlaceDetails!!.title)
            binding!!.description.setText(happyPlaceDetails!!.description)
            binding!!.date.setText(happyPlaceDetails!!.date)
            binding!!.location.setText(happyPlaceDetails!!.location)
            mLatitude = happyPlaceDetails!!.latitude
            mLongitude = happyPlaceDetails!!.longitude


            savedImagePath = happyPlaceDetails!!.image
            binding!!.image.setImageURI(Uri.parse(savedImagePath))
            binding!!.btnSave.text = "Update"
        }


        binding!!.date.setOnClickListener(this)
        binding!!.btnAdd.setOnClickListener(this)
        binding!!.btnSave.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.date -> {
                DatePickerDialog(
                    this@Add_Happy_Place,
                    dateSetListener, // This is the variable which have created globally and initialized in setupUI method.
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR), // Here the cal instance is created globally and used everywhere in the class where it is required.
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            R.id.btnAdd -> {
                val pictureDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from gallery", "Capture photo from camera")
                pictureDialog.setItems(
                    pictureDialogItems
                ) { dialog, which ->
                    when (which) {
                        // Here we have create the methods for image selection from GALLERY
                        0 -> choosePhotoFromGallery()
                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }

            R.id.btnSave -> {



                when {
                    binding!!.tittle.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
                    }
                    binding!!.description.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT)
                            .show()
                    }
                    binding!!.location.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please select location", Toast.LENGTH_SHORT)
                            .show()
                    }
                    savedImagePath == null -> {
                        Toast.makeText(this, "Please add image", Toast.LENGTH_SHORT).show()
                    }
                     else -> {




                         // Assigning all the values to data model class.
                        val happyPlaceModel = HappyPlaceModel(
                            if (happyPlaceDetails == null) 0 else happyPlaceDetails!!.id ,
                            binding!!.tittle.text.toString(),
                            savedImagePath.toString(),
                            binding!!.description.text.toString(),
                            binding!!.date.text.toString(),
                            binding!!.location.text.toString(),
                            mLatitude,
                            mLongitude
                        )

                        // Here we initialize the database handler class.
                        val dbHandler = databasehandler(this)
                         if (happyPlaceDetails == null){

                          val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)
                             if (addHappyPlace > 0) {
                                 setResult(Activity.RESULT_OK)
                                 finish()
                             }
                         }else{
                             val updateHappyPlace = dbHandler.updateHappyPlace(happyPlaceModel)
                             if(updateHappyPlace > 0){
                                 setResult(Activity.RESULT_OK)
                                 finish()
                             }
                         }








                    }

                }

           }



        }
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        // Here this is used to get an bitmap from URI
                        @Suppress("DEPRECATION")
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                        val bitmap: Bitmap = selectedImageBitmap
                        val fileName = "my_image.jpg" // Name for the saved image
                        savedImagePath = saveImageToInternalStorage(bitmap)


                        binding!!.image.setImageBitmap(selectedImageBitmap) // Set the selected image from GALLERY to imageView.
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@Add_Happy_Place, "Failed!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else if (requestCode == CAMERA) {

                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap // Bitmap from camera

                val bitmap: Bitmap = thumbnail
                val fileName = "my_image.jpg" // Name for the saved image
               savedImagePath = saveImageToInternalStorage(bitmap)


                binding!!.image.setImageBitmap(thumbnail) // Set to the imageView.
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    /**
     * A function to update the selected date in the UI with selected format.
     * This function is created because every time we don't need to add format which we have added here to show it in the UI.
     */
    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault()) // A date format
        binding!!.date.setText(sdf.format(cal.time).toString()) // A selected date using format which we have used is set to the UI.
    }

    /**
     * A method is used for image selection from GALLERY / PHOTOS of phone storage.
     */
    private fun choosePhotoFromGallery() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_MEDIA_IMAGES)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                /* ... */
                   val galleryIntent = Intent(
                       Intent.ACTION_PICK ,
                       MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                   )
                    startActivityForResult(galleryIntent , GALLERY)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(this@Add_Happy_Place , "Permission Denied" , Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                showRationalDialogForPermissions()
                }
            }).check()
    }

    /**
     * A method is used  asking the permission for camera and storage and image capturing and selection from Camera.
     */
    private fun takePhotoFromCamera() {

        /**Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // Here after all the permission are granted launch the CAMERA to capture an image.
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, CAMERA)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()**/
        Dexter.withContext(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    /* ... */
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent , CAMERA)

                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(this@Add_Happy_Place , "Permission Denied" , Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    showRationalDialogForPermissions()
                }
            }).check()
    }

    /**
     * A function used to show the alert dialog when the permissions are denied and need to allow it from settings app info.
     */
    private fun showRationalDialogForPermissions() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }

    /**
     * A function to save a copy of an image to internal storage for HappyPlaceApp to use.
     */
    private fun saveImageToInternalStorage(bitmap: Bitmap): String {

        // Get the context wrapper instance
        val wrapper = ContextWrapper(applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        /**
         * The Mode Private here is
         * File creation mode: the default mode, where the created file can only
         * be accessed by the calling application (or all applications sharing the
         * same user ID).
         */
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
            Toast.makeText(this , "Image saved" , Toast.LENGTH_SHORT).show()
        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
            Toast.makeText(this , "Image not saved" , Toast.LENGTH_SHORT).show()
        }

        // Return the saved image uri
        return file.absolutePath
    }
    private fun saveImageToExternalStorage(context: Context, bitmap: Bitmap, fileName: String): String? {
        if (isExternalStorageWritable()) {
            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY)
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, fileName)
            return try {
                val outputStream: OutputStream = FileOutputStream(file)
                context.contentResolver.openOutputStream(Uri.fromFile(file))?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                outputStream.flush()
                outputStream.close()
                file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the error gracefully, e.g., log it for debugging
                Toast.makeText(this , "did this work ", Toast.LENGTH_SHORT).show()
                null
            }
        }
        return null
    }

    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }
}