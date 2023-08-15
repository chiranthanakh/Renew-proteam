package com.proteam.renew.responseModel

data class AttendanceCountResponsce(
    val code: String = "" ,
    val present_attendance_count : Int = 0,
    val messages: List<String> ,
    val status: String = ""
)