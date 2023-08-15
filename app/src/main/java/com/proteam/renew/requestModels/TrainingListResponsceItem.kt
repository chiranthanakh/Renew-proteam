package com.proteam.renew.requestModels

data class TrainingListResponsceItem(
    val completion_status: String,
    val date_allocation: String,
    val date_of_completion: String,
    val project_id: String,
    val project_name: String,
    val project_type: String,
    val trainer_email: String,
    val trainer_name: String,
    val training_allocation_id: String,
    val training_id: String,
    val training_master_id: String,
    val training_name: String
)