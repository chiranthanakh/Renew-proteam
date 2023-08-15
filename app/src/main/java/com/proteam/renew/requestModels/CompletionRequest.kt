package com.proteam.renew.requestModels

import java.io.File

data class CompletionRequest(
    val project_id: String,
    val training_master_id: String,
    val date_allocation: String,
    val completion_status: String

)
