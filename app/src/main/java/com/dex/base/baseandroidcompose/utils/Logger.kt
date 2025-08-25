package com.dex.base.baseandroidcompose.utils

import android.util.Log

object Logger {


    fun d(message: String) {
        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size >= 4) {
            val element = stackTrace[3]
            val className = element.className
            val classItem =
                className.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val justClassName = classItem[classItem.size - 1]
            val methodName = element.methodName
            val lineNumber = element.lineNumber
            val logMessage = "$methodName[$lineNumber] $message"
            Log.d("###Log - $justClassName", logMessage)
        }
    }

    fun b(message: String) {
        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size >= 4) {
            val element = stackTrace[3]
            val className = element.className
            val classItem =
                className.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val justClassName = classItem[classItem.size - 1]
            val methodName = element.methodName
            val lineNumber = element.lineNumber
            val logMessage = "$methodName[$lineNumber] $message"
            Log.d("###Log - $justClassName", logMessage)
        }
    }

    fun w(message: String) {
        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size >= 4) {
            val element = stackTrace[3]
            val className = element.className
            val classItem =
                className.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val justClassName = classItem[classItem.size - 1]
            val methodName = element.methodName
            val lineNumber = element.lineNumber
            val logMessage = "$methodName[$lineNumber] $message"
            Log.w("###Log-$justClassName", logMessage)
        }
    }

    fun e(message: String) {
        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size >= 4) {
            val element = stackTrace[3]
            val className = element.className
            val classItem =
                className.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val justClassName = classItem[classItem.size - 1]
            val methodName = element.methodName
            val lineNumber = element.lineNumber
            val logMessage = "$methodName[$lineNumber] $message"
            Log.e("###LogError-$justClassName", logMessage)
        }
    }
}