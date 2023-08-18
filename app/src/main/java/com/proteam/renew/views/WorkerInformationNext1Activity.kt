package com.proteam.renew.views

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.proteam.renew.R
import com.proteam.renew.requestModels.RejectRequest
import com.proteam.renew.responseModel.ContractorListResponsce
import com.proteam.renew.responseModel.ContractorListResponsceItem
import com.proteam.renew.responseModel.Generalresponsce
import com.proteam.renew.responseModel.SupervisorListResponsce
import com.proteam.renew.responseModel.ViewProjectMaster
import com.proteam.renew.responseModel.ViewSkillsetMaster
import com.proteam.renew.responseModel.ViewTrainingMaster
import com.proteam.renew.utilitys.OnResponseListener
import com.proteam.renew.utilitys.Utils
import com.proteam.renew.utilitys.WebServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WorkerInformationNext1Activity : AppCompatActivity(),OnResponseListener<Any>, DatePickerDialog.OnDateSetListener {

    val sp_project: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_project) }
    val sp_skill_type: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_skill_type) }
    val sp_skill_set: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_skill_set) }
    val edt_worker_designation: EditText by lazy { findViewById<EditText>(R.id.edt_worker_designation) }
    val sp_status: AutoCompleteTextView by lazy { findViewById(R.id.sp_status) }
    val sp_feed_back: AutoCompleteTextView by lazy { findViewById(R.id.sp_feed_back) }
    val edt_doj: EditText by lazy { findViewById<EditText>(R.id.edt_doj) }
    val sp_Supervisor_name: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_Supervisor_name) }
    val sp_sub_contractor: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_sub_contractor) }
    val edt_contractor_contact_number: EditText by lazy { findViewById<EditText>(R.id.edt_contractor_contact_number) }
    val sp_induction_status: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_induction_status) }
    val edt_aadhaar_card: EditText by lazy { findViewById<EditText>(R.id.edt_aadhaar_card) }
    val sp_medical_test_status: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_medical_test_status) }
    val sp_report_is_ok: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_report_is_ok) }
    val sp_year_exp: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_year_exp) }
    val sp_months_exp: AutoCompleteTextView by lazy { findViewById<AutoCompleteTextView>(R.id.sp_months_exp) }
    val ll_exp: LinearLayout by lazy { findViewById(R.id.ll_exp) }
    val edt_driving_licence : EditText by lazy { findViewById<EditText>(R.id.edt_driving_licence) }
    val edt_medical_date : EditText by lazy { findViewById<EditText>(R.id.edt_medical_date) }
    val con_licence_date: ConstraintLayout by lazy { findViewById(R.id.con_licence_date) }
    val edt_licence_expire : EditText by lazy { findViewById<EditText>(R.id.edt_licence_expire) }
    val tv_previous: TextView by lazy { findViewById<TextView>(R.id.tv_previous) }
    val tv_next_two: TextView by lazy { findViewById<TextView>(R.id.tv_next_two) }
    val dri_const: ConstraintLayout by lazy { findViewById(R.id.dri_const) }
    val con_medical_date: ConstraintLayout by lazy { findViewById(R.id.con_medical_date) }

    val con_induction_date: ConstraintLayout by lazy { findViewById(R.id.con_induction_date) }
    val con_remarks: ConstraintLayout by lazy { findViewById(R.id.con_remarks) }
    val ti_report_ok: TextInputLayout by lazy { findViewById(R.id.ti_report_ok) }
    val edt_worker_id: EditText by lazy { findViewById(R.id.edt_worker_id) }
    val ti_state: TextInputLayout by lazy { findViewById(R.id.ti_state) }
    val ti_performance_feedback: TextInputLayout by lazy { findViewById(R.id.ti_performance_feedback) }
    val edt_remarks: EditText by lazy { findViewById<EditText>(R.id.edt_remarks) }
    val ti_induction_state: TextInputLayout by lazy { findViewById(R.id.ti_induction_state) }
    val induction_date: EditText by lazy { findViewById<EditText>(R.id.edt_induction_date) }
    val con_worker_id: ConstraintLayout by lazy { findViewById(R.id.con_worker_id) }
    var progressDialog: ProgressDialog? = null
    var skillsList = ArrayList<String>()
    var projectslist = ArrayList<String>()
    var projectsreverselist = HashMap<String, String>()
    var skillsetreverse = HashMap<String, String>()
    var traininglist = ArrayList<String>()
    var skillType = ArrayList<String>()
    var generaloption = ArrayList<String>()
    var medicaloption = ArrayList<String>()

    var contractorList = ArrayList<String>()
    var supervisorList = ArrayList<String>()
    var experienceyearsList = ArrayList<String>()
    var experiencemonthList = ArrayList<String>()
    var status = ArrayList<String>()
    var feedback = ArrayList<String>()
    var approvalstatus : String = ""

    //Shared Pref  string
    val Project = "project"
    val skill_type = "skill_type"
    val skill_set = "sp_skill_set"
    val worker_designation = "worker_designation"
    val doj = "doj"
    val Supervisor_name = "Supervisor_name"
    val sub_contractor = "sub_contractor"
    val contractor_contact_number = "contractor_contact_number"
    val induction_status = "induction_status"
    val aadhaar_card = "aadhaar_card"
    val medical_test_status = "medical_test_status"
    val report_is_ok = "report_is_ok"

    var userid: String? = ""
    var rollid: String? = ""
     var medicalmonth : String? = ""
    var aadharvalidation: Boolean? = false

    var type: Boolean? = false
    var approval: Boolean? = false
    var flag: Boolean? = false
    var prijectid: String = ""
    var skillid: String = ""
    var conId: String = ""
    var supId = ""
    var dateview : Int = 0
    val projectMap = HashMap<String, String>()
    val skillsetMap = HashMap<String, String>()
    val trainingMap = HashMap<String, String>()
    var supervisorreverse = HashMap<String, String>()
    var contractorreverse = HashMap<String, String>()
    var contractormap = HashMap<String, String>()
    var supervisormap = HashMap<String, String>()
    var contractormapinglist = ArrayList<ContractorListResponsceItem>()
    private lateinit var bottomNavigationView: BottomNavigationView
    private val calendar: Calendar = Calendar.getInstance()
    var utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_information_next1)

        callArray()
        bottomnavigation()
        val sharedPreferences: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
        rollid = sharedPreferences.getString("rollid", "")!!
        userid = sharedPreferences.getString("userid", "")!!

        val sharedPreferences1: SharedPreferences =getSharedPreferences("workerPref", Context.MODE_PRIVATE)!!
        type = sharedPreferences1.getBoolean("edit", false)!!
        approval = sharedPreferences1.getBoolean("approval", false)!!


        val sharedPreferences4: SharedPreferences =getSharedPreferences("onboard", Context.MODE_PRIVATE)!!
        var nav = sharedPreferences4.getBoolean("nav", false)!!

        val sharedPrefef: SharedPreferences =getSharedPreferences("viewsshow", Context.MODE_PRIVATE)!!
        var views = sharedPrefef.getBoolean("views", true)!!

        callmasterAps()

        dri_const.visibility = View.GONE
        con_licence_date.visibility = View.GONE
        tv_next_two.setOnClickListener {
            validateform()
        }
        edt_licence_expire.setOnClickListener{
            dateview = 3
            showDatePickerDialog2()
        }

        if(rollid == "1"){
            con_worker_id.visibility = View.GONE
            con_induction_date.visibility = View.GONE
            ti_report_ok.visibility = View.GONE
            ti_induction_state.visibility = View.GONE
            con_remarks.visibility = View.GONE
            ti_state.visibility = View.GONE
            ti_performance_feedback.visibility = View.GONE
            con_induction_date.visibility = View.GONE
        }

        tv_previous.setOnClickListener {
            //val intent = Intent(applicationContext, WorkerInformationActivity::class.java)
            //startActivity(intent)
            finish()
        }
        edt_doj.setOnClickListener {
            dateview = 0
            edt_doj.error = null
            showDatePickerDialog()
        }
        sp_project.setOnClickListener {
            val adapter2 = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, projectslist)
            sp_project.setAdapter(adapter2)
        }

        /*sp_skill_set.setOnClickListener {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, skillsList)
            sp_skill_set.setAdapter(adapter)
        }*/
        sp_medical_test_status.setText("Yes")
        edt_medical_date.setOnClickListener {
            dateview = 1
            showDatePickerDialog()
        }

        induction_date.setOnClickListener {
            dateview = 2
            showDatePickerDialog()
        }
        if(type == true) {
            val sharedPreferences2: SharedPreferences =getSharedPreferences("updateworker", Context.MODE_PRIVATE)!!
            prijectid = sharedPreferences2.getString("Project", "")!!
            sp_skill_type.setText(sharedPreferences2.getString("skill_type", ""))!!
            skillid = sharedPreferences2.getString("skill_set", "")!!
            edt_worker_designation.setText( sharedPreferences2.getString("worker_designation", ""))!!
            edt_doj.setText( sharedPreferences2.getString("doj", ""))!!
            var doj = sharedPreferences2.getString("doj", "")
            var dates = doj?.split("-")
            if(dates?.get(0)?.length == 4){
                doj = dates?.get(2)+"-"+dates.get(1)+"-"+dates.get(0)
            } else {
                doj = dates?.get(0)+"-"+dates?.get(1)+"-"+dates?.get(2)
            }
            edt_doj.setText(doj)
            supId = sharedPreferences2.getString("Supervisor_name", "")!!
            Log.d("projectidtest",prijectid.toString())
            if(prijectid != ""){
              //  callsupervisorapi(prijectid)
            }
            conId = sharedPreferences2.getString("sub_contractor", "")!!
            edt_contractor_contact_number.setText( sharedPreferences2.getString("contractor_contact_number", ""))!!
            if(currecttheDate(sharedPreferences2.getString("inductiondate", "")!!) != "00-00-0000"){
                induction_date.setText(currecttheDate(sharedPreferences2.getString("inductiondate", "")!!))
            }
            edt_worker_id.setText(sharedPreferences2.getString("workerid", ""))
            sp_year_exp.setText(sharedPreferences2.getString("exp_years", ""))
            sp_months_exp.setText(sharedPreferences2.getString("exp_months", ""))
            edt_remarks.setText(sharedPreferences2.getString("edt_remarks",""))
            edt_driving_licence.setText(sharedPreferences2.getString("drivinglic",""))
            edt_licence_expire.setText(currecttheDate(sharedPreferences2.getString("licence_exp","")!!))
            edt_medical_date.setText(currecttheDate(sharedPreferences2.getString("medical_test_date","")!!))

            var istate = sharedPreferences2.getString("induction_status", "")
            if(views == false && rollid != "1"){
                con_induction_date.visibility = View.VISIBLE
                if(istate == "0" || istate == "1"){
                    sp_induction_status.setText("Yes")
                } else if(istate == "2") {
                    sp_induction_status.setText("No")
                    con_induction_date.visibility = View.GONE
                }
            } else {
                con_induction_date.visibility = View.GONE
            }

            edt_aadhaar_card.setText( sharedPreferences2.getString("aadhaar_card", ""))!!
            if(edt_aadhaar_card.text.toString() != ""){
                aadharvalidation = true
            }
            if( sharedPreferences2.getString("medical_test_status", "") == "0"){
                sp_medical_test_status.setText("Yes")
                con_medical_date.visibility = View.VISIBLE
            }else if( sharedPreferences2.getString("medical_test_status", "") == "1"){
                sp_medical_test_status.setText("Yes")
                con_medical_date.visibility = View.VISIBLE
            }
            else{
                sp_medical_test_status.setText("No")
                con_medical_date.visibility = View.GONE
            }

            if(sharedPreferences2.getString("report_is_ok", "") == "0" || sharedPreferences2.getString("report_is_ok", "") == "1" || sharedPreferences2.getString("report_is_ok", "") == ""){
                sp_report_is_ok.setText("Yes")
            }else{
                sp_report_is_ok.setText("No")
            }

            if(sp_skill_type.text.toString().lowercase() == "skilled"){
                ll_exp.visibility = View.VISIBLE
            }
            if(sp_skill_set.text.toString().lowercase() != "driver"){
                dri_const.visibility = View.GONE
                con_licence_date.visibility = View.GONE
            }
             approvalstatus = sharedPreferences2.getString("approvalstatus", "")!!



            if(approvalstatus == "0" && rollid != "1") {
                con_remarks.visibility = View.GONE
                ti_performance_feedback.visibility = View.GONE
               // ti_state.visibility = View.GONE
            }
            if(approvalstatus == "1"){
                setEditable()
            }
            if(approvalstatus == "1") {
                //edt_doj.setEn
            }
            if(rollid != "2" || approvalstatus == "0"){
                ti_state.visibility = View.GONE
            }else if(approvalstatus == "1"){
                sp_status.setText("Active")
            }else{
                sp_status.setText("Inactive")
            }
            if(approvalstatus == "2"){
                con_remarks.visibility = View.GONE
                ti_performance_feedback.visibility = View.GONE
                ti_state.visibility = View.GONE
            }
        }

        sp_skill_type.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            if(selectedItem.lowercase() == "skilled"){
                ll_exp.visibility = View.VISIBLE
                val adapter = ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, skillsList)
                sp_skill_set.setAdapter(adapter)
            }else{
                var skills = ArrayList<String>()
                skills.add("Worker")
                val adapter = ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, skills)
                sp_skill_set.setAdapter(adapter)

                ll_exp.visibility = View.GONE
            }
        }

        sp_skill_set.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            if(selectedItem.lowercase() != "driver"){
                dri_const.visibility = View.GONE
                con_licence_date.visibility = View.GONE
            } else {
                dri_const.visibility = View.VISIBLE
                con_licence_date.visibility = View.VISIBLE
            }
        }

        sp_project.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            callsupervisorapi(selectedItem)
        }

        sp_induction_status.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            if(selectedItem.lowercase() == "yes"){
                con_induction_date.visibility = View.VISIBLE
            } else {
                con_induction_date.visibility = View.GONE
            }
        }

        sp_sub_contractor.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            for(x in contractormapinglist){
                if(selectedItem == x.username){
                    edt_contractor_contact_number.setText(x.phone)
                }
            }
        }
        sp_medical_test_status.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            if(selectedItem.lowercase() == "yes"){
                con_medical_date.visibility = View.VISIBLE
            } else {
                con_medical_date.visibility = View.GONE
            }
        }
        setspinneradaptors()

        edt_aadhaar_card.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d("aadharValidate2",s.toString())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("aadharValidate",count.toString())
                if(s?.length == 12){
                  callaadharvalidation()
                }
            }
        })
    }

    private fun setEditable() {
        if(approvalstatus == "1" && rollid == "2"){
            Log.d("commingInside","comming")
            sp_medical_test_status.isClickable = false
            sp_medical_test_status.isEnabled = false
            sp_induction_status.isClickable = false
            sp_induction_status.isEnabled = false
            edt_medical_date.isEnabled = false
            edt_medical_date.isClickable = false
            induction_date.isEnabled = false
            induction_date.isClickable = false
            sp_report_is_ok.isEnabled = false
            sp_report_is_ok.isClickable = false
            sp_medical_test_status.isFocusableInTouchMode = false
            sp_induction_status.isFocusableInTouchMode = false
            induction_date.isFocusableInTouchMode = false
            sp_medical_test_status.dismissDropDown()
            sp_induction_status.dismissDropDown()
            sp_induction_status.setDropDownHeight(0)
            sp_medical_test_status.setDropDownHeight(0)
            sp_report_is_ok.setDropDownHeight(0)
            var yeslist = ArrayList<String>()
            yeslist.add("Yes")
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, yeslist)
            sp_medical_test_status.setAdapter(adapter)
            sp_induction_status.setAdapter(adapter)

        }else{
            sp_project.isEnabled = false
            sp_project.isClickable = false
            sp_skill_set.isEnabled = false
            sp_skill_set.isClickable = false
            sp_skill_type.isEnabled = false
            sp_skill_type.isClickable = false
            sp_sub_contractor.isClickable = false
            sp_sub_contractor.isEnabled = false
            sp_Supervisor_name.isClickable = false
            sp_Supervisor_name.isEnabled = false
            sp_medical_test_status.isClickable = false
            sp_medical_test_status.isEnabled = false
            sp_induction_status.isClickable = false
            sp_induction_status.isEnabled = false
            edt_doj.inputType = InputType.TYPE_NULL
            edt_doj.isEnabled = false
            edt_doj.isClickable = false
            edt_licence_expire.isClickable = false
            edt_licence_expire.isEnabled = false
            edt_medical_date.isEnabled = false
            edt_medical_date.isClickable = false
            induction_date.isEnabled = false
            induction_date.isClickable = false
            edt_contractor_contact_number.inputType = InputType.TYPE_NULL
            edt_driving_licence.inputType = InputType.TYPE_NULL
            edt_aadhaar_card.inputType = InputType.TYPE_NULL
            edt_medical_date.inputType = InputType.TYPE_NULL
        }
    }

    private fun validateform() {
        var edt_doj1: String = edt_doj.text.toString()
        var doj_send : String = ""
        val currentDate = LocalDate.now()
        var differenceInMonths: Long = 0
        if (!TextUtils.isEmpty(edt_doj.text)) {
        var dates = edt_doj1.split("-")
        var year = if (dates.get(2).length == 4 && !TextUtils.isEmpty(edt_doj.text)) {
            dates.get(2)
        } else {
            dates.get(0)
        }
        var day = if (dates.get(0).length == 2) {
            dates.get(0)
        } else {
            dates.get(2)
        }

        if (dates.size == 3 && year != "0000" && day != "00") {
            val receivedDate = LocalDate.of(
                year.toInt(),
                dates.get(1).toInt(),
                day.toInt()
            ) // Replace with the actual received date
            differenceInMonths = ChronoUnit.MONTHS.between(receivedDate, currentDate)
            flag = false
            doj_send = year+"-"+dates.get(1)+"-"+day
        } else {
            flag = true
            Toast.makeText(this, "enter valid DOJ", Toast.LENGTH_SHORT)
                .show()
        }
    }
        var medicalreport : String = ""
        if(con_medical_date.isVisible) {
            medicalreport = reversetheDate(edt_medical_date.text.toString(), true)
        }
        var drivingexpire : String =  ""
        if(con_licence_date.isVisible){
            drivingexpire = reversetheDate(edt_licence_expire.text.toString(), false)
        }
        var inductdate : String =  ""
        //if(con_induction_date.isVisible){
            if(reversetheDate(induction_date.text.toString(),false) == "00-00-0000" || reversetheDate(induction_date.text.toString(),false) == "0000-00-00" ){
                inductdate = ""//reversetheDate( induction_date.text.toString(), false)
            }else{
                inductdate = reversetheDate( induction_date.text.toString(), false)
            }
        //}

        val sharedPreferences: SharedPreferences =getSharedPreferences("WorkerInfoOnePref2", Context.MODE_PRIVATE)!!
        val sp_project1: String = sp_project.text.toString()
        val sp_skill_type1: String = sp_skill_type.text.toString()
        val sp_skill_set1: String = sp_skill_set.text.toString()
        val edt_worker_designation1: String = edt_worker_designation.text.toString()
        val sp_Supervisor_name1: String? = supervisormap.get(sp_Supervisor_name.text.toString())
        val sp_sub_contractor1: String? = contractormap.get(sp_sub_contractor.text.toString())
        val edt_contractor_contact_number1: String = edt_contractor_contact_number.text.toString()
        val sp_induction_status1: String =  sp_induction_status.text.toString()
        val edt_aadhaar_card1: String = edt_aadhaar_card.text.toString()
        val sp_medical_test_status1: String = sp_medical_test_status.text.toString()
        val sp_report_is_ok1: String = sp_report_is_ok.text.toString()
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        if (!TextUtils.isEmpty(sp_project1)) {
            if (!TextUtils.isEmpty(sp_skill_type1)) {
                    if (!TextUtils.isEmpty(sp_skill_set1)) {
                        if (!TextUtils.isEmpty(doj_send) && differenceInMonths < 1 && flag == false) {
                            if (!TextUtils.isEmpty(sp_Supervisor_name1)) {
                                if (!TextUtils.isEmpty(sp_sub_contractor1)) {
                                    if (!TextUtils.isEmpty(edt_contractor_contact_number1) && edt_contractor_contact_number1.length == 10) {
                                        if (!TextUtils.isEmpty(edt_aadhaar_card1) && edt_aadhaar_card1.length == 12) {
                                            if (!TextUtils.isEmpty(sp_medical_test_status1) /*&& sp_medical_test_status.text.toString() != "No"*/) {
                                                if (!con_medical_date.isVisible  || con_medical_date.isVisible && !TextUtils.isEmpty(edt_medical_date.text) && medicalmonth == "0") {
                                                    if (!con_licence_date.isVisible || con_licence_date.isVisible && !TextUtils.isEmpty(edt_driving_licence.text) && !TextUtils.isEmpty(edt_licence_expire.text) && isDLDateValid(edt_licence_expire.text.toString()) == true) {
                                                        if(approval == false || rollid == "1" || /*sp_induction_status.text.toString() != "No"*/ sp_induction_status.text.toString() == "Yes" && !TextUtils.isEmpty(induction_date.text) && induction_date.text.toString() != "00-00-0000" && induction_date.text.toString() != "0000-00-00" ) {
                                                            if (!ti_state.isVisible || !TextUtils.isEmpty(sp_status.text) && ti_state.isVisible) {
                                                                if(!ti_report_ok.isVisible || !TextUtils.isEmpty(sp_report_is_ok1) && sp_report_is_ok1 == "Yes" ){
                                                                    if(aadharvalidation == true){
                                                                editor.putString(
                                                                Project,
                                                                projectMap.get(sp_project1)
                                                             )
                                                            editor.putString(
                                                                skill_type,
                                                                if(sp_skill_type1.lowercase() == "skilled" ){
                                                                    "skilled"
                                                                } else {
                                                                    "unskilled"
                                                                }
                                                            )
                                                            editor.putString(
                                                                skill_set,
                                                                skillsetMap.get(sp_skill_set1)
                                                            )
                                                            editor.putString(
                                                                worker_designation,
                                                                edt_worker_designation1
                                                            )
                                                            editor.putString(doj, doj_send)
                                                            editor.putString(
                                                                Supervisor_name,
                                                                sp_Supervisor_name1
                                                            )
                                                            editor.putString(
                                                                sub_contractor,
                                                                sp_sub_contractor1
                                                            )
                                                            editor.putString(
                                                                contractor_contact_number,
                                                                edt_contractor_contact_number1
                                                            )
                                                            editor.putString(
                                                                induction_status,
                                                                if(sp_induction_status1 == "Yes"){
                                                                    "1"
                                                                }else{
                                                                    "2"
                                                                }
                                                            )
                                                            editor.putString(
                                                                aadhaar_card,
                                                                edt_aadhaar_card1
                                                            )
                                                            editor.putString(
                                                                medical_test_status,
                                                                if(sp_medical_test_status1 == "Yes"){
                                                                    "1"
                                                                }else{
                                                                    "2"
                                                                }
                                                            )
                                                            editor.putString(
                                                                report_is_ok,
                                                                if(sp_report_is_ok1 == "Yes"){
                                                                    "1"
                                                                }else{
                                                                    "2"
                                                                }
                                                            )
                                                            editor.putString(
                                                                "exp_years",
                                                                sp_year_exp.text.toString()
                                                            )
                                                            editor.putString(
                                                                "exp_months",
                                                                sp_months_exp.text.toString()
                                                            )
                                                            editor.putString(
                                                                "driving_licence",
                                                                edt_driving_licence.text.toString()
                                                            )
                                                            editor.putString(
                                                                "licence_exp",
                                                                drivingexpire
                                                            )
                                                            editor.putString(
                                                                "induction_date",
                                                                inductdate
                                                            )
                                                            editor.putString(
                                                                "workerid",
                                                                edt_worker_id.text.toString()
                                                            )
                                                            editor.putString(
                                                                "edt_remarks",
                                                                edt_remarks.text.toString()
                                                            )
                                                            editor.putString(
                                                                "medical_test_date",
                                                                medicalreport
                                                            )
                                                            editor.putString(
                                                                "status",
                                                                if(sp_status.text.toString() == "Active"){
                                                                    "1"
                                                                }else{
                                                                    "0"
                                                                }
                                                            )
                                                            editor.commit()
                                                            val intent = Intent(
                                                                applicationContext,
                                                                WorkerInformationNext2Activity::class.java
                                                            )
                                                            startActivity(intent)

                                                                } else {
                                                                    Toast.makeText(this, "Aadhar card already present in database", Toast.LENGTH_SHORT)
                                                                            .show()
                                                                }
                                                            } else {
                                                                Toast.makeText(this, "Please select Report is ok or not", Toast.LENGTH_SHORT)
                                                                    .show()
                                                            }
                                                            } else {
                                                                Toast.makeText(this, "Please select status", Toast.LENGTH_SHORT)
                                                                    .show()
                                                            }
                                                        } else {
                                                            /*if(TextUtils.isEmpty(sp_status.text)){
                                                               // sp_status.error = "select status"
                                                                Toast.makeText(this, "Please select status", Toast.LENGTH_SHORT)
                                                                    .show()
                                                            }*/
                                                            if(!TextUtils.isEmpty(sp_induction_status.text) || sp_induction_status.text.toString() == "No"){
                                                                //sp_induction_status.error = "select induction status"
                                                                Toast.makeText(this, "Please Complete Induction", Toast.LENGTH_SHORT)
                                                                    .show()
                                                            } else if (TextUtils.isEmpty(induction_date.text)){
                                                                //induction_date.error = "enter induction date"
                                                                Toast.makeText(this, "Please enter induction date", Toast.LENGTH_SHORT)
                                                                    .show()
                                                            } else {
                                                                Toast.makeText(this, "Please Complete Induction", Toast.LENGTH_SHORT)
                                                                    .show()
                                                            }
                                                        }
                                                    } else {
                                                        if(TextUtils.isEmpty(edt_driving_licence.text)){
                                                            Toast.makeText(this, "Please Enter Driving licence number", Toast.LENGTH_SHORT)
                                                                .show()
                                                        }else if(isDLDateValid(edt_licence_expire.text.toString()) == false){
                                                            Toast.makeText(this, "Driving licence date expired, Please enter valid driving licence date", Toast.LENGTH_SHORT)
                                                                .show()
                                                        } else {
                                                            Toast.makeText(this, " Invalid driving licence or Driving licence date expired", Toast.LENGTH_SHORT)
                                                                .show()
                                                        }
                                                    }
                                                } else {
                                                    if(medicalmonth != "0" ){
                                                        Toast.makeText(this, "Medical test date must be with in 1 month", Toast.LENGTH_SHORT)
                                                            .show()
                                                    }
                                                    else {
                                                       // edt_medical_date.error = "enter medical text date"
                                                        Toast.makeText(this, "Please enter medical test date", Toast.LENGTH_SHORT)
                                                            .show()
                                                    }
                                            }
                                            } else {
                                               // sp_medical_test_status.error = "select medical test status"
                                                Toast.makeText(this, "Please select medical test status ", Toast.LENGTH_SHORT)
                                                    .show()
                                            }
                                        } else {
                                           // edt_aadhaar_card.error = "Enter Valid aadhaar number"
                                            Toast.makeText(this, "Please enter valid aadhaar number", Toast.LENGTH_SHORT)
                                                .show()
                                        }

                                    } else {
                                        //edt_contractor_contact_number.error = "Enter Valid contractor number"
                                        Toast.makeText(this, "Please Enter Valid contractor contact number", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                } else {
                                   // sp_sub_contractor.error = "select contractor"
                                    Toast.makeText(this, "Please select contractor", Toast.LENGTH_SHORT)
                                        .show()
                                }

                            } else {
                               // sp_Supervisor_name.error = "select supervisor"
                                Toast.makeText(this, "Please select supervisor", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            edt_doj.error = "select DOJ"
                            if(differenceInMonths > 1){
                                Toast.makeText(this, "DOJ is not more then 1 month. ", Toast.LENGTH_SHORT)
                                    .show()
                            }else{
                                Toast.makeText(this, "Please enter valid DOJ", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } else {
                       // sp_skill_set.error = "select skill set"
                        Toast.makeText(this, "please select skill set", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    //sp_skill_type.error = "select skill type"
                    Toast.makeText(this, "Please select skill type", Toast.LENGTH_SHORT)
                        .show()
                }

        } else {
            //sp_project.error = "Select Project"
            Toast.makeText(this, "Please select project", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun callsupervisorapi(selectedItem: String) {
        var pro_id = projectMap.get(selectedItem)
        if(pro_id != ""){
            val webServices = WebServices<Any>(this@WorkerInformationNext1Activity)
            webServices.Supervisor(WebServices.ApiType.supervisors,pro_id)
        }
    }

    private fun callaadharvalidation() {
        var aadharvalid = edt_aadhaar_card.text.toString()
        if(aadharvalid != "" && aadharvalid.length == 12){
            val webServices = WebServices<Any>(this@WorkerInformationNext1Activity)
            webServices.aadharvalidate(WebServices.ApiType.aadharvalidate,aadharvalid)
        }else{
            Toast.makeText(this, "Invalid aadhar card", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun callmasterAps() {
        GlobalScope.launch(Dispatchers.IO) {
            val webServices = WebServices<Any>(this@WorkerInformationNext1Activity)
            webServices.projects(WebServices.ApiType.projects, userid)

            val webServices2 = WebServices<Any>(this@WorkerInformationNext1Activity)
            webServices2.skills(WebServices.ApiType.skills)

            val webServices3 = WebServices<Any>(this@WorkerInformationNext1Activity)
            webServices3.training(WebServices.ApiType.training)

            if (rollid == "1") {
                val webServices4 = WebServices<Any>(this@WorkerInformationNext1Activity)
                webServices4.Conctractors(WebServices.ApiType.contractors, userid)

               /* val webServices5 = WebServices<Any>(this@WorkerInformationNext1Activity)
                webServices5.Supervisor(WebServices.ApiType.supervisors, "0")*/

            } else {
                val webServices4 = WebServices<Any>(this@WorkerInformationNext1Activity)
                webServices4.Conctractors(WebServices.ApiType.contractors, "0")

                /*val webServices5 = WebServices<Any>(this@WorkerInformationNext1Activity)
                webServices5.Supervisor(WebServices.ApiType.supervisors, userid)*/
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResponse(
        response: Any?,
        URL: WebServices.ApiType?,
        isSucces: Boolean,
        code: Int
    ) {
        when (URL) {
            WebServices.ApiType.training -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val viewTrainingMaster = response as ViewTrainingMaster
                    if (viewTrainingMaster?.isEmpty() == false) {
                        for (x in viewTrainingMaster) {
                            traininglist.add(x.training_name)
                            trainingMap.put(x.training_name,x.training_master_id)
                        }
                    } else {
                        Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            WebServices.ApiType.skills -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val viewSkillsetMaster = response as ViewSkillsetMaster
                    if (viewSkillsetMaster?.isEmpty() == false) {
                        for (x in viewSkillsetMaster) {
                            if(x.skill_set_name != "Worker"){
                                skillsList.add(x.skill_set_name)
                                skillsetMap.put(x.skill_set_name,x.skill_set_id)
                                skillsetreverse.put(x.skill_set_id,x.skill_set_name)
                            }
                        }
                        sp_skill_set.setText(skillsetreverse.get(skillid))
                        if(skillsetreverse.get(skillid)?.lowercase() != "driver"){
                            dri_const.visibility = View.GONE
                            con_licence_date.visibility = View.GONE
                        } else {
                            dri_const.visibility = View.VISIBLE
                            con_licence_date.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(this, "Unable to load Skills", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }
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
                            projectMap.put(x.project_name,x.id)
                            projectsreverselist.put(x.id,x.project_name)
                        }
                        sp_project.setText(projectsreverselist.get(prijectid))
                        if(type == true && prijectid != ""){
                            projectsreverselist.get(prijectid)?.let { callsupervisorapi(it) }
                        }
                    } else {
                        Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            WebServices.ApiType.aadharvalidate ->{
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                        var attendanceres = response as Generalresponsce
                        if (attendanceres.status == 200 ) {
                            if(attendanceres.code == "success"){
                                aadharvalidation = true
                            } else {
                                aadharvalidation = false
                            }
                        }
                    } else {
                        Toast.makeText(this, "Something Went wrong, aadhar not validated", Toast.LENGTH_SHORT)
                                .show()
                    }
            }
            WebServices.ApiType.contractors -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val contractorListResponsce = response as ContractorListResponsce
                    if (contractorListResponsce?.isEmpty() == false) {
                        val phonemap = HashMap<String, String>()
                        contractormapinglist = response
                        for (x in contractorListResponsce) {
                            contractorList.add(x.username)
                            contractorreverse.put(x.id,x.username)
                            contractormap.put(x.username,x.id)
                            phonemap.put(x.id, x.phone)
                        }
                        sp_sub_contractor.setText(contractorreverse.get(conId))
                        if(rollid == "1"){
                            contractorList.clear()
                            contractorList.add(contractorreverse.get(userid).toString())
                            sp_sub_contractor.setText(contractorreverse.get(userid))
                            edt_contractor_contact_number.setText(phonemap.get(userid))
                        }
                        val adapter5 = ArrayAdapter(this,
                            android.R.layout.simple_list_item_1, contractorList)
                        sp_sub_contractor.setAdapter(adapter5)
                    } else {
                        Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            WebServices.ApiType.supervisors -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val supervisorsresponsce = response as SupervisorListResponsce
                    if (supervisorsresponsce?.isEmpty() == false) {
                        supervisorList.clear()
                        for (x in supervisorsresponsce) {
                            supervisorList.add(x.username)
                            supervisorreverse.put(x.id, x.username)
                            supervisormap.put(x.username,x.id)
                        }
                        sp_Supervisor_name.setText(supervisorreverse.get(supId))
                        if(rollid == "2"){
                            Log.d("supervisorlistcheck2",supervisorList.size.toString() +"--"+supervisorList)
                            supervisorList.clear()
                            supervisorList.add(supervisorreverse.get(userid).toString())
                            sp_Supervisor_name.setText(supervisorreverse.get(userid))
                        }
                        val adapter6 = ArrayAdapter(this,
                            android.R.layout.simple_list_item_1, supervisorList)
                        sp_Supervisor_name.setAdapter(adapter6)
                        Log.d("supervisorlistcheck3",supervisorList.size.toString() +"--"+supervisorList)

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



    private fun setspinneradaptors() {
        /*val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, skillsList)
        sp_skill_set.setAdapter(adapter)*/

        val adapter2 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, projectslist)
        sp_project.setAdapter(adapter2)

        val adapter3 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, skillType)
        sp_skill_type.setAdapter(adapter3)

        val adapter4 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, generaloption)

        val adapter12 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, medicaloption)

        sp_report_is_ok.setAdapter(adapter4)
        sp_induction_status.setAdapter(adapter4)
        sp_medical_test_status.setAdapter(adapter12)

        val adapter5 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, contractorList)
        sp_sub_contractor.setAdapter(adapter5)

        Log.d("supervisorlistcheck",supervisorList.size.toString() +"--"+supervisorList)

        val adapter6 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, supervisorList)
        sp_Supervisor_name.setAdapter(adapter6)

        val adapter7 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, status)
        sp_status.setAdapter(adapter7)

        val adapter8 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, experienceyearsList)
        sp_year_exp.setAdapter(adapter8)

        val adapter9 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, experiencemonthList)
        sp_months_exp.setAdapter(adapter9)

        val adapter10 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, feedback)
        sp_feed_back.setAdapter(adapter10)
    }


    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, this, year, month, dayOfMonth)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
    private fun showDatePickerDialog2() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, this, year, month, dayOfMonth)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        if(dateview==0){
            edt_doj.setText(formattedDate)
        }else if(dateview == 2){
            induction_date.setText(formattedDate)
        }else if(dateview == 3){
            edt_licence_expire.setText(formattedDate)
        }
        else{
            edt_medical_date.setText(formattedDate)
        }
    }
    private fun bottomnavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavShift)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                  val intentScan = Intent(this@WorkerInformationNext1Activity, MainActivity::class.java)
                  startActivity(intentScan)
                   true
                }
                R.id.it_scan -> {
                    if(rollid != "1") {
                        val intentScan = Intent(this@WorkerInformationNext1Activity, ScanIdActivity::class.java)
                        startActivity(intentScan)
                    }
                    true
                }

                R.id.it_worker -> {
                    if(rollid != "1") {
                        val intentScan = Intent(this@WorkerInformationNext1Activity, WorkerListActivity::class.java)
                        startActivity(intentScan)
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
    }


    private fun callArray() {
        status.add("Active")
        status.add("InActive")

        feedback.add("Good")
        feedback.add("Average")
        feedback.add("Below Average")

        generaloption.add("Yes")
        generaloption.add("No")

        medicaloption.add("Yes")
        medicaloption.add("No")

        skillType.add("Skilled")
        skillType.add("Un Skilled")

        experiencemonthList.add("0")
        experiencemonthList.add("1")
        experiencemonthList.add("2")
        experiencemonthList.add("3")
        experiencemonthList.add("4")
        experiencemonthList.add("5")
        experiencemonthList.add("6")
        experiencemonthList.add("7")
        experiencemonthList.add("8")
        experiencemonthList.add("9")
        experiencemonthList.add("10")
        experiencemonthList.add("11")
        experiencemonthList.add("12")
        experienceyearsList.add("0")
        experienceyearsList.add("1")
        experienceyearsList.add("2")
        experienceyearsList.add("3")
        experienceyearsList.add("4")
        experienceyearsList.add("5")
        experienceyearsList.add("6")
        experienceyearsList.add("7")
        experienceyearsList.add("8")
        experienceyearsList.add("9")
        experienceyearsList.add("10")
        experienceyearsList.add("11")
        experienceyearsList.add("12")
        experienceyearsList.add("13")
        experienceyearsList.add("14")
        experienceyearsList.add("15")
        experienceyearsList.add("16")
        experienceyearsList.add("17")
        experienceyearsList.add("18")
        experienceyearsList.add("19")
    }

    fun reversetheDate(text: String, b: Boolean): String {
        var dates = text.split("-")
        var differenceInMonths: Long = 0
        if(dates.size == 3) {
            var year = if (dates?.get(2)?.length == 4 && !TextUtils.isEmpty(edt_doj.text)) {
                dates.get(2)
            } else {
                dates.get(0)
            }
            var day = if (dates?.get(0)?.length == 2) {
                dates.get(0)
            } else {
                dates.get(2)
            }

            if (dates?.size == 3 && year != "0000" && day != "00") {
                val receivedDate = LocalDate.of(
                    year.toInt(),
                    dates.get(1).toInt(),
                    day.toInt()
                ) // Replace with the actual received date
                if(b){
                    medicalmonth = ChronoUnit.MONTHS.between(receivedDate, LocalDate.now()).toString()
                    Log.d("checkmedicalmonth", medicalmonth.toString())
                }
                //flag = false
                //doj_send = year+"-"+dates.get(1)+"-"+day
            } else {
                // flag = true
                //Toast.makeText(this, "enter valid Medical report", Toast.LENGTH_SHORT)
                  //  .show()
            }
            return year+"-"+dates.get(1)+"-"+day


        }else{
            return "0000-00-00"
        }
    }

    fun monthsBetween(a: Date, b: Date?): Int {
        var b = b
        val cal = Calendar.getInstance()
        if (a.before(b)) {
            cal.time = a
        } else {
            cal.time = b
            b = a
        }
        var c = 0
        while (cal.time.before(b)) {
            cal.add(Calendar.MONTH, 1)
            c++
        }
        return c - 1
    }

    fun currecttheDate(text: String): String {
        var dates = text.split("-")
        var differenceInMonths: Long = 0
        if(dates.size == 3) {
            var year = if (dates?.get(2)?.length == 4 && !TextUtils.isEmpty(edt_doj.text)) {
                dates.get(2)
            } else {
                dates.get(0)
            }
            var day = if (dates.get(0).length == 2) {
                dates.get(0)
            } else {
                dates.get(2)
            }

            if (dates.size == 3 && year != "0000" && day != "00") {
                val receivedDate = LocalDate.of(
                    year.toInt(),
                    dates.get(1).toInt(),
                    day.toInt()
                )
            } else {
                // flag = true
                // Toast.makeText(this, "enter valid Medical report", Toast.LENGTH_SHORT)
                //    .show()
            }

            return day + "-" + dates.get(1) + "-" + year

        }else {
            return "00-00-0000"
        }
    }


    fun isDLDateValid(expiryDateStr: String): Boolean {
        if(expiryDateStr != "00-00-0000" && expiryDateStr != "" && expiryDateStr != null){
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val currentDate = LocalDate.now()
            val expiryDate = LocalDate.parse(expiryDateStr, formatter)

            return expiryDate.isAfter(currentDate) || expiryDate.isEqual(currentDate)
        }else{
            return false
        }
    }
}