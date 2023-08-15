package com.proteam.renew.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.proteam.renew.R

class WorkerDetailsActivity : AppCompatActivity() {

    val edt_employee_name_details: EditText by lazy { findViewById<EditText>(R.id.edt_employee_name_details) }
    val edt_guardian_name_details: EditText by lazy { findViewById<EditText>(R.id.edt_guardian_name_details) }
    val edt_dob_details: EditText by lazy { findViewById<EditText>(R.id.edt_dob_details) }
    val sp_gender_details: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_gender_details) }
    val edt_workerPhone_details: EditText by lazy { findViewById<EditText>(R.id.edt_workerPhone_details) }
    val edt_emgContactName_details: EditText by lazy { findViewById<EditText>(R.id.edt_emgContactName_details) }
    val edt_emgContactNumber_details: EditText by lazy { findViewById<EditText>(R.id.edt_emgContactNumber_details) }
    val edt_nationality_details: EditText by lazy { findViewById<EditText>(R.id.edt_nationality_details) }
    val sp_bloodGroup_details: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_bloodGroup_details) }
    val edt_address_details: EditText by lazy { findViewById<EditText>(R.id.edt_address_details) }
    val sp_state_details: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_state_details) }
    val sp_location_details: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_location_details) }
    val edt_pincode_details: EditText by lazy { findViewById<EditText>(R.id.edt_pincode_details) }
    val sp_project_details: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_project_details) }
    val sp_skilltype_details: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_skilltype_details) }
    val sp_skillset_details: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_skillset_details) }
    val edt_WorkerDegination_details: EditText by lazy { findViewById<EditText>(R.id.edt_WorkerDegination_details) }
    val edt_doj_details: EditText by lazy { findViewById<EditText>(R.id.edt_doj_details) }
    val sp_supervisorName_details: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_supervisorName_details) }
    val sp_subContractorName_details: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_subContractorName_details) }
    val edt_ContractorConNumber_details: EditText by lazy { findViewById<EditText>(R.id.edt_ContractorConNumber_details) }
    val sp_inductionStatus_details: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_inductionStatus_details) }
    val edt_aadhaar_details: EditText by lazy { findViewById<EditText>(R.id.edt_aadhaar_details) }
    val sp_medicalStatus_details: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_medicalStatus_details) }
    val sp_report_details: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_report_details) }


    val select_aadharImg_details: ImageView by lazy { findViewById<ImageView>(R.id.select_aadharImg_details) }
    val select_driving_licenceimg_details: ImageView by lazy { findViewById<ImageView>(R.id.select_driving_licenceimg_details) }
    val select_medical_certificateImg_details: ImageView by lazy { findViewById<ImageView>(R.id.select_medical_certificateImg_details) }





    val submit_approve: TextView by lazy { findViewById<TextView>(R.id.submit_approve) }
    val tv_reject: TextView by lazy { findViewById<TextView>(R.id.tv_reject) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_details)
    }
}