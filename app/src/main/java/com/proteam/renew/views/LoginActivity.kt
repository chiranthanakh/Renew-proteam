package com.proteam.renew.views

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.proteam.renew.R
import com.proteam.renew.requestModels.Loginmodel
import com.proteam.renew.responseModel.LoginResponse
import com.proteam.renew.utilitys.OnResponseListener
import com.proteam.renew.utilitys.WebServices

class LoginActivity : AppCompatActivity(), View.OnClickListener, OnResponseListener<Any?> {
    var btn_continue: AppCompatButton? = null
    var progressDialog: ProgressDialog? = null
    var edt_email: EditText? = null
    var edt_pass: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPreferences2: SharedPreferences =getSharedPreferences("myPref", Context.MODE_PRIVATE)!!
        val  rollid = sharedPreferences2.getString("rollid", "")!!

        if(rollid != ""){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_continue = findViewById(R.id.btn_continue)
        edt_pass = findViewById(R.id.et_password)
        edt_email = findViewById(R.id.et_user_name)
        btn_continue?.setOnClickListener(this)
    }

    private fun callLoginapi() {
        progressDialog = ProgressDialog(this@LoginActivity)
        if (progressDialog != null) {
            if (!progressDialog!!.isShowing) {
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage("Please wait...")
                progressDialog?.show()
                val loginmodel = Loginmodel(edt_email?.text.toString().trim { it <= ' ' },
                    edt_pass?.text.toString().trim { it <= ' ' })
                val webServices = WebServices<Any>(this@LoginActivity)
                webServices.login(WebServices.ApiType.login, loginmodel)
            } else {
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onResponse(
        response: Any?,
        URL: WebServices.ApiType?,
        isSucces: Boolean,
        code: Int
    ) {
        when (URL) {
            WebServices.ApiType.login -> {
                if (progressDialog != null) {
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }
                if (isSucces && response != null) {
                    val loginResponse = response as LoginResponse
                    if (loginResponse?.status == "200") {
                        val prefs = getSharedPreferences("myPref", MODE_PRIVATE)
                        val editor = prefs.edit()
                        editor.putBoolean("login", true)
                        editor.putString("rollid",loginResponse.role_id)
                        editor.putString("userid",loginResponse.user_id)
                        editor.putString("username",loginResponse.username)

                        editor.commit()
                        if(loginResponse.role_id == "2"){
                            val intent = Intent(this@LoginActivity, ApprovalsActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Check your login credentials ", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            else -> {}
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_continue ->
                if (edt_email!!.text.toString() != "") {
                    if (edt_pass!!.text.toString() != "") {
                        callLoginapi()
                    } else {
                        Toast.makeText(this@LoginActivity, "Enter your password", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Enter your email-id", Toast.LENGTH_LONG)
                        .show()
                }
        }
    }
}