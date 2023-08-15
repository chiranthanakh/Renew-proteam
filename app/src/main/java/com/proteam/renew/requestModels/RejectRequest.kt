package com.proteam.renew.requestModels

data class RejectRequest(
    val employee_id: String,
    val remarks: String,
    val user_id: String
)