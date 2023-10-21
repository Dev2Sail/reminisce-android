package studio.hcmc.reminisce.util

import android.util.Log

object LocalLogger {
    private class LoggerException : Exception {
        constructor(): super("Log stack trace")

        constructor(throwable: Throwable): super("Log stack trace", throwable)
    }

    fun v(message: String) {
        Log.v("Reminisce", message, LoggerException())
    }

    fun v(message: String, throwable: Throwable) {
        Log.v("Reminisce", message, LoggerException(throwable))
    }

    fun v(throwable: Throwable) {
        Log.v("Reminisce", throwable.message ?: "<no message>", LoggerException(throwable))
    }

    fun d(message: String) {
        Log.d("Reminisce", message, LoggerException())
    }

    fun d(message: String, throwable: Throwable) {
        Log.d("Reminisce", message, LoggerException(throwable))
    }

    fun d(throwable: Throwable) {
        Log.d("Reminisce", throwable.message ?: "<no message>", LoggerException(throwable))
    }

    fun i(message: String) {
        Log.i("Reminisce", message, LoggerException())
    }

    fun i(message: String, throwable: Throwable) {
        Log.i("Reminisce", message, LoggerException(throwable))
    }

    fun i(throwable: Throwable) {
        Log.i("Reminisce", throwable.message ?: "<no message>", LoggerException(throwable))
    }

    fun w(message: String) {
        Log.w("Reminisce", message, LoggerException())
    }

    fun w(message: String, throwable: Throwable) {
        Log.w("Reminisce", message, LoggerException(throwable))
    }

    fun w(throwable: Throwable) {
        Log.w("Reminisce", throwable.message ?: "<no message>", LoggerException(throwable))
    }

    fun e(message: String) {
        Log.e("Reminisce", message, LoggerException())
    }

    fun e(message: String, throwable: Throwable) {
        Log.e("Reminisce", message, LoggerException(throwable))
    }

    fun e(throwable: Throwable) {
        Log.e("Reminisce", throwable.message ?: "<no message>", LoggerException(throwable))
    }

    fun wtf(message: String) {
        // What a Terrible Failure
        Log.wtf("Reminisce", message, LoggerException())
    }

    fun wtf(message: String, throwable: Throwable) {
        Log.wtf("Reminisce", message, LoggerException(throwable))
    }

    fun wtf(throwable: Throwable) {
        Log.wtf("Reminisce", throwable.message ?: "<no message>", LoggerException(throwable))
    }
}