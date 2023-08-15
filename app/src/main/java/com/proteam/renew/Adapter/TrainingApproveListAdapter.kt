package com.proteam.renew.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import com.proteam.renew.R
import com.proteam.renew.requestModels.TrainingListResponsce
import com.proteam.renew.requestModels.TrainingListResponsceItem
import com.proteam.renew.responseModel.AttendenceListResponsce
import com.proteam.renew.responseModel.workersListResponsceItem
import com.proteam.renew.utilitys.CheckboxListner
import com.proteam.renew.utilitys.TrainingListner

class TrainingApproveListAdapter(private val mList: List<TrainingListResponsceItem>,
     var applicationContext: Context, private val itemClickListener: TrainingListner): RecyclerView.Adapter<TrainingApproveListAdapter.MyViewHolder>()  {

    private lateinit var qrCodeScanner: IntentIntegrator

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.layout_training_allocation_list,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val trainings = mList[position]
        if(trainings.completion_status == "1"){
            holder.tv_status.setText("Completed")
            holder.tv_status.setBackgroundResource(R.drawable.btn_bg)
            holder.tv_scan.visibility = View.INVISIBLE

        } else {
            holder.tv_status.setText("Pending")
            holder.tv_status.setBackgroundResource(R.drawable.btn_bg_yellow)
        }

        holder.tv_trainer.text = trainings.trainer_name

        var doj = trainings.date_allocation
        var dates = doj?.split("-")
        if(dates?.get(0)?.length == 4){
            doj = dates?.get(2)+"-"+dates.get(1)+"-"+dates.get(0)
        } else {
            doj = dates?.get(0)+"-"+dates?.get(1)+"-"+dates?.get(2)
        }
        holder.tv_date.text = doj

        holder.tv_set_status.setOnClickListener {
            itemClickListener.statuslistner(trainings,"1",it)
        }

        if((position % 2) != 0){
            holder.ll_layout.setBackgroundColor(Color.parseColor("#BFF1D1"))
        }

        holder.ll_layout.setOnClickListener{
            itemClickListener.workerslistlistner(trainings)
        }

        holder.tv_scan.setOnClickListener {
            itemClickListener.traininglisten(trainings)
        }
    }

    class MyViewHolder (view: View): RecyclerView.ViewHolder(view){

        var tv_date : TextView
        var tv_status : TextView
        var tv_trainer : TextView
        var tv_scan : TextView
        var tv_set_status : TextView
        var ll_layout : LinearLayout
        init {
            tv_date = view.findViewById(R.id.tv_date)
            tv_status = view.findViewById(R.id.tv_training_status)
            tv_trainer = view.findViewById(R.id.tv_trainer)
            tv_scan = view.findViewById(R.id.tv_scan)
            ll_layout = view.findViewById(R.id.ll_layout)
            tv_set_status = view.findViewById(R.id.tv_complete)
        }
    }
}