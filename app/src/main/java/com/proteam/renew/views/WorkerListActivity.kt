package com.proteam.renew.views

import com.proteam.renew.R
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.proteam.renew.Adapter.WorkerListAdapter
import com.proteam.renew.responseModel.workersListResponsce
import com.proteam.renew.responseModel.workersListResponsceItem
import com.proteam.renew.utilitys.OnResponseListener
import com.proteam.renew.utilitys.WebServices


class WorkerListActivity : AppCompatActivity(),OnResponseListener<Any> {
    val iv_Add_NewWorker: ImageView by lazy { findViewById<ImageView>(R.id.iv_Add_NewWorker) }
    var contractorList = ArrayList<workersListResponsceItem>()
    var aaprovedList = ArrayList<workersListResponsceItem>()
    var rejectedList = ArrayList<workersListResponsceItem>()
    var alllist = ArrayList<workersListResponsceItem>()
    var progressDialog: ProgressDialog? = null
    val rv_worker_list: RecyclerView by lazy { findViewById(R.id.rv_worker_list) }
    val tv_add_new: TextView by lazy { findViewById(R.id.tv_add_new) }
    val tv_worker_Count: TextView by lazy { findViewById(R.id.tv_worker_count) }
    val filter: ImageView by lazy { findViewById(R.id.filter) }
    val no_data_linear_layout: LinearLayout by lazy { findViewById(R.id.no_data_linear_layout) }
    var value: Boolean = false
    var userid: String = ""
    var rollid: String = ""
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bottomNavigationView2: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_list)
        val bundle = intent.extras
        if (bundle != null) {
             value = bundle.getBoolean("approval")
        }
        val sharedPreferences: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
         rollid = sharedPreferences.getString("rollid", "")!!
         userid = sharedPreferences.getString("userid", "")!!
        bottomnavigation()

        if(rollid == "2"){
            value = true
            tv_add_new.visibility = View.INVISIBLE
            iv_Add_NewWorker.visibility = View.INVISIBLE
        }else{
            value = false
        }

        rv_worker_list.layoutManager = LinearLayoutManager(this)
        iv_Add_NewWorker.setOnClickListener(View.OnClickListener
        {
            val intent = Intent(this@WorkerListActivity, WorkerInformationActivity::class.java)
            val sharedPreferences1: SharedPreferences =applicationContext.getSharedPreferences("workerPref", Context.MODE_PRIVATE)!!
            val editor1: SharedPreferences.Editor = sharedPreferences1.edit()
            editor1.putBoolean("edit",false)
            editor1.putBoolean("approval",false)
            editor1.putBoolean("imageinsert",true)
            editor1.commit()

            val shared: SharedPreferences =applicationContext.getSharedPreferences("viewsshow", Context.MODE_PRIVATE)!!
            val editor: SharedPreferences.Editor = shared.edit()
            editor.putBoolean("views",true)
            editor.commit()
            startActivity(intent)
        })

        tv_add_new.setOnClickListener {
            val intent = Intent(this@WorkerListActivity, WorkerInformationActivity::class.java)
            val sharedPreferences1: SharedPreferences =applicationContext.getSharedPreferences("workerPref", Context.MODE_PRIVATE)!!
            val editor1: SharedPreferences.Editor = sharedPreferences1.edit()
            editor1.putBoolean("edit",false)
            editor1.putBoolean("approval",false)
            editor1.putBoolean("imageinsert",true)
            editor1.commit()
            val shared: SharedPreferences =applicationContext.getSharedPreferences("viewsshow", Context.MODE_PRIVATE)!!
            val editor: SharedPreferences.Editor = shared.edit()
            editor.putBoolean("views",true)
            editor.commit()
            startActivity(intent)

        }

        filter.setOnClickListener {
            val popupMenu = PopupMenu(this@WorkerListActivity, it)
            popupMenu.getMenuInflater().inflate(R.menu.filter_menu, popupMenu.getMenu())

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem -> // Handle menu item clicks here
                when (menuItem.itemId) {
                    R.id.menu_item -> {
                        tv_worker_Count.setText("Worker List ("+alllist.size+")")
                        val adapter = WorkerListAdapter( alllist, getApplicationContext(),value)
                        rv_worker_list.adapter = adapter
                        if(alllist.size == 0){
                            no_data_linear_layout.visibility = View.VISIBLE
                        }else{
                            no_data_linear_layout.visibility = View.GONE
                        }
                        return@OnMenuItemClickListener true
                    }
                    R.id.menu_item1 -> {
                        tv_worker_Count.setText("Worker List ("+contractorList.size+")")
                        val adapter = WorkerListAdapter( contractorList, getApplicationContext(),value)
                        rv_worker_list.adapter = adapter
                        if(contractorList.size == 0){
                            no_data_linear_layout.visibility = View.VISIBLE
                        }else{
                            no_data_linear_layout.visibility = View.GONE
                        }
                        return@OnMenuItemClickListener true
                    }

                    R.id.menu_item2 -> {
                        tv_worker_Count.setText("Worker List ("+aaprovedList.size+")")

                        val adapter = WorkerListAdapter( aaprovedList, getApplicationContext(),value)
                        rv_worker_list.adapter = adapter
                        if(aaprovedList.size == 0){
                            no_data_linear_layout.visibility = View.VISIBLE
                        }else{
                            no_data_linear_layout.visibility = View.GONE
                        }
                        return@OnMenuItemClickListener true
                    }
                    R.id.menu_item3 -> {
                        tv_worker_Count.setText("Worker List ("+rejectedList.size+")")
                        val adapter = WorkerListAdapter( rejectedList, getApplicationContext(),value)
                        rv_worker_list.adapter = adapter
                        if(rejectedList.size == 0){
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

    override fun onResume() {
        super.onResume()
        callworkerapi()

    }

    private fun bottomnavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavShift)
        bottomNavigationView2 = findViewById(R.id.bottomNavShift2)

        if(rollid == "2"){
            bottomNavigationView.visibility = View.GONE
        }else {
            bottomNavigationView2.visibility = View.GONE
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intentScan = Intent(this@WorkerListActivity, MainActivity::class.java)
                    startActivity(intentScan)
                    finish()
                    true
                }
                R.id.it_scan -> {
                    if(rollid == "1") {
                        val intentScan = Intent(this@WorkerListActivity, ScanIdActivity::class.java)
                        startActivity(intentScan)
                        finish()
                    }
                    true
                }

                R.id.it_worker -> {
                    // Handle the Profile action
                    true
                }
                else -> false
            }
        }

        bottomNavigationView2.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intentScan = Intent(this@WorkerListActivity, ApprovalsActivity::class.java)
                    startActivity(intentScan)
                    finish()
                    true
                }
                R.id.it_scan -> {
                    if(rollid != "1") {
                        val intentScan = Intent(this@WorkerListActivity, TrainingAllocationListActivity::class.java)
                        startActivity(intentScan)
                        finish()
                    }
                    true
                }

                R.id.it_worker -> {
                    // Handle the Profile action
                    true
                }
                else -> false
            }
        }


    }


    private fun callworkerapi() {
        progressDialog = ProgressDialog(this@WorkerListActivity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                val webServices2 = WebServices<Any>(this@WorkerListActivity)
                webServices2.workers(WebServices.ApiType.workers)
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
            WebServices.ApiType.workers -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val workerslist = response as workersListResponsce
                    if (workerslist?.isEmpty() == false) {
                        alllist.clear()
                        contractorList.clear()
                        aaprovedList.clear()
                        for (x in workerslist) {
                            // if(value){
                            if (rollid == "2") {
                            if (x.employer_contractor_name == userid) {
                                alllist.add(x)
                            }
                            if (x.approval_status == "0") {
                                if (x.employer_contractor_name == userid) {
                                    contractorList.add(x)
                                }
                            } else if (x.approval_status == "1") {
                                if (x.employer_contractor_name == userid) {
                                    aaprovedList.add(x)
                                }
                            } else {
                                if (x.employer_contractor_name == userid) {
                                    rejectedList.add(x)
                                }
                            }
                        } else if(rollid == "1") {
                                if (x.subcontractor_name == userid) {
                                    alllist.add(x)
                                }
                                if (x.approval_status == "0") {
                                    if (x.subcontractor_name == userid) {
                                        contractorList.add(x)
                                    }
                                } else if (x.approval_status == "1") {
                                    if (x.subcontractor_name == userid) {
                                        aaprovedList.add(x)
                                    }
                                } else {
                                    if (x.subcontractor_name == userid) {
                                        rejectedList.add(x)
                                    }
                                }
                            }else{
                                alllist.add(x)
                            }
                        }

                        //updateworkerList()
                        val  SharedPreferences =getSharedPreferences("updateImages", Context.MODE_PRIVATE)!!
                        val editor = SharedPreferences.edit()
                        editor.clear()
                        editor.apply()

                        tv_worker_Count.setText("Worker List ("+contractorList.size+")")
                        val adapter = WorkerListAdapter(contractorList, getApplicationContext(),value)
                        rv_worker_list.adapter = adapter

                        if(contractorList.size == 0){
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
                    Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            else -> {}
        }
    }

    /*private fun updateworkerList() {
        val sharedPreferences2: SharedPreferences =getSharedPreferences("updateImages", Context.MODE_PRIVATE)!!
        val a = sharedPreferences2.getString("pic_profile", "")!!
        val b = sharedPreferences2.getString("pic_aadhar", "")!!
        val c = sharedPreferences2.getString("pic_medical", "")!!
        val d = sharedPreferences2.getString("pic_driving", "")!!

        var lastworker = alllist.get(alllist.size-1).id
        var nameemp = alllist.get(alllist.size-1).full_name
        Log.d("lastworkerid",lastworker+"---"+nameemp+"----"+a+"---"+b+"---"+c+"---"+d)

        if(a != ""){
            val webServices2 = WebServices<Any>(this@WorkerListActivity)
            webServices2.file_update(WebServices.ApiType.fileupdate, lastworker,a,"profilepic")
        }

        if(b != ""){
            val webServices2 = WebServices<Any>(this@WorkerListActivity)
            webServices2.file_update(WebServices.ApiType.fileupdate, lastworker,b,"aadhaar_card")
        }

        if(c != ""){
            val webServices2 = WebServices<Any>(this@WorkerListActivity)
            webServices2.file_update(WebServices.ApiType.fileupdate, lastworker,c,"medical_certificate")
        }

        if(d != ""){
            val webServices2 = WebServices<Any>(this@WorkerListActivity)
            webServices2.file_update(WebServices.ApiType.fileupdate, lastworker,d,"driving_lisence_docs")
        }
        //sharedPreferences2.edit().clear()
        //sharedPreferences2.edit().apply()

    }*/
}