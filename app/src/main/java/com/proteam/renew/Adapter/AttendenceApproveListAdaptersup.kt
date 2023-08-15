package com.proteam.renew.Adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proteam.renew.R
import com.proteam.renew.responseModel.Attendance_new_listItem
import com.proteam.renew.utilitys.CheckboxListner
import com.proteam.renew.views.AttendenceApproveActivity

class AttendenceApproveListAdaptersup(
    private val mList: ArrayList<Attendance_new_listItem>,
    private val itemClickListener: CheckboxListner,
    var applicationContext: Context,
    var check: Boolean
): RecyclerView.Adapter<AttendenceApproveListAdaptersup.MyViewHolder>()  {

    var isSelectAll : Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.layout_attendence_approval2,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val attendanceItem = mList[position]

        Log.d("testingcheck2",check.toString())
        holder.tv_sl.text = (position+1).toString()
        if(attendanceItem.as_status == "1"){
            holder.tv_project.setText("Approved")
            holder.tv_project.setBackgroundResource(R.drawable.btn_bg)
        } else {
            holder.tv_project.setText("Pending")
            holder.tv_project.setBackgroundResource(R.drawable.btn_bg_yellow)
        }

        var doj = attendanceItem.date
        var dates = doj?.split("-")
        if(dates?.get(0)?.length == 4){
            doj = dates?.get(2)+"-"+dates.get(1)+"-"+dates.get(0)
        } else {
            doj = dates?.get(0)+"-"+dates?.get(1)+"-"+dates?.get(2)
        }
        holder.tv_date.text = doj
        holder.tv_contractor.text = attendanceItem.username

        if((position % 2) == 0){
            holder.ll_layout.setBackgroundColor(Color.parseColor("#BFF1D1"))
        }

        holder.iv_action_Attendance_approve.setOnClickListener {
            val sharedPref: SharedPreferences =applicationContext.getSharedPreferences("Attendenceid", Context.MODE_PRIVATE)!!
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString("contractorid",attendanceItem.contractor_id)
            editor.putString("projectid",attendanceItem.project_id)
            editor.putString("date",attendanceItem.date)
            editor.commit()
            val intent = Intent(applicationContext, AttendenceApproveActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            applicationContext.startActivity(intent)
        }
    }

    class MyViewHolder (view: View): RecyclerView.ViewHolder(view){

        var tv_sl : TextView
        var tv_project : TextView
        var tv_contractor : TextView
        var tv_date : TextView
        var iv_action_Attendance_approve : ImageView
        var ll_layout : LinearLayout
        init {
            tv_sl=view.findViewById(R.id.tv_sl)
            tv_project = view.findViewById(R.id.tv_project)
            tv_contractor = view.findViewById(R.id.tv_contractor)
            tv_date = view.findViewById(R.id.tv_date)
            iv_action_Attendance_approve = view.findViewById(R.id.iv_action_Attendance_approve)
            ll_layout = view.findViewById(R.id.ll_layout)
        }
    }
}