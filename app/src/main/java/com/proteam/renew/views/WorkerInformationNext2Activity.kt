package com.proteam.renew.views

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.proteam.renew.R
import com.proteam.renew.requestModels.ApprovalRequest
import com.proteam.renew.requestModels.OnBoarding
import com.proteam.renew.requestModels.OnBoardingUpdate
import com.proteam.renew.requestModels.RejectRequest
import com.proteam.renew.responseModel.Generalresponsce
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
import java.util.Calendar
import java.util.Date
import java.util.Locale


open class WorkerInformationNext2Activity : AppCompatActivity(), OnResponseListener<Any> {

    private val PICK_FILE_AADHAR = 101
    private val PICK_FILE_MEDICAL = 102
    private val PICK_FILE_DRIVING = 103
    private val CAMERA_FILE_AADHAR = 201
    private val CAMERA_FILE_MEDICAL = 202
    private val CAMERA_FILE_DRIVING = 303
    var progressDialog: ProgressDialog? = null
    private val MAX_FILE_SIZE_BYTES = 2 * 1024 * 1024
    var isImageFitToScreen = false

    val select_aadhar_card: ImageView by lazy { findViewById<ImageView>(R.id.select_aadhar_card) }
    val view_aadhar_card: ImageView by lazy { findViewById<ImageView>(R.id.iv_aadhar_view) }
    val view_driving_card: ImageView by lazy { findViewById<ImageView>(R.id.iv_driving_view) }
    val view_medical_card: ImageView by lazy { findViewById<ImageView>(R.id.iv_medical_view) }
    val select_driving_licence: ImageView by lazy { findViewById<ImageView>(R.id.select_driving_licence) }
    val select_medical_certificate: ImageView by lazy { findViewById<ImageView>(R.id.select_medical_certificate) }
    val tv_previous_two: TextView by lazy { findViewById<TextView>(R.id.tv_previous_two) }
    val tv_submit: TextView by lazy { findViewById<TextView>(R.id.tv_submit) }
    val ll_driving_lic : LinearLayout by lazy { findViewById(R.id.ll_driving_lic) }
    val ll_medical_cert : LinearLayout by lazy { findViewById(R.id.ll_medical_cert) }
    val tv_update: TextView by lazy { findViewById((R.id.tv_update)) }

    var mCapturedPhotoUri : Uri? = null
    var name: String = ""
    var gaurdian: String = ""
    var Dob: String = ""
    var profilepic = ""
    var profileuri = ""
    var oldprofilepic = ""
    var gender: String = ""
    var aadharuri: String = ""
    var medicaluri: String = ""
    var drivinguri: String = ""
    var phone_number: String = ""
    var nationality: String = ""
    var emergency_number: String = ""
    var emergency_name: String = ""
    var blood_group: String = ""
    var address: String = ""
    var state: String = ""
    var location: String = ""
    var pincode: String = ""
    var Project: String = ""
    var skill_type: String = ""
    var skill_set: String = ""
    var worker_designation: String = ""
    var driving_license: String = ""
    var doj: String = ""
    var ex_month: String = ""
    var ex_year: String = ""
    var approvalstates: String = ""
    var sub_contractor: String = ""
    var contractor_contact_number: String = ""
    var induction_status: String = ""
    var aadhaar_card: String = ""
    var medical_test_status: String = ""
    var report_is_ok: String = ""
    var training: String = ""
    var adharimage : String = ""
    var medical_image : String = ""
    var driving_image : String = ""
    var medicalimage: String = ""
    var drivingimage: String = ""
    var oldmedicalimage : String = ""
    var oldadharimage : String = ""
    var olddrivingimage : String = ""

