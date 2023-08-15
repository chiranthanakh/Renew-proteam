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

class ContractorAttendenceListAdaptor(
    private val mList: Attendance_new_list,
    private val itemClickListener: CheckboxListner,
    var applicationContext: Context,
    var check: Boolean
): RecyclerView.Adapter<ContractorAttendenceListAdaptor.MyViewHolder>()  {

    var isSelectAll : Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.layout_attendence_contractor,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val attendanceItem = mList[position]
        Log.d("testingcheck2",check.toString())
        holder.tv_worker.text = attendanceItem.full_name
        holder.tv_contractor.text = attendanceItem.date
        holder.tv_attendance.text = attendanceItem.attendance

        var dates = attendanceItem.date?.split("-")
        if(dates?.get(0)?.length == 4){
            holder.tv_contractor.text = dates?.get(2)+"-"+dates.get(1)+"-"+dates.get(0)
        } else {
            holder.tv_contractor.text = dates?.get(0)+"-"+dates?.get(1)+"-"+dates?.get(2)
        }

        if((position % 2) == 0){
            holder.ll_layout.setBackgroundColor(Color.parseColor("#BFF1D1"))
        }

        if(attendanceItem.as_status == "1"){
            holder.iv_action_Attendance_approve.setBackgroundResource(R.drawable.ic_status2)
        } else {
            holder.iv_action_Attendance_approve.setBackgroundResource(R.drawable.ic_status)
        }
    }

    class MyViewHolder (view: View): RecyclerView.ViewHolder(view){
        var tv_worker : TextView
        var tv_contractor : TextView
        var tv_attendance : TextView
        var iv_action_Attendance_approve : ImageView
        var ll_layout : LinearLayout
        init {
            tv_worker = view.findViewById(R.id.tv_worker)
            tv_contractor = view.findViewById(R.id.tv_contractor)
            tv_attendance = view.findViewById(R.id.tv_attendance)
            iv_action_Attendance_approve = view.findViewById(R.id.iv_action_Attendance_approve)
            ll_layout = view.findViewById(R.id.ll_layout)
        }
    }
}