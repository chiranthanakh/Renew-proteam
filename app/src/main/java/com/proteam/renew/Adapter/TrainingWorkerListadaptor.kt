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
import com.proteam.renew.responseModel.TraininWorkersResponsceItem
import com.proteam.renew.responseModel.workersListResponsceItem
import com.proteam.renew.utilitys.CheckboxListner
import com.proteam.renew.utilitys.TrainingListner

class TrainingWorkerListadaptor(private val mList: List<TraininWorkersResponsceItem>,
                                var applicationContext: Context): RecyclerView.Adapter<TrainingWorkerListadaptor.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.layout_training_workers_list,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val trainings = mList[position]
        if((position % 2) != 0){
            holder.ll_layout.setBackgroundColor(Color.parseColor("#BFF1D1"))
        }
        holder.tv_contractor.text = trainings.username
        holder.tv_Worker.text = trainings.full_name
        if(trainings.completion_status == "2"){
            holder.tv_status.visibility = View.INVISIBLE
        }
        var doj = trainings.date_of_completion
        if(doj != "") {
            var dates = doj?.split("-")
            if (dates?.get(0)?.length == 4) {
                doj = dates?.get(2) + "-" + dates.get(1) + "-" + dates.get(0)
            } else {
                doj = dates?.get(0) + "-" + dates?.get(1) + "-" + dates?.get(2)
            }
            holder.tv_date.text = doj
        } else {
            holder.tv_date.text = "--"
        }
    }

    class MyViewHolder (view: View): RecyclerView.ViewHolder(view){

        var tv_date : TextView
        var tv_Worker : TextView
        var tv_status : ImageView
        var ll_layout : LinearLayout
        var tv_contractor : TextView
        init {
            tv_date = view.findViewById(R.id.tv_date)
            tv_Worker = view.findViewById(R.id.tv_training_status)
            tv_status = view.findViewById(R.id.tv_status)
            ll_layout = view.findViewById(R.id.ll_layout)
            tv_contractor = view.findViewById(R.id.tv_contractor)

        }
    }
}