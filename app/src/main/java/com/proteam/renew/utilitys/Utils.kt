package com.proteam.renew.utilitys

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


class Utils {

    fun parseDateString(dateString: String, format: String): Date {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.parse(dateString) ?: Date()
    }

    fun getDifferenceInDays(startDate: Date, endDate: Date): Long {
        val differenceInMillis = endDate.time - startDate.time
        return TimeUnit.MILLISECONDS.toDays(differenceInMillis)
    }

    fun findMonthDifference(startDateStr: String, endDateStr: String): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val startDate = LocalDate.parse(startDateStr, formatter)
        val endDate = LocalDate.parse(endDateStr, formatter)

        if(startDate.isAfter(endDate)){
            return 22//ChronoUnit.MONTHS.between(startDate, endDate)

        }else {
            return ChronoUnit.MONTHS.between(startDate, endDate)

        }
    }



    fun getPath(context: Context, uri: Uri?): String? {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri!!, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(column_index)
            }
            cursor.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }

    fun getFileName(prefix: String, date: Date?, sufix: String): String? {
        val dateFormatter = SimpleDateFormat("yyMMddHHmmss", Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return if (prefix.isEmpty() && sufix.isEmpty()) {
            dateFormatter.format(date)
        } else if (prefix.isEmpty()) {
            dateFormatter.format(date) + "_" + sufix
        } else if (sufix.isEmpty()) {
            prefix + "_" + dateFormatter.format(date)
        } else {
            prefix + "_" + dateFormatter.format(date) + "_" + sufix
        }
    }
}