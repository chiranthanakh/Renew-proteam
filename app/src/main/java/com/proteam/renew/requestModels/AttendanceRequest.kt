package com.proteam.renew.requestModels

import java.util.Calendar

data class AttendanceRequest(
    val activity_id: String,
    val attendance: String,
    val contractor_id: String,
    val date: String,
    val employee_id: String,
    val fromtime: String,
    val project_id: String,
    val totime: String,
    val user_id: String,
    val work_permit: String
)