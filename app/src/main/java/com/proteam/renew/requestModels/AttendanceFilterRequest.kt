package com.proteam.renew.requestModels

data class AttendanceFilterRequest(
    val contractor_id: String,
    val date: String,
    val project_id: String,
    val user_id: String
)