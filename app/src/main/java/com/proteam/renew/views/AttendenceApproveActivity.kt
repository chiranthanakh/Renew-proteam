package com.proteam.renew.views

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.proteam.renew.Adapter.AttendenceApproveListAdapter
import com.proteam.renew.R
import com.proteam.renew.requestModels.AttendancApproveRequest
import com.proteam.renew.requestModels.AttendanceFilterRequest
import com.proteam.renew.responseModel.Attendance_new_list
import com.proteam.renew.responseModel.ContractorListResponsce
import com.proteam.renew.responseModel.ViewProjectMaster
import com.proteam.renew.responseModel.generalGesponce
import com.proteam.renew.utilitys.CheckboxListner
import com.proteam.renew.utilitys.OnResponseListener
import com.proteam.renew.utilitys.WebServices
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AttendenceApproveActivity : AppCompatActivity(), OnResponseListener<Any>, CheckboxListner, DatePickerDialog.OnDateSetListener {

    var userid: String = ""
    var rollid : String = ""
    var progressDialog: ProgressDialog? = null
    val rv_attendance: RecyclerView by lazy { findViewById(R.id.rv_attendance) }
    val tv_attendance_approve: TextView by lazy { findViewById(R.id.tv_attendance_approve) }
    val ch_approve_all :CheckBox by lazy { findViewById(R.id.ch_approve_all) }
    val iv_filter_view : ImageView by lazy { findViewById(R.id.iv_filter_view) }
    val no_data_linear_layout : LinearLayout by lazy { findViewById(R.id.no_data_linear_layout) }
    val checkbox = HashMap<String,Boolean>()
    var attendancelist : Attendance_new_list ? = null
    private val calendar: Calendar = Calendar.getInstance()
    var date : EditText? = null
    var empids : String = ""
    var contId : String = ""
    var proid : String = ""
    var dateof : String = ""
    var pendindapproval : Boolean = false
    val projectMap = HashMap<String, String>()
    val contractorMap = HashMap<String, String>()
    val contractorreverseMap = HashMap<String, String>()
    val projectslist = ArrayList<String>()
    val contractorlist = ArrayList<String>()
    val projectsreverselist = HashMap<String, String>()
    var contractorlistmaping = ArrayList<String>()
    var selectall : Boolean = false
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendence_approve)
        val sharedPreferences2: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
        rollid = sharedPreferences2.getString("rollid", "")!!
        userid = sharedPreferences2.getString("userid", "")!!

        val sharedPreferences: SharedPreferences =getSharedPreferences("Attendenceid", Context.MODE_PRIVATE)!!
         contId = sharedPreferences.getString("contractorid", "")!!
         proid = sharedPreferences.getString("projectid", "")!!
         dateof = sharedPreferences.getString("date", "")!!

        bottomnavigation()
        rv_attendance.layoutManager = LinearLayoutManager(this)

        tv_attendance_approve.setOnClickListener {
            callAttendanceApprove()
        }
        Log.d("testingcheck3",true.toString())

        ch_approve_all.setOnClickListener {
            if (ch_approve_all.isChecked) {
                Log.d("testingcheck3",true.toString())
                selectall =true
                val adapter = attendancelist?.let { AttendenceApproveListAdapter(it,this,getApplicationContext(),true) }
                rv_attendance.adapter = adapter
            } else {
                Log.d("testingcheck4",false.toString())
                selectall = false
                val adapter = attendancelist?.let { AttendenceApproveListAdapter(it,this,getApplicationContext(),false) }
                rv_attendance.adapter = adapter
            }
        }


        iv_filter_view.setOnClickListener {
           // showCustomDialog()
        }

       /* ch_approve_all.setOnCheckedChangeListener { _, isChecked ->
            // Handle the checkbox click event here
            if (isChecked) {
                Log.d("testingcheck3",true.toString())

                val adapter = attendancelist?.let { AttendenceApproveListAdapter(it,this,getApplicationContext(),true) }
                rv_attendance.adapter = adapter
            } else {
                Log.d("testingcheck4",false.toString())

                val adapter = attendancelist?.let { AttendenceApproveListAdapter(it,this,getApplicationContext(),false) }
                rv_attendance.adapter = adapter
            }
        }*/

    }

    override fun onResume() {
        super.onResume()
        //callListapi(contId)
        callListapifilter(contId,proid,dateof)
    }

    private fun callAttendanceApprove() {
        progressDialog = ProgressDialog(this@AttendenceApproveActivity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()

                var attendance: String = ""
                var empid: String = ""

                if (selectall) {
                    attendancelist?.forEach {
                        if(it.as_status == "1"){

                        }else{
                            attendance = attendance + "," + it.attendance_list_id
                            empids = empids + "," + it.employee_id
                        }
                    }
                } else {
                    checkbox.forEach { (key, value) ->
                        if (value) {
                            if (attendance != "") {
                                attendance = attendance + "," + key
                            } else {
                                attendance = key
                            }
                        }
                        println("$key = $value")
                    }
                }
                if(attendance != ""){
                    var approve = AttendancApproveRequest(attendance,empids, userid)
                    val webServices2 = WebServices<Any>(this@AttendenceApproveActivity)
                    webServices2.AttendanceApprove(WebServices.ApiType.attendanceapprove,approve)
                }else{
                    if (progressDialog != null) {
                        if (progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
                        }
                    }
                    Toast.makeText(this, "attendance already approved", Toast.LENGTH_SHORT)
                            .show()
                }
            } else {
            }
        }    }

    private fun callListapi(contId: String) {
        progressDialog = ProgressDialog(this@AttendenceApproveActivity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                val webServices2 = WebServices<Any>(this@AttendenceApproveActivity)
                webServices2.attendance_list(WebServices.ApiType.attendancelist, contId)
            } else {
            }
        }
    }

    private fun callListapifilter(conId: String, date2: String, project: String) {
        progressDialog = ProgressDialog(this@AttendenceApproveActivity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                var filterBody = AttendanceFilterRequest(conId,project,date2, userid )
                val webServices2 = WebServices<Any>(this@AttendenceApproveActivity)
                webServices2.attendance_list2(WebServices.ApiType.attendancefilter, filterBody)
            } else {

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

            WebServices.ApiType.attendancelist -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                     attendancelist = response as Attendance_new_list
                    if (attendancelist?.isEmpty() == false) {

                        for(x in attendancelist!!){
                            contractorlist.add(x.username)
                            contractorMap.put(x.username,x.contractor_id)
                            if(x.as_status != "1"){
                                pendindapproval = true
                                Log.d("contractorlist12323",pendindapproval.toString())

                            }
                        }

                        val adapter = AttendenceApproveListAdapter(attendancelist!!,this,getApplicationContext(),false)
                        rv_attendance.adapter = adapter

                        no_data_linear_layout.visibility = View.GONE
                        Log.d("contractorlist12323",pendindapproval.toString())

                        if(!pendindapproval){
                            Log.d("contractorlist123",pendindapproval.toString())
                            tv_attendance_approve.visibility = View.GONE
                        }
                    } else {
                        no_data_linear_layout.visibility = View.VISIBLE
                        Toast.makeText(this, "No Approval list", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            WebServices.ApiType.attendanceapprove -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val attendanceres = response as generalGesponce
                    if (attendanceres.status == "200" ) {
                        Toast.makeText(this, "Attendance Approved successfully ", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this, "Something Went wrong. Please check Internet", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT)
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
                            //if(contractorlist.contains(x.user_id)){
                                projectslist.add(x.project_name)
                                projectMap.put(x.project_name,x.id)
                                projectsreverselist.put(x.id,x.project_name)
                           // }
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
            WebServices.ApiType.contractors -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val contractorListResponsce = response as ContractorListResponsce
                    if (contractorListResponsce?.isEmpty() == false) {
                        for (x in contractorListResponsce) {
                            Log.d("contractorlist123",contractorlist.toString() +"--"+x.id)
                            if(contractorlist.contains(x.id)){
                                contractorlistmaping.add(x.username)
                                contractorMap.put(x.id,x.username)
                                contractorreverseMap.put(x.username,x.id)
                            }
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

            WebServices.ApiType.attendancefilter -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    attendancelist = response as Attendance_new_list
                    if (attendancelist?.isEmpty() == false) {

                        for(x in attendancelist!!){
                            contractorlist.add(x.username)
                            contractorMap.put(x.username,x.contractor_id)
                            if(x.as_status != "1"){
                                pendindapproval = true
                            }
                        }
                        if(!pendindapproval){
                            tv_attendance_approve.visibility = View.GONE
                        }
                        val adapter = AttendenceApproveListAdapter(attendancelist!!,this,getApplicationContext(),false)
                        rv_attendance.adapter = adapter
                    } else {
                        Toast.makeText(this, "Something Went wrong. Please check Internet", Toast.LENGTH_SHORT)
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

    private fun showCustomDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.approve_filter_dialog)
        dialog.setCancelable(false)
        var profile = dialog?.findViewById<AutoCompleteTextView>(R.id.sp_project)
         date = dialog.findViewById<TextInputEditText>(R.id.edt_date)
        var contractor = dialog.findViewById<AutoCompleteTextView>(R.id.sp_sub_contractor)
        var filter = dialog.findViewById<TextView>(R.id.tv_filter)
        var cancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        callfilterapis()

        (date as TextInputEditText?)?.setOnClickListener {
            showDatePickerDialog()
        }

        val adapter5 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, projectslist)
        profile?.setAdapter(adapter5)

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, contractorlist)
        contractor?.setAdapter(adapter)

        filter.setOnClickListener {
            callListapifilter(contractor.text.toString(), (date as TextInputEditText?)?.text.toString(),profile?.text.toString())
            dialog.dismiss()
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun callfilterapis() {
        val webServices = WebServices<Any>(this@AttendenceApproveActivity)
        webServices.projects(WebServices.ApiType.projects, userid)

        val webServices4 = WebServices<Any>(this@AttendenceApproveActivity)
        webServices4.Conctractors(WebServices.ApiType.contractors, "0")
    }


    private fun bottomnavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavShift2)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intentScan = Intent(this@AttendenceApproveActivity, ApprovalsActivity::class.java)
                    startActivity(intentScan)
                    finish()
                    true
                }
                R.id.it_scan -> {
                    if(rollid != "1") {
                        val intentScan = Intent(this@AttendenceApproveActivity, TrainingAllocationListActivity::class.java)
                        startActivity(intentScan)
                        finish()
                    }
                    true
                }

                R.id.it_worker -> {
                    val intentScan = Intent(this@AttendenceApproveActivity, WorkerListActivity::class.java)
                    startActivity(intentScan)
                    finish()

                    true
                }
                else -> false
            }
        }    }


    override fun onCheckboxChanged(empid_id: String, id: String, isChecked: Boolean) {
        checkbox.put(id,isChecked)
        if(empids != ""){
            empids = empids+","+ empid_id
        }else{
            empids = empid_id
        }
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
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
            date?.setText(formattedDate)
    }
}