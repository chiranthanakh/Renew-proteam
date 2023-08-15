package com.proteam.renew.responseModel

data class TraininWorkersResponsceItem(
    val allocation_status: String,
    val allocation_type: String,
    val completion_status: String,
    val contractor_id: String,
    val created_on: String,
    val date_allocation: String,
    val date_of_completion: String,
    val employee_id: String,
    val from_time: String,
    val project_id: String,
    val project_type: String,
    val skill_set_id: String,
    val to_time: String,
    val trainer_email: String,
    val trainer_name: String,
    val training_allocation_id: String,
    val training_id: String,
    val training_master_id: String,
    val update_on: String,
    val user_id: String,
    val full_name: String,
    val username: String
)