package com.proteam.renew.views

import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.proteam.renew.Adapter.TrainingApproveListAdapter
import com.proteam.renew.R
import com.proteam.renew.requestModels.CompletionRequest
import com.proteam.renew.requestModels.TrainingAllocationRequest
import com.proteam.renew.requestModels.TrainingListResponsce
import com.proteam.renew.requestModels.TrainingListResponsceItem
import com.proteam.renew.requestModels.TrainingWorkersrequest
import com.proteam.renew.responseModel.Generalresponsce
import com.proteam.renew.responseModel.generalGesponce
import com.proteam.renew.utilitys.OnResponseListener
import com.proteam.renew.utilitys.TrainingListner
import com.proteam.renew.utilitys.WebServices
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class TrainingAllocationListActivity : AppCompatActivity(), OnResponseListener<Any>,TrainingListner {

    var userid: String = ""
    var rollid : String = ""
    var progressDialog: ProgressDialog? = null
    val rv_traiing: RecyclerView by lazy { findViewById(R.id.rv_traiing) }
    val iv_filter: ImageView by lazy { findViewById(R.id.filter_training) }
    private lateinit var qrCodeScanner: IntentIntegrator
    var traingAlloc : TrainingListResponsceItem? = null
    private lateinit var bottomNavigationView: BottomNavigationView
    var pendingtrainglist = ArrayList<TrainingListResponsceItem>()
    var completedtrainglist = ArrayList<TrainingListResponsceItem>()
    var alltrainglist = ArrayList<TrainingListResponsceItem>()
    val no_data_linear_layout: LinearLayout by lazy { findViewById(R.id.no_data_linear_layout) }
    var to_time : String? = null
    var from_time : String? = null
    //val tv_training_approve: TextView by lazy { findViewById(R.id.tv_training_approve) }
    //val checkbox = HashMap<String,Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_allocation_list)

        val sharedPreferences2: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
        rollid = sharedPreferences2.getString("rollid", "")!!
        userid = sharedPreferences2.getString("userid", "")!!
        qrCodeScanner = IntentIntegrator(this)
        rv_traiing.layoutManager = LinearLayoutManager(this)
        inilatize()
        bottomnavigation()
    }

    override fun onResume() {
        super.onResume()
        callListapi()
    }

    private fun inilatize() {
        iv_filter.setOnClickListener {
            val popupMenu = PopupMenu(this@TrainingAllocationListActivity, it)
            popupMenu.getMenuInflater().inflate(R.menu.menu_items_training, popupMenu.getMenu())

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem -> // Handle menu item clicks here
                when (menuItem.itemId) {
                    R.id.pending -> {
                        val adapter = TrainingApproveListAdapter(pendingtrainglist,getApplicationContext(),this)
                        rv_traiing.adapter = adapter
                        if(pendingtrainglist.size == 0){
                            no_data_linear_layout.visibility = View.VISIBLE
                        }else{
                            no_data_linear_layout.visibility = View.GONE
                        }
                        return@OnMenuItemClickListener true
                    }

                    R.id.completed -> {
                        val adapter = TrainingApproveListAdapter(completedtrainglist,getApplicationContext(),this)
                        rv_traiing.adapter = adapter
                        if(completedtrainglist.size == 0){
                            no_data_linear_layout.visibility = View.VISIBLE
                        }else{
                            no_data_linear_layout.visibility = View.GONE
                        }
                        return@OnMenuItemClickListener true
                    }

                    R.id.all -> {
                        val adapter = TrainingApproveListAdapter(alltrainglist,getApplicationContext(),this)
                        rv_traiing.adapter = adapter
                        if(alltrainglist.size == 0){
                            no_data_linear_layout.visibility = View.VISIBLE
                        }else{
                            no_data_linear_layout.visibility = View.GONE
                        }
                        return@OnMenuItemClickListener true
                    }
                }
                false
            })
            popupMenu.show();
        }
    }

    private fun callListapi() {
        progressDialog = ProgressDialog(this@TrainingAllocationListActivity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                val webServices2 = WebServices<Any>(this@TrainingAllocationListActivity)
                webServices2.Traininglist(WebServices.ApiType.traininglist, userid)
            } else {

            }
        }
    }

    private fun callTraingallocate(scannedData: String) {
        progressDialog = ProgressDialog(this@TrainingAllocationListActivity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
               // progressDialog?.show()

                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val currentDate = sdf.format(Date())

                val sharedPreferences: SharedPreferences =getSharedPreferences("trainingtime", Context.MODE_PRIVATE)!!
                var from = sharedPreferences.getString("startTime", "")!!
                var to = sharedPreferences.getString("endTime", "")!!
                var id = sharedPreferences.getString("trainingid", "")!!
                var alloc = traingAlloc?.let {
                    TrainingAllocationRequest(
                        currentDate,
                        "",it.date_allocation,scannedData,from,it.project_id,
                        it.project_type, to,it.training_master_id,userid)
                }
                val webServices2 = WebServices<Any>(this@TrainingAllocationListActivity)
                webServices2.trainingAllocation(WebServices.ApiType.trainingAllocate, alloc)
            } else {

            }
        }
    }

    private fun bottomnavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavShift2)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intentScan = Intent(this@TrainingAllocationListActivity, MainActivity::class.java)
                    startActivity(intentScan)
                    finish()
                    true
                }
                R.id.it_scan -> {
                    /*if(rollid != "1") {
                        val intentScan = Intent(this@TrainingAllocationListActivity, ScanIdActivity::class.java)
                        startActivity(intentScan)
                        finish()
                    }*/
                    true
                }

                R.id.it_worker -> {
                    val intentScan = Intent(this@TrainingAllocationListActivity, WorkerListActivity::class.java)
                    startActivity(intentScan)
                    finish()
                    true
                }
                else -> false
            }
        }
    }


    override fun onResponse(
        response: Any?,
        URL: WebServices.ApiType?,
        isSucces: Boolean,
        code: Int
    ) {
        when(URL){
            WebServices.ApiType.traininglist -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val traininglist = response as TrainingListResponsce
                    if (traininglist?.isEmpty() == false) {
                        pendingtrainglist.clear()
                        completedtrainglist.clear()
                        alltrainglist.clear()
                        for (x in traininglist) {
                            if(x.completion_status == "1"){
                                completedtrainglist.add(x)
                            } else {
                                pendingtrainglist.add(x)
                            }
                            alltrainglist.add(x)
                        }
                        val adapter = TrainingApproveListAdapter(pendingtrainglist,getApplicationContext(),this)
                        rv_traiing.adapter = adapter
                        Log.d("pendingListSize",pendingtrainglist.size.toString())
                        if(pendingtrainglist.size == 0){
                            no_data_linear_layout.visibility = View.VISIBLE
                        }else{
                            no_data_linear_layout.visibility = View.GONE
                        }
                    } else {
                        if(pendingtrainglist.size == 0){
                            no_data_linear_layout.visibility = View.VISIBLE
                        }else{
                            no_data_linear_layout.visibility = View.GONE
                        }
                        Toast.makeText(this, "Training allocation list is empty", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            WebServices.ApiType.complete -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    var attendanceres = response as Generalresponsce
                    if (attendanceres.status == 200 ) {
                        Toast.makeText(this, "Training Completed Successfully", Toast.LENGTH_SHORT)
                            .show()
                        callListapi()

                    } else if(attendanceres.status == 400) {
                        Toast.makeText(this, attendanceres.messages.get(0), Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            WebServices.ApiType.trainingAllocate -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val attendanceres = response as generalGesponce
                    if (attendanceres.status == "200" ) {
                        scanQRCode()
                        Toast.makeText(this, "Training added Successfully", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this, "Something Went wrong.", Toast.LENGTH_SHORT)
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

    fun scanQRCode() {
        qrCodeScanner.setOrientationLocked(false)
        qrCodeScanner.createScanIntent()
        qrCodeScanner.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents != null) {
                    val scannedData = result.contents
                    callTraingallocate(scannedData)
                } else {

                }
            }
    }


    override fun traininglisten(trainginfo: TrainingListResponsceItem) {
        val sharedPreferences: SharedPreferences =getSharedPreferences("trainingtime", Context.MODE_PRIVATE)!!
        var from = sharedPreferences.getString("startTime", "")!!
        var to = sharedPreferences.getString("endTime", "")!!
        var id = sharedPreferences.getString("trainingid", "")!!

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())

        Log.d("trainingAllocation566", id+"----"+trainginfo.training_id+"----"+currentDate+"----"+trainginfo.date_of_completion)


        if(currentDate == trainginfo.date_allocation){
            if(id == trainginfo.training_id){
                scanQRCode()
            } else {
                showTraingTime(trainginfo)
            }
            traingAlloc = trainginfo
        } else {
            Toast.makeText(this, "Training not allocate for today", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun statuslistner(position: TrainingListResponsceItem, state: String, its: View) {
        callstatusapi(position)
    }

    override fun workerslistlistner(position: TrainingListResponsceItem) {
        calltraingwarkers(position)
    }

    private fun calltraingwarkers(position: TrainingListResponsceItem) {

        val prefs = getSharedPreferences("trainingDetails", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("p_id",position.project_id)
        editor.putString("m_id",position.training_master_id)
        editor.putString("d_allocation",position.date_allocation)
        editor.commit()
        Log.d("workerlistcheck", position.project_id+"---"+position.training_master_id+"--"+position.date_allocation)

        val intentScan = Intent(this@TrainingAllocationListActivity, TrainingWorkerListActivity::class.java)
        startActivity(intentScan)
    }

    private fun callstatusapi(position: TrainingListResponsceItem) {
        val dialogView = Dialog(this)
        dialogView.setContentView(R.layout.status_dialog)
        dialogView.setCancelable(false)
        var status = ArrayList<String>()
        status.add("Completed")
        status.add("Pending")

        var tv_submit: TextView = dialogView.findViewById(R.id.tv_submit)
        var tv_cancel: TextView = dialogView.findViewById(R.id.tv_cancel)
        val sp_skill_type: AutoCompleteTextView by lazy { dialogView.findViewById(R.id.sp_training_status) }
        val adapter3 = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, status)
        sp_skill_type.setAdapter(adapter3)

        tv_submit.setOnClickListener {
            if(!TextUtils.isEmpty(sp_skill_type.text.toString())){
                if(sp_skill_type.text.toString() == "Completed"){
                    callcompleteapi("1",position)
                } else {
                    callcompleteapi("0", position)
                }
            } else {
                Toast.makeText(this, "Please Select Status", Toast.LENGTH_SHORT)
                    .show()
            }
            dialogView.dismiss()
        }
        tv_cancel.setOnClickListener {
            dialogView.dismiss()
        }
        dialogView.show()
    }

    private fun callcompleteapi(s: String, position: TrainingListResponsceItem) {
        progressDialog = ProgressDialog(this@TrainingAllocationListActivity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                var alloc =
                    CompletionRequest(
                        position.project_id,
                        position.training_master_id,position.date_allocation,s)


                val webServices2 = WebServices<Any>(this@TrainingAllocationListActivity)
                webServices2.Trainingcompletion(WebServices.ApiType.complete, alloc)
            } else {
            }
        }
    }


    fun showTraingTime(trainginfo: TrainingListResponsceItem) {
        val dialogView = Dialog(this)
        dialogView.setContentView(R.layout.time_picker_dialog)
        dialogView.setCancelable(false)
        val etChooseTime_from: EditText = dialogView.findViewById(R.id.etChooseTime_from)
        val etChooseTime_to: EditText = dialogView.findViewById(R.id.etChooseTime_to)
        var tv_submit: TextView = dialogView.findViewById(R.id.tv_submit)
        var tv_cancel: TextView = dialogView.findViewById(R.id.tv_cancel)

        var timePickerDialogfrom: TimePickerDialog? = null
        var timePickerDialogto: TimePickerDialog? = null
        var calendar: Calendar? = null
        var currentHour = 0
        var currentMinute = 0
        var amPm: String? = null

        etChooseTime_from?.setOnClickListener(View.OnClickListener {
            calendar = Calendar.getInstance()
            currentHour = calendar?.get(Calendar.HOUR_OF_DAY)!!
            currentMinute = calendar?.get(Calendar.MINUTE)!!
            timePickerDialogfrom =
                TimePickerDialog(this@TrainingAllocationListActivity, { timePicker, hourOfDay, minutes ->
                    amPm = if (hourOfDay >= 12) {
                        "PM"
                    } else {
                        "AM"
                    }
                    from_time = String.format("%02d:%02d", hourOfDay, minutes) + amPm
                    etChooseTime_from?.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm)
                }, currentHour, currentMinute, false)
            timePickerDialogfrom!!.show()
        })
        etChooseTime_to?.setOnClickListener(View.OnClickListener {
            calendar = Calendar.getInstance()
            currentHour = calendar?.get(Calendar.HOUR_OF_DAY)!!
            currentMinute = calendar?.get(Calendar.MINUTE)!!
            timePickerDialogto =
                TimePickerDialog(this@TrainingAllocationListActivity, { timePicker, hourOfDay, minutes ->
                    amPm = if (hourOfDay >= 12) {
                        "PM"
                    } else {
                        "AM"
                    }
                    to_time = String.format("%02d:%02d", hourOfDay, minutes) + amPm
                    etChooseTime_to?.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm)
                }, currentHour, currentMinute, false)
            timePickerDialogto!!.show()
        })

        tv_submit.setOnClickListener {
            if (!TextUtils.isEmpty(etChooseTime_from?.text.toString())){
                if(!TextUtils.isEmpty(etChooseTime_to?.text.toString())){
                val prefs = getSharedPreferences("trainingtime", MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putString("trainingid",trainginfo.training_id)
                editor.putString("startTime",etChooseTime_from.text.toString())
                editor.putString("endTime",etChooseTime_to.text.toString())
                editor.commit()
                dialogView.dismiss()
                scanQRCode()
                } else {
                    Toast.makeText(this, "Please Select from Time", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Please Select To Time", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        tv_cancel.setOnClickListener {
            dialogView.dismiss()
        }
        dialogView.show()
    }
}