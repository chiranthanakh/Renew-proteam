package com.proteam.renew.views

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.proteam.renew.R
import com.proteam.renew.responseModel.LocationResponse
import com.proteam.renew.responseModel.LocationResponseItem
import com.proteam.renew.responseModel.statesResponse
import com.proteam.renew.responseModel.workersListResponsce
import com.proteam.renew.utilitys.OnResponseListener
import com.proteam.renew.utilitys.WebServices
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.Calendar
import java.util.Locale


class WorkerInformationActivity : AppCompatActivity(), OnResponseListener<Any>, DatePickerDialog.OnDateSetListener {

    val edt_employee_name: EditText by lazy { findViewById<EditText>(R.id.edt_employee_name) }
    val edt_guardian_name: EditText by lazy { findViewById<EditText>(R.id.edt_guardian_name) }
    val edt_dob: EditText by lazy { findViewById<EditText>(R.id.edt_dob) }
    val sp_gender: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_gender) }
    val edt_phone_number: EditText by lazy { findViewById<EditText>(R.id.edt_phone_number) }
    val edt_emergency_contact_Name: EditText by lazy { findViewById<EditText>(R.id.edt_emergency_contact_Name) }
    val nationality: EditText by lazy { findViewById<EditText>(R.id.nationality) }
    val sp_blood_group: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_blood_group) }
    val edt_address: EditText by lazy { findViewById<EditText>(R.id.edt_address) }
    val sp_state: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_state) }
    val sp_location: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_location) }
    val edt_pincode: EditText by lazy { findViewById<EditText>(R.id.edt_pincode) }
    val tv_next_one: TextView by lazy { findViewById<TextView>(R.id.tv_next_one) }
    val edt_emergency_contactNumber: EditText by lazy { findViewById<EditText>(R.id.edt_emergency_contactNumber) }
    val iv_profile_image: ImageView by lazy { findViewById(R.id.iv_nav_image) }
    val iv_back: ImageView by lazy { findViewById(R.id.iv_nav_view) }


    var progressDialog: ProgressDialog? = null
    var stateList = ArrayList<String>()
    var locationList = ArrayList<String>()
    var locatinmaping = ArrayList<LocationResponseItem>()
    var genderList = ArrayList<String>()
    var bloodgroup = ArrayList<String>()
    var profileuri : String = ""
    private val MAX_FILE_SIZE_BYTES = 2 * 1024 * 1024

    var statemap = HashMap<String, String>()
    var locationmap = HashMap<String, String>()
    var statemapreverse = HashMap<String, String>()
    var locationmapreverse = HashMap<String, String>()
    private lateinit var bottomNavigationView: BottomNavigationView


    val Emp_Name = "employee_name"
    val Guardian_Name = "guardian_name"
    val Dob = "Dob"
    val Gender = "sp_gender"
    val PhoneNumber = "phone_number"
    val emergency_name = "emergency_contact_name"
    val Nationality = "nationality"
    val Blood_group = "Blood_group"
    val Address = "address"
    val State = "state"
    val Location = "location"
    val Pincode = "pincode"
    val emergency_contactNumber = "emergency_contactNumber"
    var userid: String? = ""
    var rollid: String? = ""
    var type: Boolean? = false
    var addnew: Boolean? = false
    var imageUri: Uri? = null
    var stateid: String? = ""
    var locationid: String?=""
    var profileimage: String? = ""
    var profileimageurl: String? =""
    var oldprofileimeage: String? = ""
    var workerid: String?= ""
    private val calendar: Calendar = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_information)

        addlists()
        callstatedApi()
        val sharedPreferences: SharedPreferences =getSharedPreferences("workerPref", Context.MODE_PRIVATE)!!
        type = sharedPreferences.getBoolean("edit", false)!!
        addnew = sharedPreferences.getBoolean("imageinsert", false)!!

        if(type == true){
            getworkerdetails()
        }
        callworkerapi()
        initilize()
        edt_dob.inputType = InputType.TYPE_NULL
        edt_dob.setOnClickListener(View.OnClickListener
        {
            datePicker_diaog()
        })

    }

    private fun seteditablefortext() {
        edt_employee_name.inputType = InputType.TYPE_NULL
        edt_dob.inputType = InputType.TYPE_NULL
        edt_dob.isClickable = false
        edt_guardian_name.inputType = InputType.TYPE_NULL
        edt_emergency_contact_Name.inputType = InputType.TYPE_NULL
        edt_pincode.inputType = InputType.TYPE_NULL
        edt_emergency_contactNumber.inputType = InputType.TYPE_NULL
        edt_address.inputType = InputType.TYPE_NULL
        edt_phone_number.inputType = InputType.TYPE_NULL
        sp_location.inputType = InputType.TYPE_NULL
        sp_location.isEnabled = false
        sp_location.isClickable = false
        sp_state.isEnabled = false
        sp_state.isClickable = false
        sp_state.isFocusable = false
        edt_dob.isEnabled = false
        nationality.inputType = InputType.TYPE_NULL
        edt_dob.isFocusable = false
        sp_gender.isEnabled = false
    }

    private fun initilize() {
        tv_next_one.setOnClickListener {
            validateall()
        }
        iv_back.setOnClickListener {
            finish()
        }
        iv_profile_image.setOnClickListener {
            Log.d("clickprofileimage","clicked")
            showImagePickerDialog(this)
        }
       // cleareditTexts()
        sp_state.setOnClickListener{
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, stateList)
            sp_state.setAdapter(adapter)
        }

        edt_dob.setOnClickListener{
          //  showDatePickerDialog()
        }
        sp_location.setOnClickListener{
            getlocations()
        }

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, stateList)
        sp_state.setAdapter(adapter)

        val adapter2 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, locationList)
        sp_location.setAdapter(adapter2)

        val adapter3 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, genderList)
        sp_gender.setAdapter(adapter3)

        val adapter4 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, bloodgroup)
        sp_blood_group.setAdapter(adapter4)
    }

    private fun getlocations() {
        var stateid = statemap.get(sp_state.text.toString())
        var locationlistid = ArrayList<String>()
        for(x in locatinmaping){
            if(x.state_id == stateid){
                locationlistid.add(x.city_name)
            }
        }
        if(locationlistid.isEmpty()){
            Toast.makeText(this, "Please select state first", Toast.LENGTH_SHORT)
                .show()
        }
        val adapter2 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, locationlistid)
        sp_location.setAdapter(adapter2)
    }


    private fun validateall() {
        val edt_Dob: String = edt_dob.text.toString()
        val currentDate = LocalDate.now()
        var age: Int = 0
        var dateOfBirth: LocalDate?
        var dates = edt_Dob.split("-")
        if (!TextUtils.isEmpty(edt_Dob) && dates.size == 3) {
            var year = if (dates.get(0).length == 4) {
                dates.get(0)
            } else {
                dates.get(2)
            }
            var day = if (dates.get(2).length == 2) {
                dates.get(2)
            } else {
                dates.get(0)
            }
            dateOfBirth = LocalDate.of(year.toInt(), dates.get(1).toInt(), day.toInt())
            age = Period.between(dateOfBirth, currentDate).years
        }

        // Shared Pref saving and getting text in fields
        val edt_Employee_name: String = edt_employee_name.text.toString()
        val edt_guardian_name: String = edt_guardian_name.text.toString()
        val sp_gender1: String = sp_gender.text.toString()
        val edt_phone_number1: String = edt_phone_number.text.toString()
        val edt_Emergency_contact_Name: String = edt_emergency_contact_Name.text.toString()
        val nationality1: String = nationality.text.toString()
        val sp_blood_group: String = sp_blood_group.text.toString()
        val edt_address1: String = edt_address.text.toString()
        val sp_state1: String = sp_state.text.toString()
        val sp_location1: String = sp_location.text.toString()
        val edt_pincode1: String = edt_pincode.text.toString()
        val edt_emergency_contactNumber1: String = edt_emergency_contactNumber.text.toString()


        if (!TextUtils.isEmpty(edt_Employee_name)) {
            if (!TextUtils.isEmpty(edt_Dob) && age >= 17) {
                if (!TextUtils.isEmpty(edt_phone_number1) && edt_phone_number1.length == 10) {
                   // if (!TextUtils.isEmpty(edt_Emergency_contact_Name)) {
                        if (!TextUtils.isEmpty(nationality1)) {
                            if (!TextUtils.isEmpty(edt_address1)) {
                                if (TextUtils.isEmpty(edt_pincode1) || edt_pincode1.length == 6) {
                                    if (!TextUtils.isEmpty(sp_gender1)) {
                                        if (!TextUtils.isEmpty(edt_emergency_contactNumber1) && edt_emergency_contactNumber1.length == 10) {
                                            if (!TextUtils.isEmpty(sp_state1)) {
                                                if (!TextUtils.isEmpty(sp_location1)) {
                                                    if(!TextUtils.isEmpty(profileimage)) {
                                                        val sharedPreferences: SharedPreferences =
                                                            getSharedPreferences(
                                                                "WorkerInfoPref",
                                                                Context.MODE_PRIVATE
                                                            )!!
                                                        val editor: SharedPreferences.Editor =
                                                            sharedPreferences.edit()
                                                        editor.putString(
                                                            Emp_Name,
                                                            edt_Employee_name
                                                        )
                                                        editor.putString(
                                                            Guardian_Name,
                                                            edt_guardian_name
                                                        )
                                                        editor.putString(Dob, edt_Dob)
                                                        editor.putString(Gender, sp_gender1)
                                                        editor.putString(
                                                            PhoneNumber,
                                                            edt_phone_number1
                                                        )
                                                        editor.putString(
                                                            emergency_name,
                                                            edt_Emergency_contact_Name
                                                        )
                                                        editor.putString(Nationality, nationality1)
                                                        editor.putString(
                                                            Blood_group,
                                                            sp_blood_group
                                                        )
                                                        editor.putString(Address, edt_address1)
                                                        editor.putString(
                                                            State,
                                                            statemap.get(sp_state1)
                                                        )
                                                        editor.putString(
                                                            Location,
                                                            locationmap.get(sp_location1)
                                                        )
                                                        editor.putString(Pincode, edt_pincode1)
                                                        editor.putString(
                                                            emergency_contactNumber,
                                                            edt_emergency_contactNumber1
                                                        )
                                                        editor.putString(
                                                            "oldprofile",
                                                            oldprofileimeage
                                                        )
                                                        editor.putString("profile","" )

                                                        editor.putString("profileuri", profileuri)

                                                        Log.d("profileview",profileimage.toString())
                                                        editor.commit()
                                                        val intent = Intent(
                                                            applicationContext,
                                                            WorkerInformationNext1Activity::class.java
                                                        )
                                                        startActivity(intent)
                                                    } else {
                                                        Toast.makeText(this, "please Upload profile image", Toast.LENGTH_SHORT)
                                                            .show()
                                                    }
                                                } else {
                                                   // sp_location.error = "select location"
                                                    Toast.makeText(this, "Please Select District", Toast.LENGTH_SHORT)
                                                        .show()
                                                }
                                            } else {
                                               // sp_state.error = "select State"
                                                Toast.makeText(this, "Please select State", Toast.LENGTH_SHORT)
                                                    .show()
                                            }
                                        } else {
                                            if(edt_emergency_contactNumber1.isEmpty()){
                                               // edt_emergency_contactNumber.error = "Enter valid Phone number"
                                            }else{
                                                //edt_emergency_contactNumber.error = "Emergency contact number must be 10 digits"
                                            }
                                            Toast.makeText(this, "Please enter Valid emergency contact number", Toast.LENGTH_SHORT)
                                                .show()

                                        }
                                    } else {
                                        //sp_gender.error = "Select Gender"
                                        Toast.makeText(this, "please Select Gender",
                                                Toast.LENGTH_SHORT).show()

                                    }

                                } else {
                                   // edt_pincode.error = "Enter valid address"
                                    Toast.makeText(this, "Please enter Valid PINCODE", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } else {
                                //edt_address.error = "Enter address"
                                Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                           // nationality.error = "Enter Nationality"
                            Toast.makeText(this, "Please enter nationality", Toast.LENGTH_SHORT)
                                .show()
                        }

                    /*} else {
                       // edt_emergency_contact_Name.error = "Please emergency contact name"
                        Toast.makeText(this, "please enter emergency contact name", Toast.LENGTH_SHORT)
                            .show()
                    }*/
                } else {
                    if(edt_phone_number1.length == 0){
                      //  edt_phone_number.error = "Enter Phone number"
                    }else{
                       // edt_phone_number.error = "Phone number must be 10 digits"
                    }
                    Toast.makeText(this, "Please enter valid Phone number", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                if(TextUtils.isEmpty(edt_Dob)){
                    edt_dob.error = "Required DOB"
                    Toast.makeText(this, "Please select DOB", Toast.LENGTH_SHORT)
                        .show()
                }else if(age < 18 ){
                    edt_dob.error = "Age must be above 18 years"
                    Toast.makeText(this, "Employee age must be more than 18 years", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            edt_employee_name.error = "Require Employee Name"
            Toast.makeText(this, "please enter employee name", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun editblock(){
       /* if(){

        }*/
    }

    private fun callstatedApi() {
        GlobalScope.launch(Dispatchers.IO) {
            GlobalScope.launch(Dispatchers.IO) {
                val webServices = WebServices<Any>(this@WorkerInformationActivity)
                webServices.states(WebServices.ApiType.states)

                val webServices2 = WebServices<Any>(this@WorkerInformationActivity)
                webServices2.location(WebServices.ApiType.location)
            }
        }
    }

    private fun callworkerapi() {
        GlobalScope.launch(Dispatchers.IO) {
            GlobalScope.launch(Dispatchers.IO) {
                if(workerid != ""){
                    val webServices = WebServices<Any>(this@WorkerInformationActivity)
                    webServices.viewWorker(WebServices.ApiType.viewworker, workerid)
                }
            }
        }
    }

    private fun getworkerdetails() {
            val sharedPreferences: SharedPreferences =
                getSharedPreferences("updateworker", Context.MODE_PRIVATE)!!
            val test = sharedPreferences.getString("Emp_Name", "")
            edt_employee_name.setText(test)!!
            edt_guardian_name.setText(sharedPreferences.getString("guardian_name", ""))!!
            var dob = sharedPreferences.getString("Dob", "")!!
            val approvalstatus = sharedPreferences.getString("approvalstatus", "")
            if(approvalstatus == "1"){
                seteditablefortext()
            }

        var dates = dob?.split("-")
            if (dates?.get(0)?.length == 4) {
                dob = dates.get(2) + "-" + dates.get(1) + "-" + dates.get(0)
            } else {
                if (dates?.size == 3) {
                    dob = dates?.get(0) + "-" + dates?.get(1) + "-" + dates?.get(2)
                }
            }
        workerid = sharedPreferences.getString("workerid", "")!!

        edt_dob.setText(dob)
            sp_gender.setText(sharedPreferences.getString("sp_gender", ""))!!
            edt_phone_number.setText(sharedPreferences.getString("PhoneNumber", ""))!!
            Log.d(
                "emergencyno",
                sharedPreferences.getString("emergency_contactNumber", "").toString()
            )
            edt_emergency_contactNumber.setText(
                if(sharedPreferences.getString("emergency_contactNumber", "") == "0"){
                    ""
                }else{
                    sharedPreferences.getString("emergency_contactNumber", "")
                }
            )!!
            sp_blood_group.setText(sharedPreferences.getString("Blood_group", ""))!!
            edt_address.setText(sharedPreferences.getString("Address", ""))!!
            stateid = sharedPreferences.getString("State", "")
            nationality.setText(sharedPreferences.getString("Nationality", ""))
            locationid = sharedPreferences.getString("Location", "")
           var pin = sharedPreferences.getString("pincode", "")
           if(pin == "0"){
               edt_pincode.setText("")!!
           }else{
               edt_pincode.setText(pin)!!
           }
            edt_emergency_contact_Name.setText(
                sharedPreferences.getString(
                    "emergency_contact_name",
                    ""
                )
            )
             profileimageurl = sharedPreferences.getString("profile", "")
            val url = "https://gp.proteam.co.in/" + profileimageurl
            profileimage = profileimageurl
           Log.d("base64url1a", url)
           /* Picasso.get().load(url)
            .into(iv_profile_image)*/
        if (profileimageurl != "" ) {
            profileimageurl?.let { Log.d("base64url1b", it) }
                val url = "https://gp.proteam.co.in/" + profileimageurl
               // profileimage = ""
               /* convertImageUrlToBase64(url, object : ImageConversionListener {
                    override fun onConversionComplete(base64String: String) {
                        profileimage = base64String

                        // Picasso.get().load(url).into(iv_profile_image)
                        //iv_profile_image.setImageBitmap(base64ToBitmap(profileimage.toString()))

                        Log.d("base64url123", base64String.toString())
                    }
                })*/
                Log.d("base64url12", profileimage.toString())
            }
    }
    private fun addlists() {
        genderList.add("male")
        genderList.add("female")

        bloodgroup.add("A+")
        bloodgroup.add("A-")
        bloodgroup.add("B+")
        bloodgroup.add("B-")
        bloodgroup.add("O+")
        bloodgroup.add("O-")
        bloodgroup.add("AB+")
        bloodgroup.add("AB-")

        val sharedPreferences: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
        rollid = sharedPreferences.getString("rollid", "")!!
        userid = sharedPreferences.getString("userid", "")!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                imageUri =  uri
                val inputStream = contentResolver.openInputStream(imageUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val mimeType = contentResolver.getType(uri)
                profileuri = getRealPathFromURI(uri)
                val fileSize = getFileSize(uri)
                if (fileSize <= MAX_FILE_SIZE_BYTES) {
                    if (mimeType == "image/jpeg" || mimeType == "image/jpg" || mimeType == "application/pdf") {
                        iv_profile_image.setImageBitmap(bitmap)
                        oldprofileimeage = profileimageurl
                        val profileimage1 = convertImageUriToBase64(contentResolver, imageUri!!)
                        profileimage = profileimage1;
                        Log.d("profileImage", profileuri)
                        if(addnew == false){
                          /*  val webServices2 = WebServices<Any>(this@WorkerInformationActivity)
                            webServices2.file_update(WebServices.ApiType.fileupdate, workerid,profileuri,"profilepic")*/
                        }

                    } else {
                        // Invalid file format - Show an error message
                        Toast.makeText(this, "Invalid file format. Only JPG, JPEG, and PDF are allowed.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // File size exceeds the limit - Show an error message
                    Toast.makeText(this, "File size should be up to 2 MB.", Toast.LENGTH_SHORT).show()
                }
            }
        }else if(requestCode == 102 && resultCode == Activity.RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            if (imageBitmap != null) {
                iv_profile_image.setImageBitmap(imageBitmap)
                val capturedImageUri = saveImageToInternalStorage(imageBitmap)
                profileuri = capturedImageUri?.path!!
                profileimage = convertImageUriToBase64(contentResolver, capturedImageUri!!)

                if(addnew == false){
                    val webServices2 = WebServices<Any>(this@WorkerInformationActivity)
                    webServices2.file_update(
                        WebServices.ApiType.fileupdate,
                        workerid,
                        profileuri,
                        "profilepic"
                    )
                }
                Log.d("testingcamera2",capturedImageUri?.path.toString())
            }
        }else if(requestCode == 501 && resultCode == Activity.RESULT_OK){
            openGallery()
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {
        val file = File(filesDir, "captured_image.jpg")
        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            return Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

    fun convertImageUriToBase64(contentResolver: ContentResolver, imageUri: Uri): String {
        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, this, year, month, dayOfMonth)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        edt_dob.error = null
        edt_dob.setText(formattedDate)
    }

    override fun onResponse(
        response: Any?,
        URL: WebServices.ApiType?,
        isSucces: Boolean,
        code: Int
    ) {
        when (URL) {
            WebServices.ApiType.viewworker -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val workersListitem = response as workersListResponsce
                    if (workersListitem.isEmpty() == false ) {
                        val url = "https://gp.proteam.co.in/" + workersListitem.get(0).profilepic
                        profileimage = workersListitem.get(0).profilepic
                        Log.d("base64url111", url)
                        Picasso.get().load(url)
                            .into(iv_profile_image)
                        iv_profile_image
                    } else {
                        Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            WebServices.ApiType.states -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val statesResponseItem = response as statesResponse
                    if (statesResponseItem?.isEmpty() == false) {
                        for (x in statesResponseItem) {
                            stateList.add(x.state_name)
                            statemap.put(x.state_name,x.id)
                            statemapreverse.put(x.id,x.state_name)
                        }
                        sp_state.setText(statemapreverse.get(stateid))!!
                    } else {
                        Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            WebServices.ApiType.location -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val LocationResponse = response as LocationResponse
                    if (LocationResponse?.isEmpty() == false) {
                        for (x in LocationResponse) {
                            locatinmaping = response
                            locationList.add(x.city_name)
                            locationmap.put(x.city_name,x.city_id)
                            locationmapreverse.put(x.city_id,x.city_name)
                        }
                        sp_location.setText(locationmapreverse.get(locationid))!!
                    } else {
                        Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            else -> {}
        }

    }

    private fun getFileSize(fileUri: Uri): Long {
        val inputStream = contentResolver.openInputStream(fileUri)
        return inputStream?.available()?.toLong() ?: 0L
    }

    fun showImagePickerDialog(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        val dialogView: View = inflater.inflate(R.layout.option_dialog, null)
        builder.setView(dialogView)

        val alertDialog = builder.create()

        dialogView.findViewById<View>(R.id.btnGallery).setOnClickListener {
            checkStoragePermission()
            alertDialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnCamera).setOnClickListener {
            checkCameraPermission()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 101)
    }

    fun openCamera() {
        // Chirantan code
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, 102)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            501 -> if (grantResults.size > 0 && permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                // check whether storage permission granted or not.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                }else{
                    openGallery()
                    // openAppSettings()
                   // Toast.makeText(this, "Please enable Storage permission", Toast.LENGTH_SHORT).show()
                }
            }

            else -> {}
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 103)
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 103)

            //openCamera()
            //openAppSettings()
            Toast.makeText(this, "Please Enabled All Permission", Toast.LENGTH_SHORT).show()

        } else {
            openCamera()
            // Permission already granted, open the camera
        }
    }
    private fun openAppSettings() {
        val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        appSettingsIntent.data = Uri.fromParts("package", packageName, null)
        startActivity(appSettingsIntent)
    }

    private fun datePicker_diaog()
    {
        val newCalendar: Calendar = Calendar.getInstance()
        val mDatePicker = DatePickerDialog(this,
            { view, year, monthOfYear, dayOfMonth ->
                val newDate: Calendar = Calendar.getInstance()
                newDate.set(year, monthOfYear, dayOfMonth)
                val myFormat = "dd-MM-yyyy" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
                agecheck(sdf.format(newDate.getTime()))
            },
            newCalendar.get(Calendar.YEAR),
            newCalendar.get(Calendar.MONTH),
            newCalendar.get(Calendar.DAY_OF_MONTH)
        )
        mDatePicker.datePicker.maxDate = System.currentTimeMillis()
        mDatePicker.show()
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf<String>(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 501
            )
        } else {
            openGallery()
        }
    }

    private fun agecheck(format: String) {
        val currentDate = LocalDate.now()
        var age: Int = 0
        var dateOfBirth: LocalDate?
        var dates = format.split("-")
        if (!TextUtils.isEmpty(format) && dates.size == 3) {
            var year = if (dates.get(0).length == 4) {
                dates.get(0)
            } else {
                dates.get(2)
            }
            var day = if (dates.get(2).length == 2) {
                dates.get(2)
            } else {
                dates.get(0)
            }
            dateOfBirth = LocalDate.of(year.toInt(), dates.get(1).toInt(), day.toInt())
            age = Period.between(dateOfBirth, currentDate).years
            if(age <= 17){
                Toast.makeText(this, "Age must be more then 18 years", Toast.LENGTH_SHORT)
                    .show()
                edt_dob.setText("")
                edt_dob.error = "Age must be more then 18 years"

            }else{
                //edt_guardian_name.isFocusable = false
                //edt_guardian_name.isCursorVisible = false
                edt_dob.setText(format)
                edt_dob.error = null
                edt_dob.isFocusable = true
            }
        }
    }
}