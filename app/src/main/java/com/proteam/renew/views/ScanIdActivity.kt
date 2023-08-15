package com.proteam.renew.views

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.proteam.renew.R
import com.proteam.renew.requestModels.AttendanceRequest
import com.proteam.renew.responseModel.AttendanceCountResponsce
import com.proteam.renew.responseModel.EmployeedetailResponsce
import com.proteam.renew.responseModel.ViewActivityMasterResponsce
import com.proteam.renew.responseModel.ViewProjectMaster
import com.proteam.renew.responseModel.generalGesponce
import com.proteam.renew.utilitys.OnResponseListener
import com.proteam.renew.utilitys.WebServices
import com.proteam.renew.utilitys.timeConverter
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScanIdActivity : AppCompatActivity(), OnResponseListener<Any> {
    var etChooseTime_from: EditText? = null
    var etChooseTime_to: EditText? = null
    var timePickerDialogfrom: TimePickerDialog? = null
    var timePickerDialogto: TimePickerDialog? = null
    var calendar: Calendar? = null
    var currentHour = 0
    var currentMinute = 0
    var amPm: String? = null
    private lateinit var qrCodeScanner: IntentIntegrator
    val qr_scan: TextView by lazy { findViewById(R.id.qr_scan) }
    var userid: String = ""
    var rollid : String = ""
    private lateinit var bottomNavigationView: BottomNavigationView
    var progressDialog: ProgressDialog? = null
    var projectslist = ArrayList<String>()
    var projectmap = HashMap<String, String>()
    var projectsreverselist = HashMap<String, String>()
    var activitylist = ArrayList<String>()
    var activitymap = HashMap<String, String>()
    var activityreverselist = HashMap<String, String>()
    val sp_project1: AutoCompleteTextView by lazy { findViewById(R.id.project) }
    val sp_activity: AutoCompleteTextView by lazy { findViewById(R.id.activity) }
    val img_work_permit: ImageView by lazy { findViewById(R.id.img_work_permit) }
    val tv_att_list: TextView by lazy { findViewById(R.id.tv_att_list) }

    val edt_total_worker: TextInputEditText by lazy { findViewById(R.id.edt_total_worker) }
    var workPermit : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_id)
        etChooseTime_from = findViewById(R.id.etChooseTime_from)
        etChooseTime_from?.setInputType(InputType.TYPE_NULL)
        etChooseTime_from?.requestFocus()
        etChooseTime_to = findViewById(R.id.etChooseTime_to)
        etChooseTime_to?.setInputType(InputType.TYPE_NULL)
        etChooseTime_to?.requestFocus()
        qrCodeScanner = IntentIntegrator(this)

        val sharedPreferences2: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
        rollid = sharedPreferences2.getString("rollid", "")!!
        userid = sharedPreferences2.getString("userid", "")!!
        callApis()

        initilize()
        bottomnavigation()

        val adapter4 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, projectslist)
        sp_project1.setAdapter(adapter4)

        val adapter5 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, activitylist)
        sp_activity.setAdapter(adapter5)

    }

    private fun callApis() {
        val webServices = WebServices<Any>(this@ScanIdActivity)
        webServices.projects(WebServices.ApiType.projects, userid)

        val webServices2 = WebServices<Any>(this@ScanIdActivity)
        webServices2.activity(WebServices.ApiType.activitys)
    }

    private fun initilize() {
        img_work_permit.setOnClickListener {
          //      val intent = Intent(Intent.ACTION_GET_CONTENT)
           //     intent.type = "*/*"
            //    startActivityForResult(intent, 101)
                //openCamera()
            checkCameraPermission()
        }

        tv_att_list.setOnClickListener {
            val sharedPref: SharedPreferences =applicationContext.getSharedPreferences("Attendenceid", Context.MODE_PRIVATE)!!
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString("contractorid",userid)
            editor.putString("projectid","")
            editor.putString("date",getFormattedDate())
            editor.commit()
            val intent = Intent(applicationContext, ContractorAttendanceList::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            applicationContext.startActivity(intent)
        }

        qr_scan.setOnClickListener{
            if(!TextUtils.isEmpty(sp_project1.text)){
                if(!TextUtils.isEmpty(sp_activity.text)){
                    if(!TextUtils.isEmpty(edt_total_worker.text.toString())) {
                        if (workPermit != "") {
                            val sharedPreferences2: SharedPreferences =
                                getSharedPreferences("countofattendance", Context.MODE_PRIVATE)!!
                            var count = sharedPreferences2.getInt("totalcount", 0)!!
                            val pro = sharedPreferences2.getString("project", "")!!
                            val date = sharedPreferences2.getString("date", "")!!
                            var newpro: Boolean = false

                            Log.d(
                                "testScanning",
                                sp_project1.text.toString() + "---" + count + "---" + pro
                            )

                            if (pro != sp_project1.text.toString() || pro == sp_project1.text.toString() && count < edt_total_worker.text.toString()
                                    .toInt()
                            ) {
                                scanQRCode()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Total worker count exceeded",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else {
                            Toast.makeText(this, "please select work permit", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }else{
                        Toast.makeText(this, "please enter total count", Toast.LENGTH_SHORT)
                            .show()
                    }

                }else{
                    Toast.makeText(this, "please select Activity", Toast.LENGTH_SHORT)
                        .show()
                }

            }else{
                Toast.makeText(this, "please select Project", Toast.LENGTH_SHORT)
                    .show()
            }
        }
/*        etChooseTime_from?.setOnClickListener(View.OnClickListener {
            calendar = Calendar.getInstance()
            currentHour = calendar?.get(Calendar.HOUR_OF_DAY)!!
            currentMinute = calendar?.get(Calendar.MINUTE)!!
            timePickerDialogfrom =
                TimePickerDialog(this@ScanIdActivity, { timePicker, hourOfDay, minutes ->
                    amPm = if (hourOfDay >= 12) {
                        "PM"
                    } else {
                        "AM"
                    }
                    etChooseTime_from?.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm)
                }, currentHour, currentMinute, false)
            timePickerDialogfrom!!.show()
        })*/


        etChooseTime_from!!.setOnClickListener {
            val timePickerBuilder: MaterialTimePicker.Builder = MaterialTimePicker
                .Builder()
                .setTitleText("Select a time")
                .setHour(2)
                .setMinute(30)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            val timePicker = timePickerBuilder.build()
            timePicker.show(supportFragmentManager, "TIME_PICKER")

            timePicker.addOnPositiveButtonClickListener {
                etChooseTime_from!!.setText(
                    timeConverter(
                        hour = timePicker.hour,
                        minute = timePicker.minute
                    )
                )
            }
        }
   /*     etChooseTime_to?.setOnClickListener(View.OnClickListener {
            calendar = Calendar.getInstance()
            currentHour = calendar?.get(Calendar.HOUR_OF_DAY)!!
            currentMinute = calendar?.get(Calendar.MINUTE)!!
            timePickerDialogto =
                TimePickerDialog(this@ScanIdActivity, { timePicker, hourOfDay, minutes ->
                    amPm = if (hourOfDay >= 12) {
                        "PM"
                    } else {
                        "AM"
                    }
                    etChooseTime_to?.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm)
                }, currentHour, currentMinute, false)
            timePickerDialogto!!.show()
        }) */

        etChooseTime_to!!.setOnClickListener {
            val timePickerBuilder: MaterialTimePicker.Builder = MaterialTimePicker
                .Builder()
                .setTitleText("Select a time")
                .setHour(2)
                .setMinute(30)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            val timePicker = timePickerBuilder.build()
            timePicker.show(supportFragmentManager, "TIME_PICKER")

            timePicker.addOnPositiveButtonClickListener {
                etChooseTime_to!!.setText(
                    timeConverter(
                        hour = timePicker.hour,
                        minute = timePicker.minute
                    )
                )
            }
        }
    }

    private fun bottomnavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavShift)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intentScan = Intent(this@ScanIdActivity, MainActivity::class.java)
                    startActivity(intentScan)
                    finish()

                    true
                }
                R.id.it_scan -> {

                    true
                }

                R.id.it_worker -> {
                    if(rollid == "1") {
                        val intentScan = Intent(this@ScanIdActivity, WorkerListActivity::class.java)
                        startActivity(intentScan)
                        finish()
                    }
                    true
                }
                else -> false
            }
        }    }

    fun scanQRCode() {
        qrCodeScanner.setOrientationLocked(true)
        qrCodeScanner.createScanIntent()
        qrCodeScanner.initiateScan()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val imageUri: Uri = uri
                val inputStream = contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                img_work_permit.setImageBitmap(bitmap)
                workPermit = convertImageUriToBase64(contentResolver, imageUri)
            }
        } else if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            if (imageBitmap != null) {
                img_work_permit.setImageBitmap(imageBitmap)
                val capturedImageUri = saveImageToInternalStorage(imageBitmap)
                workPermit = convertImageUriToBase64(contentResolver, capturedImageUri!!)
                Log.d("testingcamera2",capturedImageUri?.path.toString())
            }
        } else {
            val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents != null) {
                    val scannedData = result.contents
                    callempdetail(scannedData)
                    //showCustomDialog()
                    //validation(scannedData)
                } else {

                }
            }
        }
    }

    private fun validation(details : EmployeedetailResponsce) {
        /*val sharedPreferences2: SharedPreferences =
            getSharedPreferences("countofattendance", Context.MODE_PRIVATE)!!
        var count = sharedPreferences2.getString("totalcount", "")!!
        val pro = sharedPreferences2.getString("project", "")!!
        var newpro : Boolean = false

        Log.d("testScanning",sp_project1.text.toString()+"---"+count+"---"+pro)
        if(pro == sp_project1.text.toString()){

        } else {
            newpro = true
        }*/
       // if (edt_total_worker.text.toString() >= count || newpro == true) {
        var projectid = projectmap.get(sp_project1.text.toString())
        var activityid = activitymap.get(sp_activity.text.toString())
        if (!TextUtils.isEmpty(projectid)) {
            if (!TextUtils.isEmpty(activityid)) {
                if (!TextUtils.isEmpty(etChooseTime_from?.text.toString())) {
                    if (!TextUtils.isEmpty(etChooseTime_to?.text.toString())) {
                        if (!TextUtils.isEmpty(projectid)) {
                            if (!TextUtils.isEmpty(projectid)) {
                                callAttendanceApi(details, projectid, activityid)
                            } else {
                                Toast.makeText(this, "please select project id", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        } else {
                            Toast.makeText(this, "please select project id", Toast.LENGTH_SHORT)
                                .show()
                        }

                    } else {
                        Toast.makeText(this, "please select To timings", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    Toast.makeText(this, "please select from Timings", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                Toast.makeText(this, "please select Activity", Toast.LENGTH_SHORT)
                    .show()
            }

        } else {
            Toast.makeText(this, "please select project", Toast.LENGTH_SHORT).show()
        }
    /*} else {
            Toast.makeText(this, "Attendance count exceeded ", Toast.LENGTH_SHORT)
                .show()
       }*/
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {
        val file = File(filesDir, "captured_image1.jpg")
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

    private fun callempdetail(scannedData: String) {
        progressDialog = ProgressDialog(this@ScanIdActivity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                 val webServices = WebServices<Any>(this@ScanIdActivity)
                 webServices.empdetails(WebServices.ApiType.empdetails, scannedData)
            } else {
            }
        }
    }
    private fun callAttendanceApi(
        details: EmployeedetailResponsce,
        projectid: String?,
        activityid: String?
    ) {
        progressDialog = ProgressDialog(this@ScanIdActivity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                val date = getFormattedDate()
                val attendancerequest = AttendanceRequest(
                    activityid.toString(),
                    "P",
                    details.subcontractor_name,
                    date,
                    details.id,
                    etChooseTime_from?.text.toString(),
                    projectid.toString(),
                    etChooseTime_from?.text.toString(),
                    userid,
                    workPermit
                )
                val webServices = WebServices<Any>(this@ScanIdActivity)
                webServices.attendance(WebServices.ApiType.attendence, attendancerequest)
            } else {
            }
        }
    }

    fun getCurrentDate(): Calendar {
        return Calendar.getInstance()
    }

    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(getCurrentDate().time)
    }

    fun convertImageUriToBase64(contentResolver: ContentResolver, imageUri: Uri): String {
        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
    }
    private fun showCustomDialog(details: EmployeedetailResponsce) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.attenence_dilaog)
        dialog.setCancelable(false)
        var profile = dialog?.findViewById<ImageView>(R.id.iv_profile)
        var name = dialog.findViewById<TextView>(R.id.tv_name)
        var empid = dialog.findViewById<TextView>(R.id.tv_id)
        var save = dialog.findViewById<TextView>(R.id.tv_save)
        var cancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        var tv_invaild = dialog.findViewById<TextView>(R.id.tv_inactive)

        name.setText(details.full_name)
        empid.setText(details.worker_id)

        if(details.profilepic != "") {
            val url = "https://gp.proteam.co.in/"+details.profilepic
            Picasso.get().load(url).into(profile)
        }

        if(details.status != "1"){
            tv_invaild.visibility = View.VISIBLE
            save.visibility = View.GONE
        }else{
            tv_invaild.visibility = View.GONE
            save.visibility = View.VISIBLE
        }

        save.setOnClickListener {
            validation(details)
            dialog.dismiss()
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onResponse(
        response: Any?,
        URL: WebServices.ApiType?,
        isSucces: Boolean,
        code: Int
    ) {
        when (URL) {

            WebServices.ApiType.projects -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val viewProjectMaster = response as ViewProjectMaster
                    if (viewProjectMaster?.isEmpty() == false) {
                        for (x in viewProjectMaster) {
                            projectslist.add(x.project_name)
                            projectmap.put(x.project_name,x.id)
                            projectsreverselist.put(x.id,x.project_name)
                        }
                        /*sp_project.setOnClickListener {
                            val adapter2 = ArrayAdapter(this,
                                android.R.layout.simple_list_item_1, projectslist)
                            sp_project.setAdapter(adapter2)
                        }*/
                    } else {
                        Toast.makeText(this, "Something Went wrong. Please check Internet", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            WebServices.ApiType.activitys -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val viewActivityresponsce = response as ViewActivityMasterResponsce
                    if (viewActivityresponsce?.isEmpty() == false) {
                        for (x in viewActivityresponsce) {
                            activitylist.add(x.activity_name)
                             activitymap.put(x.activity_name,x.activity_id)
                            activityreverselist.put(x.activity_id,x.activity_name)
                        }

                    } else {
                        Toast.makeText(this, "Something Went wrong. Please check Internet", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            WebServices.ApiType.empdetails -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val empdetail = response as EmployeedetailResponsce
                    if (!empdetail.full_name.isEmpty()) {
                        showCustomDialog(empdetail)
                    } else {
                        Toast.makeText(this, "", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Invalid Worker. please scan valid Worker id ", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            WebServices.ApiType.attendence -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val attendanceres = response as AttendanceCountResponsce
                    if (attendanceres.status == "200" ) {
                        val prefs = getSharedPreferences("countofattendance", MODE_PRIVATE)
                        val editor = prefs.edit()
                        editor.putInt("totalcount",attendanceres.present_attendance_count)
                        editor.putString("project",sp_project1.text.toString())
                        editor.putString("date",getFormattedDate())
                        editor.commit()
                        if(attendanceres.present_attendance_count < edt_total_worker.text.toString().toInt()) {
                            scanQRCode()
                        } else {
                            val sharedPref: SharedPreferences =applicationContext.getSharedPreferences("Attendenceid", Context.MODE_PRIVATE)!!
                            val editor: SharedPreferences.Editor = sharedPref.edit()
                            editor.putString("contractorid",userid)
                            editor.putString("projectid","")
                            editor.putString("date",getFormattedDate())
                            editor.commit()
                            val intent = Intent(applicationContext, ContractorAttendanceList::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            applicationContext.startActivity(intent)
                        }
                        Toast.makeText(this, attendanceres.messages.get(0), Toast.LENGTH_SHORT)
                            .show()
                    } else if(attendanceres.status == "400") {
                        Toast.makeText(this, attendanceres.messages.get(0), Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            else -> {}
        }
    }

    fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, 102)
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

            openCamera()
            Toast.makeText(this, "Please Enabled All Permission", Toast.LENGTH_SHORT).show()

        } else {
            openCamera()
            // Permission already granted, open the camera
        }
    }

}