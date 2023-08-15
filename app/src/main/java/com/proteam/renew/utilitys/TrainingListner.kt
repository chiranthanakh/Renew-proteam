package com.proteam.renew.utilitys

import android.view.View
import com.proteam.renew.requestModels.TrainingListResponsce
import com.proteam.renew.requestModels.TrainingListResponsceItem

interface TrainingListner {
   fun traininglisten(position: TrainingListResponsceItem)
   fun statuslistner(position: TrainingListResponsceItem, state: String, its: View)
   fun workerslistlistner(position: TrainingListResponsceItem)


}