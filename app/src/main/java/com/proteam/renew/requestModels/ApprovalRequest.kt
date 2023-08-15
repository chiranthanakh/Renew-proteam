package com.proteam.renew.requestModels

data class ApprovalRequest(
    val employee_id: String,
    val induction_date: String,
    val induction_status: String,
    val report_is_ok: String,
    val user_id: String
)