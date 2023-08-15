package com.proteam.renew.views

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.proteam.renew.Adapter.TrainingApproveListAdapter
import com.proteam.renew.Adapter.TrainingWorkerListadaptor
import com.proteam.renew.R
import com.proteam.renew.requestModels.TrainingListResponsceItem
import com.proteam.renew.requestModels.TrainingWorkersrequest
import com.proteam.renew.responseModel.TraininWorkersResponsce
import com.proteam.renew.responseModel.TraininWorkersResponsceItem
import com.proteam.renew.responseModel.generalGesponce
import com.proteam.renew.utilitys.OnResponseListener
import com.proteam.renew.utilitys.WebServices
import java.text.SimpleDateFormat
import java.util.Date

class TrainingWorkerListActivity : AppCompatActivity(), OnResponseListener<Any> {

    var userid: String = ""
    var rollid : String = ""
    var p_id : String = ""
    var m_id : String = ""
    var d_allocation : String = ""

    var progressDialog: ProgressDialog? = null
    val rv_traiing: RecyclerView by lazy { findViewById(R.id.rv_traiing) }
    val iv_filter: ImageView by lazy { findViewById(R.id.filter_training) }
    private lateinit var qrCodeScanner: IntentIntegrator
    var traingAlloc : TrainingListResponsceItem? = null
    private lateinit var bottomNavigationView: BottomNavigationView
    var pendingtrainglist = ArrayList<TrainingListResponsceItem>()
    var completedtrainglist = ArrayList<TrainingListResponsceItem>()
    var alltrainglist = ArrayList<TraininWorkersResponsceItem>()
    val no_data_linear_layout: LinearLayout by lazy { findViewById(R.id.no_data_linear_layout) }
    var to_time : String? = null
    var from_time : String? = null
    //val tv_training_approve: TextView by lazy { findViewById(R.id.tv_training_approve) }
    //val checkbox = HashMap<String,Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_workers)

        val sharedPreferences2: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
        rollid = sharedPreferences2.getString("rollid", "")!!
        userid = sharedPreferences2.getString("userid", "")!!

        val sharedPreferences: SharedPreferences =getSharedPreferences("trainingDetails", Context.MODE_PRIVATE)!!
        p_id = sharedPreferences.getString("p_id", "")!!
        m_id = sharedPreferences.getString("m_id", "")!!
        d_allocation = sharedPreferences.getString("d_allocation", "")!!


        qrCodeScanner = IntentIntegrator(this)
        rv_traiing.layoutManager = LinearLayoutManager(this)
        inilatize()
        bottomnavigation()
        callTraingallocate()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun inilatize() {
        iv_filter.setOnClickListener {
            val popupMenu = PopupMenu(this@TrainingWorkerListActivity, it)
            popupMenu.getMenuInflater().inflate(R.menu.menu_items_training, popupMenu.getMenu())

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem -> // Handle menu item clicks here
                when (menuItem.itemId) {
                    R.id.pending -> {

                        return@OnMenuItemClickListener true
                    }

                    R.id.completed -> {

                        return@OnMenuItemClickListener true
                    }

                    R.id.all -> {

                    }
                }
                false
            })
            popupMenu.show();
        }
    }



    private fun callTraingallocate() {
        progressDialog = ProgressDialog(this@TrainingWorkerListActivity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()

                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val currentDate = sdf.format(Date())

                var alloc =
                    TrainingWorkersrequest(
                        d_allocation,
                        p_id,m_id)
                Log.d("workerlistcheck2", d_allocation+"---"+p_id+"--"+m_id)

                val webServices2 = WebServices<Any>(this@TrainingWorkerListActivity)
                webServices2.cont_training_list(WebServices.ApiType.trainingAllocate, alloc)
            } else {

            }
        }
    }

    private fun bottomnavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavShift2)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intentScan = Intent(this@TrainingWorkerListActivity, MainActivity::class.java)
                    startActivity(intentScan)
                    finish()
                    true
                }
                R.id.it_scan -> {
                    if(rollid != "1") {
                        val intentScan = Intent(this@TrainingWorkerListActivity, ScanIdActivity::class.java)
                        startActivity(intentScan)
                        finish()
                    }
                    true
                }

                R.id.it_worker -> {
                    val intentScan = Intent(this@TrainingWorkerListActivity, WorkerListActivity::class.java)
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

            WebServices.ApiType.trainingAllocate -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces) {
                    val attendanceres = response as TraininWorkersResponsce
                    if (attendanceres.isEmpty() == false ) {
                        for (x in attendanceres) {
                            alltrainglist.add(x)
                        }
                        val adapter = TrainingWorkerListadaptor(alltrainglist,getApplicationContext())
                        rv_traiing.adapter = adapter
                    } else {
                        Toast.makeText(this, "No worker added", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            else -> {}
        }
    }
}