    var userid: String = ""
    var rollid: String = ""
    var induction_date = ""
    var workerid = ""
    var addnew: Boolean? = false
    var aadharnew: Boolean? = false
    var medicalnew: Boolean? = false
    var drivingnew: Boolean? = false
    var remarks = ""
    var medical_date = ""
    var licenceExpair = ""
    var supervisor_name = ""
    private var scaleGestureDetector: ScaleGestureDetector? = null
    var scaleFactor = 1.0f
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_information_next2)

        getAlldata()
        val sharedPreferences: SharedPreferences =getSharedPreferences("workerPref", Context.MODE_PRIVATE)!!
        val type = sharedPreferences.getBoolean("edit", false)!!
        val approval = sharedPreferences.getBoolean("approval",false)
        addnew = sharedPreferences.getBoolean("imageinsert", false)!!

        val sharedPreferences1: SharedPreferences =getSharedPreferences("updateworker", Context.MODE_PRIVATE)!!
         var aadharurl = sharedPreferences1.getString("aadharpic", "")!!
         drivingimage = sharedPreferences1.getString("drivinglicpic", "")!!
         medicalimage = sharedPreferences1.getString("medicalpic", "")!!
        approvalstates = sharedPreferences1.getString("approvalstatus", "")!!
        val workerid = sharedPreferences1.getString("id","")

        val sharedPreferences2: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
        userid = sharedPreferences2.getString("userid", "")!!
        rollid = sharedPreferences2.getString("rollid", "")!!

        callworkerapi()

        if(rollid == "2"){
            if(approvalstates == "1"){
                tv_submit.visibility = View.GONE
            }else{
                tv_previous_two.text = "Reject"
                tv_submit.text = "Approve"
            }
        }else{
            if(type != false){
                tv_submit.visibility = View.GONE
            }
        }
        Log.d("approvalstate",approvalstates )
        if(approvalstates == "1"){
            if(rollid == "1"){
                tv_update.visibility = View.GONE
            }

        }
        if(type == false && rollid == "1"){
            tv_update.visibility = View.GONE
        }

        tv_update.setOnClickListener {
            if(approvalstates == "2"){
                callreopen()
            }else{
                if(adharimage != ""){
                    validateUpdate()
                }else{
                    Toast.makeText(this, "Please enter aadhaar card image", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        if(approvalstates == "2"){
            tv_previous_two.visibility = View.GONE
            tv_submit.visibility = View.GONE
        }else if(approvalstates == "1"){

        }

        tv_previous_two.setOnClickListener {
            if(rollid == "2" && approvalstates != "1"){
                workerid?.let { it1 ->

                    showRemarksDialog(this) { remarks ->
                        callrejectApi(it1,remarks)
                        Toast.makeText(this, "Remarks: $remarks", Toast.LENGTH_SHORT).show()
                    }
                }
            }else {
                finish()
            }
        }

        view_aadhar_card.setOnClickListener {
            Showfullimage(adharimage)
        }
        view_medical_card.setOnClickListener {
            Showfullimage(medical_image)
        }
        view_driving_card.setOnClickListener {
            Showfullimage(driving_image)
        }

        select_aadhar_card.setOnClickListener {
            //Showfullimage("")
            showImagePickerDialog(this,PICK_FILE_AADHAR,CAMERA_FILE_AADHAR)

        }

        select_driving_licence.setOnClickListener {
            showImagePickerDialog(this, PICK_FILE_DRIVING, CAMERA_FILE_DRIVING)
        }

        select_medical_certificate.setOnClickListener {
            showImagePickerDialog(this, PICK_FILE_MEDICAL, CAMERA_FILE_MEDICAL)
        }

        if(rollid == "1"){

        }

        tv_submit.setOnClickListener {
            if(approval){
                if(rollid != "1"){
                    workerid?.let { it1 -> callaproveapi(it1) }
                } else {
                    Toast.makeText(this, "Please update it", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                if (type == true) {
                    if(adharimage != ""){
                       validateUpdate()
                    }else{
                        Toast.makeText(this, "Please enter Aadhaar card image", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    if(adharimage != ""){
                        callmasterAps()
                    }else{
                        Toast.makeText(this, "Please enter aadhaar card image", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    }

    private fun getAlldata() {
        val sharedPreferences = getSharedPreferences("WorkerInfoPref", Context.MODE_PRIVATE)
         name = sharedPreferences.getString("employee_name", "").toString()
         gaurdian = sharedPreferences.getString("guardian_name", "")!!
         Dob = sharedPreferences.getString("Dob", "")!!
         gender =sharedPreferences.getString("sp_gender", "")!!
         phone_number = sharedPreferences.getString("phone_number", "")!!
         emergency_number = sharedPreferences.getString("emergency_contactNumber", "")!!
         blood_group = sharedPreferences.getString("Blood_group", "")!!
         address = sharedPreferences.getString("address", "")!!
         nationality = sharedPreferences.getString("nationality","")!!
         state = sharedPreferences.getString("state", "")!!
         location = sharedPreferences.getString("location", "")!!
         pincode = sharedPreferences.getString("pincode", "")!!
        Log.d("approvalstate2",approvalstates )

        profilepic = sharedPreferences.getString("profile","")!!
        profileuri = sharedPreferences.getString("profileuri","")!!
        oldprofilepic = sharedPreferences.getString("oldprofile","")!!
        emergency_name = sharedPreferences.getString("emergency_contact_name","")!!

        val sharedPreferences2 = getSharedPreferences("WorkerInfoOnePref2", Context.MODE_PRIVATE)
        Project = sharedPreferences2.getString("project", "")!!
        skill_type = sharedPreferences2.getString("skill_type", "")!!
        skill_set = sharedPreferences2.getString("sp_skill_set", "")!!
        worker_designation = sharedPreferences2.getString("worker_designation", "")!!
        doj = sharedPreferences2.getString("doj", "")!!
        supervisor_name = sharedPreferences2.getString("Supervisor_name", "")!!
        sub_contractor = sharedPreferences2.getString("sub_contractor", "")!!
        contractor_contact_number = sharedPreferences2.getString("contractor_contact_number", "")!!
        induction_status = sharedPreferences2.getString("induction_status", "")!!
        aadhaar_card = sharedPreferences2.getString("aadhaar_card", "")!!
        supervisor_name = sharedPreferences2.getString("Supervisor_name","")!!
        medical_test_status = sharedPreferences2.getString("medical_test_status", "2")!!
       /* if(sharedPreferences2.getString("medical_test_status", "")!! == "Yes"){
            medical_test_status = "2"
        } else if(sharedPreferences2.getString("medical_test_status", "")!! == "No"){
            medical_test_status = "1"
        }else{
            medical_test_status = "0"
        }*/

        medical_test_status = sharedPreferences2.getString("medical_test_status", "")!!
        report_is_ok = sharedPreferences2.getString("report_is_ok", "")!!
        training = sharedPreferences2.getString("training", "")!!
        driving_license = sharedPreferences2.getString("driving_licence", "")!!
        ex_year = sharedPreferences2.getString("exp_years", "")!!
        ex_month = sharedPreferences2.getString("exp_months", "")!!
        induction_date = sharedPreferences2.getString("induction_date", "")!!
        workerid = sharedPreferences2.getString("workerid", "")!!
        remarks = sharedPreferences2.getString("edt_remarks","")!!
        medical_date = sharedPreferences2.getString("medical_test_date","")!!
        licenceExpair = sharedPreferences2.getString("licence_exp","")!!
        if(licenceExpair == "" || licenceExpair == null){
            ll_driving_lic.visibility = View.GONE
        }
        if(medical_date == "" || medical_date == null){
            ll_medical_cert.visibility = View.GONE
        }
    }

    private fun callaproveapi(workerid: String) {
        if(adharimage != ""){
            Log.d("dockchecking",medical_test_status+"---"+medicalimage)
            if(!ll_medical_cert.isVisible || ll_medical_cert.isVisible  && medicalimage != "" ) {
                Log.d("dockchecking2",ll_driving_lic.isVisible.toString()+"---"+drivingimage+"--"+licenceExpair)
                if(!ll_driving_lic.isVisible || ll_driving_lic.isVisible && drivingimage != ""){
                    Log.d("approveParm", induction_status+"--"+induction_date+"--"+report_is_ok)
                    if(induction_status == "1" && induction_date !="" && report_is_ok == "1"){
                        if(medical_test_status == "1"){
                        progressDialog = ProgressDialog(this@WorkerInformationNext2Activity)
                        if (progressDialog != null) {
                            if (!progressDialog!!.isShowing) {
                                progressDialog?.setCancelable(false)
                                progressDialog?.setMessage("Please wait...")
                                progressDialog?.show()
                                val approveRequest = ApprovalRequest(workerid, induction_date,induction_status,report_is_ok,userid)
                                val webServices = WebServices<Any>(this@WorkerInformationNext2Activity)
                                webServices.Approve(WebServices.ApiType.approve, approveRequest)
                            } else {

                          }
                        }
                        }else{
                            Toast.makeText(this, "Medical test should be yes", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }else{
                        Toast.makeText(this, "Induction not selected, Not able to approve", Toast.LENGTH_SHORT)
                            .show()
                    }
                }else{
                    Toast.makeText(this, "Please select Driving Licence", Toast.LENGTH_SHORT)
                        .show()
                }
            }else{
                Toast.makeText(this, "Please select Medical Certificate", Toast.LENGTH_SHORT)
                    .show()
            }
        }else{
            Toast.makeText(this, "Please select Aadhaar card image", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGestureDetector!!.onTouchEvent(event!!)
        return true
    }

    private fun callrejectApi(workerid: String, remarks: String) {
        progressDialog = ProgressDialog(this@WorkerInformationNext2Activity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                val rejectrequest = RejectRequest(workerid,remarks,userid )
                val webServices = WebServices<Any>(this@WorkerInformationNext2Activity)
                webServices.Reject(WebServices.ApiType.reject, rejectrequest)
            } else {
            }
        }
    }

    private fun callreopen() {
        GlobalScope.launch(Dispatchers.IO) {
            GlobalScope.launch(Dispatchers.IO) {
                if(workerid != ""){
                    val webServices = WebServices<Any>(this@WorkerInformationNext2Activity)
                    webServices.employeeresubmit(WebServices.ApiType.empReopen, workerid)
                }
            }
        }
    }

    private fun callmasterAps() {
        progressDialog = ProgressDialog(this@WorkerInformationNext2Activity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                GlobalScope.launch(Dispatchers.IO) {
                    val onboarding = OnBoarding(
                        aadharuri,
                        aadhaar_card,
                        address,
                        blood_group,
                        location,
                        Dob,
                        doj,
                        driving_license,
                        drivinguri,
                        licenceExpair,
                        emergency_name,
                        emergency_number,
                        supervisor_name,
                        ex_month,
                        ex_year,
                        gaurdian,
                        name,
                        gender,
                        induction_date,
                        induction_status,
                        medicaluri,
                        medical_date,
                        medical_test_status,
                        phone_number,
                        nationality,
                        pincode,
                        profileuri,
                        Project,
                        skill_set,
                        skill_type,
                        state,
                        contractor_contact_number,
                        sub_contractor,
                        userid,
                        worker_designation
                    )
                    val webServices = WebServices<Any>(this@WorkerInformationNext2Activity)
                    webServices.onBoarding(WebServices.ApiType.onBoarding, onboarding)
                }
            } else {
            }
        }
    }

    fun getRealPathFromURI(uri: Uri?): String {
        val cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

    private fun callupdateApi(workerid: String?) {
       Log.d("profileimagetest",workerid!!+"--"+profileuri+"--"+aadharuri+"--"+medicaluri+"--"+drivinguri)
       progressDialog = ProgressDialog(this@WorkerInformationNext2Activity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                GlobalScope.launch(Dispatchers.IO) {

                    /*if(medicalnew == true){
                    val webServices2 = WebServices<Any>(this@WorkerInformationNext2Activity)
                    webServices2.file_update(
                        WebServices.ApiType.fileupdate,
                        workerid,
                        medicaluri,
                        "medical_certificate"
                    )
                    }
                    if(drivingnew == true){
                        val webServices2 = WebServices<Any>(this@WorkerInformationNext2Activity)
                        webServices2.file_update(
                            WebServices.ApiType.fileupdate,
                            workerid,
                            drivinguri,
                            "driving_lisence_docs"
                        )
                    }
                    if(aadharnew == true){

                    }*/

                    val onboarding = OnBoardingUpdate(
                        aadhaar_card,
                        address,
                        blood_group,
                        location,
                        Dob,
                        doj,
                        driving_license,
                        licenceExpair,
                        emergency_name,
                        emergency_number,
                        supervisor_name,
                        ex_month,
                        ex_year,
                        gaurdian,
                        name,
                        gender,
                        approvalstates,
                        induction_date,
                        induction_status,
                        medical_date,
                        medical_test_status,
                        phone_number,
                        nationality,
                        pincode,
                        Project,
                        skill_set,
                        skill_type,
                        state,
                        contractor_contact_number,
                        sub_contractor,
                        userid,
                        worker_designation,
                        oldprofilepic,
                        oldadharimage,
                        olddrivingimage,
                        oldmedicalimage
                    )
                    Log.d("profileimagetest123",onboarding.toString())
                    val webServices = WebServices<Any>(this@WorkerInformationNext2Activity)
                    webServices.workerUpdate(WebServices.ApiType.update, onboarding, workerid)
                }
            } else {
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun validateUpdate(){
        if(adharimage != ""){
            Log.d("dockchecking",medical_test_status+"---"+medicalimage)
            if(!ll_medical_cert.isVisible || ll_medical_cert.isVisible  && medicalimage != "" ) {
                Log.d("dockchecking2",ll_driving_lic.isVisible.toString()+"---"+drivingimage+"--"+licenceExpair)
                if(!ll_driving_lic.isVisible || ll_driving_lic.isVisible && drivingimage != ""){
                    callupdateApi(workerid)
                }else{
                    Toast.makeText(this, "Please select Driving Licence", Toast.LENGTH_SHORT)
                        .show()
                }
            }else{
                Toast.makeText(this, "Please select Medical Certificate", Toast.LENGTH_SHORT)
                    .show()
            }
        }else{
            Toast.makeText(this, "Please select Aadhaar card image", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_AADHAR && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val imageUri: Uri = uri
                val inputStream = contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val mimeType = contentResolver.getType(uri)
                Log.d("profileutipath",aadharuri!!)

                val fileSize = getFileSize(uri)
                if (fileSize <= MAX_FILE_SIZE_BYTES) {
                    if (mimeType == "image/jpeg" || mimeType == "image/jpg" || mimeType == "application/pdf") {
                        select_aadhar_card.setImageBitmap(bitmap)
                        oldadharimage = adharimage
                        adharimage = convertImageUriToBase64(contentResolver, imageUri)
                        aadharuri = getRealPathFromURI(uri)
                        if(addnew == false){
                            aadharnew = true
                            val webServices2 = WebServices<Any>(this@WorkerInformationNext2Activity)
                            webServices2.file_update(WebServices.ApiType.fileupdate, workerid,aadharuri,"aadhaar_card")
                        }

                    } else {
                        Toast.makeText(this, "Invalid file format. Only JPG, JPEG, and PDF are allowed.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // File size exceeds the limit - Show an error message
                    Toast.makeText(this, "File size should be up to 2 MB.", Toast.LENGTH_SHORT).show()
                }
            }
        }else if (requestCode == PICK_FILE_DRIVING && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val imageUri: Uri = uri
                val inputStream = contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val mimeType = contentResolver.getType(uri)
                val fileSize = getFileSize(uri)
                if (fileSize <= MAX_FILE_SIZE_BYTES) {
                    if (mimeType == "image/jpeg" || mimeType == "image/jpg" || mimeType == "application/pdf") {
                        select_driving_licence.setImageBitmap(bitmap)
                        olddrivingimage = drivingimage
                        drivingimage = convertImageUriToBase64(contentResolver, imageUri)
                        drivinguri = getRealPathFromURI(uri)
                        if(addnew == false){
                            drivingnew == true
                            val webServices2 = WebServices<Any>(this@WorkerInformationNext2Activity)
                            webServices2.file_update(WebServices.ApiType.fileupdate, workerid,drivinguri,"driving_lisence_docs")
                        }

                    } else {
                        Toast.makeText(this, "Invalid file format. Only JPG, JPEG, and PDF are allowed.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // File size exceeds the limit - Show an error message
                    Toast.makeText(this, "File size should be up to 2 MB.", Toast.LENGTH_SHORT).show()
                }

            }
        } else  if (requestCode == PICK_FILE_MEDICAL && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val imageUri: Uri = uri
                val inputStream = contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val mimeType = contentResolver.getType(uri)

                val fileSize = getFileSize(uri)
                if (fileSize <= MAX_FILE_SIZE_BYTES) {
                    if (mimeType == "image/jpeg" || mimeType == "image/jpg" || mimeType == "application/pdf") {
                        select_medical_certificate.setImageBitmap(bitmap)
                        oldmedicalimage = medicalimage
                        medicalimage = convertImageUriToBase64(contentResolver, imageUri)
                        medicaluri = getRealPathFromURI(uri)
                        if(addnew == false){
                            medicalnew = true
                            val webServices2 = WebServices<Any>(this@WorkerInformationNext2Activity)
                            webServices2.file_update(
                                WebServices.ApiType.fileupdate,
                                workerid,
                                medicaluri,
                                "medical_certificate"
                            )
                        }
                    } else {
                        Toast.makeText(this, "Invalid file format. Only JPG, JPEG, and PDF are allowed.", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    // File size exceeds the limit - Show an error message
                    Toast.makeText(this, "File size should be up to 2 MB.", Toast.LENGTH_SHORT).show()
                }
            }
        } else if(requestCode == CAMERA_FILE_AADHAR && resultCode == Activity.RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            if (imageBitmap != null) {
                select_aadhar_card.setImageBitmap(imageBitmap)
                val capturedImageUri = saveImageToInternalStorage(imageBitmap)
                adharimage = convertImageUriToBase64(contentResolver, capturedImageUri!!)
                aadharuri = capturedImageUri?.path!!
                if(addnew == false){
                    aadharnew = true
                    val webServices2 = WebServices<Any>(this@WorkerInformationNext2Activity)
                    webServices2.file_update(
                        WebServices.ApiType.fileupdate,
                        workerid,
                        aadharuri,
                        "aadhaar_card"
                    )
                }
                Log.d("testingcamera2",capturedImageUri?.path.toString() )
            }
        } else if(requestCode == CAMERA_FILE_DRIVING && resultCode == Activity.RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            if (imageBitmap != null) {
                select_driving_licence.setImageBitmap(imageBitmap)
                val capturedImageUri = saveImageToInternalStorage(imageBitmap)
                drivingimage = convertImageUriToBase64(contentResolver, capturedImageUri!!)
                drivinguri = capturedImageUri?.path!!
                if(addnew == false){
                    drivingnew = true

                    val webServices2 = WebServices<Any>(this@WorkerInformationNext2Activity)
                    webServices2.file_update(
                        WebServices.ApiType.fileupdate,
                        workerid,
                        drivinguri,
                        "driving_lisence_docs"
                    )
                }
                Log.d("testingcamera2",capturedImageUri?.path.toString() )
            }
        } else if(requestCode == CAMERA_FILE_MEDICAL && resultCode == Activity.RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            if (imageBitmap != null) {
                select_medical_certificate.setImageBitmap(imageBitmap)
                val capturedImageUri = saveImageToInternalStorage(imageBitmap)
                medicalimage = convertImageUriToBase64(contentResolver, capturedImageUri!!)
                medicaluri = capturedImageUri?.path!!
                if(addnew == false){
                    medicalnew = true
                    val webServices2 = WebServices<Any>(this@WorkerInformationNext2Activity)
                    webServices2.file_update(
                        WebServices.ApiType.fileupdate,
                        workerid,
                        medicaluri,
                        "medical_certificate"
                    )
                }
                Log.d("testingcamera2",capturedImageUri?.path.toString() )
            }
        }
    }

    private fun callworkerapi() {
        GlobalScope.launch(Dispatchers.IO) {
            GlobalScope.launch(Dispatchers.IO) {
                if(workerid != "") {
                    val webServices = WebServices<Any>(this@WorkerInformationNext2Activity)
                    webServices.viewWorker(WebServices.ApiType.viewworker, workerid)
                }
            }
        }
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

                        //if(type) {
                            if (workersListitem.get(0).aadhaar_card != "") {
                                val url = "https://gp.proteam.co.in/" + workersListitem.get(0).aadhaar_card
                                adharimage = "https://gp.proteam.co.in/" + workersListitem.get(0).aadhaar_card
                                Picasso.get().load(url).into(select_aadhar_card)
                            }
                            if (workersListitem.get(0).driving_lisence_docs != "") {
                                val url = "https://gp.proteam.co.in/" + workersListitem.get(0).driving_lisence_docs
                                driving_image = "https://gp.proteam.co.in/" + workersListitem.get(0).driving_lisence_docs

                                Picasso.get().load(url).into(select_driving_licence)
                            }
                            if (workersListitem.get(0).medical_certificate != "") {
                                val url = "https://gp.proteam.co.in/" + workersListitem.get(0).medical_certificate
                                medical_image = "https://gp.proteam.co.in/" + workersListitem.get(0).medical_certificate
                                Picasso.get().load(url).into(select_medical_certificate)
                            }
                        //}


                    } else {
                        Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            WebServices.ApiType.onBoarding -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces && response != null) {
                    var gresponse = response as Generalresponsce
                    Log.d("validateadhar",gresponse.status.toString())
                    if (gresponse.status == 200) {
                        Showsuccess(this,"")
                    } else if (gresponse.status == 400) {
                        showFailureDialog(gresponse.messages.get(0))
                        Toast.makeText(this, gresponse.messages.get(0), Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    if(code == 400){
                        var gresponse = response as Generalresponsce
                        showFailureDialog(gresponse.messages.get(0))
                    }
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            WebServices.ApiType.approve -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces && response != null) {
                    val gresponse = response as Generalresponsce
                    if (gresponse?.status == 200) {
                        showSuccessDialog(gresponse.messages.get(0))
                    } else if(gresponse?.status == 400) {
                        Toast.makeText(this, gresponse.messages.get(0), Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            WebServices.ApiType.update -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces && response != null) {
                    val gresponse = response as Generalresponsce
                    if (gresponse?.status.toString() == "200") {
                        showSuccessDialog(gresponse.messages.get(0))
                    } else {
                        Toast.makeText(this, gresponse.messages.get(0), Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            WebServices.ApiType.reject -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces && response != null) {
                    val gresponse = response as Generalresponsce
                    if (gresponse?.status == 200) {
                        showSuccessDialog(gresponse.messages.get(0))
                    } else if(gresponse?.status == 400) {
                        Toast.makeText(this, gresponse.messages.get(0), Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            WebServices.ApiType.empReopen -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces && response != null) {
                    val gresponse = response as Generalresponsce
                    if (gresponse?.status == 200) {
                        showSuccessDialog(gresponse.messages.get(0))
                    } else if(gresponse?.status == 400){
                        Toast.makeText(this, gresponse.messages.get(0), Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "something went wrong, not updated", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            else -> {}
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {
        val calendar: Calendar = Calendar.getInstance()
        val currentTime: Date = calendar.getTime()
        val sdf = SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.getDefault())
        val formattedTime = sdf.format(currentTime)
        val file = File(filesDir, formattedTime+"captured_image.jpg")
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

    fun convertImageUriToBase64(contentResolver: ContentResolver, imageUri: Uri): String {
        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
    }

    fun showSuccessDialog(s: String) {
        val builder = AlertDialog.Builder(this)
       // builder.setTitle("Success")
        builder.setMessage(s)
        builder.setPositiveButton("OK") { dialog, which ->
            val intent = Intent(this@WorkerInformationNext2Activity, WorkerListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun Showsuccess(context: Context,msg: String) {
        val dialogView = Dialog(this)
        dialogView.setContentView(R.layout.dialog_success)
        dialogView.setCancelable(false)
        val submit: TextView = dialogView.findViewById(R.id.iv_submit)

        submit.setOnClickListener {
            val intent = Intent(this@WorkerInformationNext2Activity, WorkerListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            dialogView.dismiss()
        }

        dialogView.show()
    }

    fun Showfullimage(url : String) {
        if(url != "" && url != null){
            val dialogView = Dialog(this)
            dialogView.setContentView(R.layout.dialog_full_image)
            val image: PhotoView = dialogView.findViewById(R.id.fullImageView)
            Picasso.get().load(url).into(image)
            dialogView.show()
        }
    }

    fun showFailureDialog(s: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Failure!")
        builder.setMessage(s)
        builder.setPositiveButton("OK") { dialog, which ->
            // Perform any desired action after the user clicks the OK button
            dialog.dismiss() // Dismiss the dialog
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun getFileSize(fileUri: Uri): Long {
        val inputStream = contentResolver.openInputStream(fileUri)
        return inputStream?.available()?.toLong() ?: 0L
    }

    fun showRemarksDialog(context: Context, remarksListener: (String) -> Unit) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_remarks, null)
        val editTextRemarks: EditText = dialogView.findViewById(R.id.editTextRemarks)

        dialogBuilder.setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                val remarks = editTextRemarks.text.toString()
                remarksListener(remarks)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }


    fun showImagePickerDialog(activity: Activity, nav: Int, nav2: Int) {
        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        val dialogView: View = inflater.inflate(R.layout.option_dialog, null)
        builder.setView(dialogView)

        val alertDialog = builder.create()

        dialogView.findViewById<View>(R.id.btnGallery).setOnClickListener {
            openGallery(nav)
            alertDialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnCamera).setOnClickListener {
            checkCameraPermission(nav2)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }


    fun openGallery(nav: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, nav)
    }

    fun openCamera(nav2: Int) {
        //dispatchTakePictureCamIntent(nav2)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, nav2)
        }
    }

    private fun checkCameraPermission(nav: Int) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                103
            )
        } else {
            // Permission already granted, open the camera
            openCamera(nav)
        }
    }

    private fun dispatchTakePictureCamIntent(nav2: Int) {
        //Create intent for the Camera
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File
            var photoFile: File? = null
            try {
                photoFile = createImageFile(this.applicationContext)
            } catch (ex: IOException) {
                //Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(
                    this,
                    this.applicationContext.packageName + ".provider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(takePictureIntent, nav2)
                 mCapturedPhotoUri = photoUri
                //Log.d("testimagecapture", mCapturedPhotoUri.path.toString())
            }
        }
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File? {
        //Create Image Name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmsss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.d("storageDir",storageDir.toString())
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

}