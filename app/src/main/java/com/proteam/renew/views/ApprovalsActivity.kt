package com.proteam.renew.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.proteam.renew.R

class ApprovalsActivity : AppCompatActivity(), View.OnClickListener {
    var ll_OnBoard_approval: LinearLayout? = null
    var ll_training_approval: LinearLayout? = null
    var ll_attendance_approval: LinearLayout? = null
    var userid: String = ""
    var rollid : String = ""
    var iv_logout: ImageView? = null
    var name: TextView? = null

    private lateinit var bottomNavigationView: BottomNavigationView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approvals)
        ll_OnBoard_approval = findViewById(R.id.ll_OnBoard_approval)
        ll_OnBoard_approval?.setOnClickListener(this)
        ll_attendance_approval = findViewById(R.id.ll_attendance_approval)
        ll_attendance_approval?.setOnClickListener(this)
        ll_training_approval = findViewById(R.id.ll_training_approval)
        ll_training_approval?.setOnClickListener(this)
        iv_logout = findViewById(R.id.iv_logout)
        name = findViewById(R.id.tv_name)

        val sharedPreferences2: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
        rollid = sharedPreferences2.getString("rollid", "")!!
        userid = sharedPreferences2.getString("userid", "")!!
        var username = sharedPreferences2.getString("username", "")!!
        name?.setText("Hi "+username)



        bottomnavigation()

        iv_logout?.setOnClickListener {
//            val sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
//            val editor = sharedPreferences.edit()
//            editor.clear()
//            editor.apply()
//            val intent = Intent(this@ApprovalsActivity, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
            LogOutAlertDilog()
        }
    }



    private fun bottomnavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavShift2)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {

                    /*    val intentScan = Intent(this@ApprovalsActivity, MainActivity::class.java)
                        startActivity(intentScan)
                        finish()*/

                    true
                }
                R.id.it_scan -> {
                    if(rollid != "1") {
                        val intentScan = Intent(this@ApprovalsActivity, TrainingAllocationListActivity::class.java)
                        startActivity(intentScan)
                        finish()
                    }
                    true
                }
                R.id.it_worker -> {
                    if(rollid != "1") {
                        val intentScan = Intent(this@ApprovalsActivity, WorkerListActivity::class.java)
                        startActivity(intentScan)
                        finish()
                    }
                    true
                }
                else -> false
            }
        }    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.ll_OnBoard_approval -> {
                val intent = Intent(this@ApprovalsActivity, WorkerListActivity::class.java)
                val bundle = Bundle()
                bundle.putBoolean("approval", true)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            R.id.ll_attendance_approval -> {
                val intentAttendence =
                    Intent(this@ApprovalsActivity, AttendenceApproveSupervisor::class.java)
                startActivity(intentAttendence)
            }

            R.id.ll_training_approval -> {
                val intentAttendence =
                    Intent(this@ApprovalsActivity, TrainingAllocationListActivity::class.java)
                startActivity(intentAttendence)
            }
        }
    }


    private fun LogOutAlertDilog() {

        val alertDialog = AlertDialog.Builder(this@ApprovalsActivity)
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert)
        alertDialog.setTitle("Logout")
        alertDialog.setMessage("Do you really want to logout ?")
        alertDialog.setPositiveButton("YES")
        { dialog, which ->

            val sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(this@ApprovalsActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        alertDialog.setNegativeButton("NO")
        { dialog, which -> // Write your code here to invoke NO event
            dialog.cancel()
        }
        // Showing Alert Message
        alertDialog.show()
    }
}