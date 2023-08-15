package com.proteam.renew.requestModels

data class TrainingAllocationRequest(
    val completion_date: String,
    val completion_status: String,
    val date_allocation: String,
    val employee_id: String,
    val from_time: String,
    val project_id: String,
    val project_type: String,
    val to_time: String,
    val training_master_id: String,
    val user_id: String
)