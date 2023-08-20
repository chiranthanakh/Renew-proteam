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
import com.proteam.renew.Adapter.ContractorAttendenceListAdaptor
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

class ContractorAttendanceList : AppCompatActivity(), OnResponseListener<Any>, CheckboxListner, DatePickerDialog.OnDateSetListener {

    var userid: String = ""
    var rollid : String = ""
    var progressDialog: ProgressDialog? = null
    val rv_attendance: RecyclerView by lazy { findViewById(R.id.rv_attendance) }
    val iv_filter_view : ImageView by lazy { findViewById(R.id.iv_filter_view) }
    val no_data_linear_layout : LinearLayout by lazy { findViewById(R.id.no_data_linear_layout) }
    val checkbox = HashMap<String,Boolean>()
    var attendancelist : Attendance_new_list ? = null
    private val calendar: Calendar = Calendar.getInstance()
    var date : EditText? = null
    var contId : String = ""
    var proid : String = ""
    var dateof : String = ""
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
        setContentView(R.layout.contractor_attendence_list)
        val sharedPreferences2: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
        rollid = sharedPreferences2.getString("rollid", "")!!
        userid = sharedPreferences2.getString("userid", "")!!

        val sharedPreferences: SharedPreferences =getSharedPreferences("Attendenceid", Context.MODE_PRIVATE)!!
         contId = sharedPreferences.getString("contractorid", "")!!
         proid = sharedPreferences.getString("projectid", "")!!
         dateof = sharedPreferences.getString("date", "")!!

        bottomnavigation()
        rv_attendance.layoutManager = LinearLayoutManager(this)

        Log.d("testingcheck3",true.toString())
        iv_filter_view.setOnClickListener {
           // showCustomDialog()
        }

    }

    override fun onResume() {
        super.onResume()
        callListapi(contId)
        //callListapifilter(contId,proid,dateof)
    }

    private fun callListapi(contId: String) {
        progressDialog = ProgressDialog(this@ContractorAttendanceList)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                val webServices2 = WebServices<Any>(this@ContractorAttendanceList)
                webServices2.cont_attendance_list(WebServices.ApiType.attendancelist, contId)
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
                        }
                        val adapter = ContractorAttendenceListAdaptor(attendancelist!!,this,getApplicationContext(),false)
                        rv_attendance.adapter = adapter
                        no_data_linear_layout.visibility = View.GONE
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


    private fun bottomnavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavShift)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intentScan = Intent(this@ContractorAttendanceList, MainActivity::class.java)
                    startActivity(intentScan)
                    finish()
                    true
                }
                R.id.it_scan -> {
                    val intentScan = Intent(this@ContractorAttendanceList, ScanIdActivity::class.java)
                    startActivity(intentScan)
                    finish()
                    true
                }

                R.id.it_worker -> {
                    if(rollid == "1") {
                        val intentScan = Intent(this@ContractorAttendanceList, WorkerListActivity::class.java)
                        startActivity(intentScan)
                        finish()
                    }
                    true
                }
                else -> false
            }
        }    }


    override fun onCheckboxChanged(empid_id: String, id: String, isChecked: Boolean) {
        checkbox.put(id,isChecked)
       // empids = empids+","+ empid_id
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