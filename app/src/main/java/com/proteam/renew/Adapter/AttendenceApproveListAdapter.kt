package com.proteam.renew.Adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proteam.renew.R
import com.proteam.renew.responseModel.Attendance_new_list
import com.proteam.renew.utilitys.CheckboxListner

class AttendenceApproveListAdapter(
    private val mList: Attendance_new_list,
    private val itemClickListener: CheckboxListner,
    var applicationContext: Context,
    var check: Boolean
): RecyclerView.Adapter<AttendenceApproveListAdapter.MyViewHolder>()  {

    var isSelectAll : Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.layout_attendence_approval,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val attendanceItem = mList[position]
        if(check == true){
            Log.d("testingcheck1",check.toString())
            holder.ch_approve.isChecked = true
        }else{
            holder.ch_approve.isChecked = false
        }
        Log.d("testingcheck2",check.toString())

        holder.tv_worker.text = attendanceItem.full_name
        holder.tv_contractor.text = attendanceItem.username
        holder.tv_attendance.text = attendanceItem.attendance

        if((position % 2) == 0){
            holder.ll_layout.setBackgroundColor(Color.parseColor("#BFF1D1"))
        }

        if(attendanceItem.as_status == "1"){
            holder.ch_approve.setChecked(true)
            holder.iv_action_Attendance_approve.setBackgroundResource(R.drawable.ic_status2)
            holder.ch_approve.isEnabled = false
        } else {
            holder.iv_action_Attendance_approve.setBackgroundResource(R.drawable.ic_status)
        }

        holder.ch_approve.setOnCheckedChangeListener { _, isChecked ->
            itemClickListener.onCheckboxChanged(attendanceItem.employee_id, attendanceItem.attendance_list_id, isChecked)
        }
    }

    class MyViewHolder (view: View): RecyclerView.ViewHolder(view){
        var ch_approve : CheckBox
        var tv_worker : TextView
        var tv_contractor : TextView
        var tv_attendance : TextView
        var iv_action_Attendance_approve : ImageView
        var ll_layout : LinearLayout
        init {
            ch_approve=view.findViewById(R.id.ch_approve)
            tv_worker = view.findViewById(R.id.tv_worker)
            tv_contractor = view.findViewById(R.id.tv_contractor)
            tv_attendance = view.findViewById(R.id.tv_attendance)
            iv_action_Attendance_approve = view.findViewById(R.id.iv_action_Attendance_approve)
            ll_layout = view.findViewById(R.id.ll_layout)
        }
    }
}