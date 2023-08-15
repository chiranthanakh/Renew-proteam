package com.proteam.renew.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.proteam.renew.R
import java.util.Locale

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var ll_add_newWorker: LinearLayout? = null
    var ll_id_scan: LinearLayout? = null
    var ll_approvals: LinearLayout? = null
    var iv_logout: ImageView? = null
    var userid: String = ""
    var rollid : String = ""
    var name: TextView? = null
    private lateinit var bottomNavigationView: BottomNavigationView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val sharedPreferences2: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
            rollid = sharedPreferences2.getString("rollid", "")!!
            userid = sharedPreferences2.getString("userid", "")!!
            var username = sharedPreferences2.getString("username", "")!!

        checkStoragePermission()

        if(rollid == "2"){
            val intent = Intent(this@MainActivity, ApprovalsActivity::class.java)
            startActivity(intent)
            finish()
        }

        if(userid == ""){
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        ll_add_newWorker = findViewById(R.id.ll_add_newWorker)
        ll_add_newWorker?.setOnClickListener(this)
        ll_id_scan = findViewById(R.id.ll_id_scan)
        ll_id_scan?.setOnClickListener(this)
        ll_approvals = findViewById(R.id.ll_approvals)
        ll_approvals?.setOnClickListener(this)
        iv_logout = findViewById(R.id.iv_logout)
        name = findViewById(R.id.tv_name)
        name?.setText("Hi "+username)


        if(rollid == "1"){
            ll_approvals?.visibility = View.GONE
        } else if(rollid == "2"){
            ll_id_scan?.visibility = View.GONE
            ll_add_newWorker?.visibility = View.GONE
        }
        iv_logout?.setOnClickListener {

//            val sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
//            val editor = sharedPreferences.edit()
//            editor.clear()
//            editor.apply()
//            val intent = Intent(this@MainActivity, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
            LogOutAlertDilog()
        }

        bottomnavigation()

    }

    private fun bottomnavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavShift)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    if(rollid != "1"){

                    }
                    true
                }
                R.id.it_scan -> {
                    if(rollid == "1") {
                        val intentScan = Intent(this@MainActivity, ScanIdActivity::class.java)
                        startActivity(intentScan)
                    }
                    true
                }

                R.id.it_worker -> {
                    if(rollid == "1") {
                        val intentScan = Intent(this@MainActivity, WorkerListActivity::class.java)
                        startActivity(intentScan)
                        finish()
                    }
                    true
                }
                else -> false
            }
        }    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ll_add_newWorker -> {
                val intent = Intent(this@MainActivity, WorkerListActivity::class.java)
                val prefs = getSharedPreferences("onboard", MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putBoolean("nav",false)
                editor.commit()
                startActivity(intent)
            }

            R.id.ll_id_scan -> {
                val intentScan = Intent(this@MainActivity, ScanIdActivity::class.java)
                startActivity(intentScan)
            }

            R.id.ll_approvals -> {
                val intentapprovals = Intent(this@MainActivity, ApprovalsActivity::class.java)
                val prefs = getSharedPreferences("onboard", MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putBoolean("nav",true)
                editor.commit()
                startActivity(intentapprovals)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            501 -> if (grantResults.size > 0 && permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                // check whether storage permission granted or not.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }else{
                   // openAppSettings()
                   // Toast.makeText(this, "Please enable Storage permission", Toast.LENGTH_SHORT)
                    //    .show()
                }
            }

            else -> {}
        }
    }

    private fun openAppSettings() {
        val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        appSettingsIntent.data = Uri.fromParts("package", packageName, null)
        startActivity(appSettingsIntent)
    }

    private fun checkCameraPermission() {
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
        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf<String>(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 501
            )
        } else {
            checkCameraPermission()
            //openGallery()
        }
    }

    private fun LogOutAlertDilog() {

        val alertDialog = AlertDialog.Builder(this@MainActivity)
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert)
        alertDialog.setTitle("Logout")
        alertDialog.setMessage("Do you really want to logout ?")
        alertDialog.setPositiveButton("YES")
        { dialog, which ->

            val sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
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