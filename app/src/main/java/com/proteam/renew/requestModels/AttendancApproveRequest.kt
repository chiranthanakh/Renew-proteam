package com.proteam.renew.requestModels

data class AttendancApproveRequest(
    val attendance_list_id: String,
    val employee_id: String,
    val user_id: String
)