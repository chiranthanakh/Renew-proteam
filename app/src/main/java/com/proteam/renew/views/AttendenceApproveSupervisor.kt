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
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.proteam.renew.Adapter.AttendenceApproveListAdaptersup
import com.proteam.renew.R
import com.proteam.renew.requestModels.AttendancApproveRequest
import com.proteam.renew.requestModels.AttendanceFilterRequest
import com.proteam.renew.responseModel.Attendance_new_list
import com.proteam.renew.responseModel.Attendance_new_listItem
import com.proteam.renew.responseModel.ContractorListResponsce
import com.proteam.renew.responseModel.ViewProjectMaster
import com.proteam.renew.responseModel.generalGesponce
import com.proteam.renew.utilitys.CheckboxListner
import com.proteam.renew.utilitys.OnResponseListener
import com.proteam.renew.utilitys.WebServices
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AttendenceApproveSupervisor : AppCompatActivity(), OnResponseListener<Any>, CheckboxListner, DatePickerDialog.OnDateSetListener {

    var userid: String = ""
    var rollid : String = ""
    var progressDialog: ProgressDialog? = null
    val rv_attendance: RecyclerView by lazy { findViewById(R.id.rv_attendance) }
    val tv_attendance_approve: TextView by lazy { findViewById(R.id.tv_attendance_approve) }
    val iv_filter_view : ImageView by lazy { findViewById(R.id.iv_filter_view) }
    val checkbox = HashMap<String,Boolean>()
    var attendancelist : Attendance_new_list ? = null
    var completedlist = ArrayList<Attendance_new_listItem>()
    var pendinglist = ArrayList<Attendance_new_listItem>()
    private val calendar: Calendar = Calendar.getInstance()
    var date : EditText? = null
    private lateinit var bottomNavigationView: BottomNavigationView

    val no_data_linear_layout: LinearLayout by lazy { findViewById(R.id.no_data_linear_layout) }

    val projectMap = HashMap<String, String>()
    val contractorMap = HashMap<String, String>()
    val contractorreverseMap = HashMap<String, String>()
    val projectslist = ArrayList<String>()
    val contractorlist = ArrayList<String>()
    val projectsreverselist = HashMap<String, String>()
    var contractorlistmaping = ArrayList<String>()
    var selectall : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendence_approve2)
        val sharedPreferences2: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
        rollid = sharedPreferences2.getString("rollid", "")!!
        userid = sharedPreferences2.getString("userid", "")!!
        rv_attendance.layoutManager = LinearLayoutManager(this)


        callListapi()
        bottomnavigation()
        tv_attendance_approve.setOnClickListener {
            if(checkbox.isEmpty()){
            }else{
                callAttendanceApprove()
            }
        }
        Log.d("testingcheck3",true.toString())

        iv_filter_view.setOnClickListener {

                val popupMenu = PopupMenu(this@AttendenceApproveSupervisor, it)
                popupMenu.getMenuInflater().inflate(R.menu.menu_items_training, popupMenu.getMenu())

                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem -> // Handle menu item clicks here
                    when (menuItem.itemId) {
                        R.id.pending -> {
                            if(pendinglist.isEmpty()){
                                no_data_linear_layout.visibility = View.VISIBLE
                            }else{
                                no_data_linear_layout.visibility = View.GONE
                            }
                            val adapter = AttendenceApproveListAdaptersup(pendinglist!!,this,getApplicationContext(),false)
                            rv_attendance.adapter = adapter
                            return@OnMenuItemClickListener true
                        }

                        R.id.completed -> {
                            if(completedlist.isEmpty()){
                                no_data_linear_layout.visibility = View.VISIBLE
                            }else{
                                no_data_linear_layout.visibility = View.GONE
                            }
                            val adapter = AttendenceApproveListAdaptersup(completedlist!!,this,getApplicationContext(),false)
                            rv_attendance.adapter = adapter
                            return@OnMenuItemClickListener true
                        }

                        R.id.all -> {
                            if(attendancelist?.isEmpty() == true){
                                no_data_linear_layout.visibility = View.VISIBLE
                            }else{
                                no_data_linear_layout.visibility = View.GONE
                            }
                            val adapter = AttendenceApproveListAdaptersup(attendancelist!!,this,getApplicationContext(),false)
                            rv_attendance.adapter = adapter
                            return@OnMenuItemClickListener true
                        }

                    }
                    false
                })
                popupMenu.show();
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

    private fun callAttendanceApprove() {
        progressDialog = ProgressDialog(this@AttendenceApproveSupervisor)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()

                var attendance: String = ""
                var empid: String = ""

                if (selectall) {
                    attendancelist?.forEach {
                        attendance = attendance + "," + it.attendance_list_id
                        empid = attendance + "," + it.employee_id
                    }
                } else {
                    checkbox.forEach { (key, value) ->
                        if (value) {
                            attendance = attendance + "," + key
                        }
                        println("$key = $value")
                    }
                }

                var approve = AttendancApproveRequest(attendance,empid, userid)
                val webServices2 = WebServices<Any>(this@AttendenceApproveSupervisor)
                webServices2.AttendanceApprove(WebServices.ApiType.attendanceapprove,approve)
            } else {
            }
        }    }

    private fun callListapi() {
        progressDialog = ProgressDialog(this@AttendenceApproveSupervisor)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                val webServices2 = WebServices<Any>(this@AttendenceApproveSupervisor)
                webServices2.attendance_list(WebServices.ApiType.attendancelist, userid)
            } else {
            }
        }
    }

    private fun callListapifilter(conId: String, date2: String, project: String) {
        progressDialog = ProgressDialog(this@AttendenceApproveSupervisor)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                var filterBody = AttendanceFilterRequest(conId,date2,project, userid )
                val webServices2 = WebServices<Any>(this@AttendenceApproveSupervisor)
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

                            if(x.as_status == "1"){
                                completedlist?.add(x)
                            }else{
                                pendinglist?.add(x)
                            }
                        }

                        val adapter = AttendenceApproveListAdaptersup(pendinglist!!,this,getApplicationContext(),false)
                        rv_attendance.adapter = adapter
                        if(pendinglist.isEmpty()){
                            no_data_linear_layout.visibility = View.VISIBLE
                        }else{
                            no_data_linear_layout.visibility = View.GONE
                        }
                    } else {
                        no_data_linear_layout.visibility = View.VISIBLE
                        Toast.makeText(this, "Attendance Approve list is empty", Toast.LENGTH_SHORT)
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
                    if (attendanceres.status == "200" )
                    {
                        Toast.makeText(this, "Attendance Approved successfully ", Toast.LENGTH_SHORT)
                            .show()
                    } else
                    {

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
                        val adapter = AttendenceApproveListAdaptersup(attendancelist!!,this,getApplicationContext(),false)
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
        val webServices = WebServices<Any>(this@AttendenceApproveSupervisor)
        webServices.projects(WebServices.ApiType.projects, userid)

        val webServices4 = WebServices<Any>(this@AttendenceApproveSupervisor)
        webServices4.Conctractors(WebServices.ApiType.contractors, "0")
    }


    override fun onCheckboxChanged(empid_id: String, id: String, isChecked: Boolean) {
        checkbox.put(id,isChecked)
        //empids = empids+","+ empid_id
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

    private fun bottomnavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavShift2)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intentScan = Intent(this@AttendenceApproveSupervisor, ApprovalsActivity::class.java)
                    startActivity(intentScan)
                    finish()
                    true
                }
                R.id.it_scan -> {
                    if(rollid != "1") {
                        val intentScan = Intent(this@AttendenceApproveSupervisor, TrainingAllocationListActivity::class.java)
                        startActivity(intentScan)
                        finish()
                    }
                    true
                }

                R.id.it_worker -> {
                    val intentScan = Intent(this@AttendenceApproveSupervisor, WorkerListActivity::class.java)
                    startActivity(intentScan)
                    finish()

                    true
                }
                else -> false
            }
        }    }

}