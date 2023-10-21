package studio.hcmc.reminisce.util

import android.util.Log
import org.slf4j.Logger
import studio.hcmc.reminisce.BuildConfig

object RemoteLogger {
    private lateinit var remoteLogger: Logger

    fun makeFailMsg(currentPosition: String, currentFn: String, body: Throwable): String {
        val loggerBuilder = StringBuilder()
        loggerBuilder.append("[reminisce > $currentPosition > $currentFn] :: ")
        loggerBuilder.append("msg -> ${body.message} \n")
        loggerBuilder.append("localMsg -> ${body.localizedMessage} \n")
        loggerBuilder.append("cause -> ${body.cause} \n")
        loggerBuilder.append("stackTrace -> ${body.stackTrace}")

        return loggerBuilder.toString()
    }

    fun v(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg)
        }
        if (this::remoteLogger.isInitialized) {
            remoteLogger.trace("V // {}: {}", tag, msg)
        }
    }

    fun v(tag: String, msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg, tr)
        }
        if (this::remoteLogger.isInitialized) {
            remoteLogger.trace("V // $tag: $msg", tr)
        }
    }

    fun d(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg)
        }
        if (this::remoteLogger.isInitialized) {
            remoteLogger.debug("D // {}: {}", tag, msg)
        }
    }

    fun d(tag: String, msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg, tr)
        }
        if (this::remoteLogger.isInitialized) {
            remoteLogger.debug("D // $tag: $msg", tr)
        }
    }

    fun i(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg)
        }
        if (this::remoteLogger.isInitialized) {
            remoteLogger.info("I // {}: {}", tag, msg)
        }
    }

    fun i(tag: String, msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg, tr)
        }
        if (this::remoteLogger.isInitialized) {
            remoteLogger.info("I // $tag: $msg", tr)
        }
    }

    fun w(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg)
        }
        if (this::remoteLogger.isInitialized) {
            remoteLogger.warn("W // {}: {}", tag, msg)
        }
    }

    fun w(tag: String, msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg, tr)
        }
        if (this::remoteLogger.isInitialized) {
            remoteLogger.warn("W // $tag: $msg", tr)
        }
    }

    fun e(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg)
        }
        if (this::remoteLogger.isInitialized) {
            remoteLogger.error("E // {}: {}", tag, msg)
        }
    }

    fun e(tag: String, msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg, tr)
        }
        if (this::remoteLogger.isInitialized) {
            remoteLogger.error("E // $tag: $msg", tr)
        }
    }

    fun wtf(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.wtf(tag, msg)
        }
        if (this::remoteLogger.isInitialized) {
            remoteLogger.error("WTF // {}: {}", tag, msg)
        }
    }

    fun wtf(tag: String, msg: String, tr: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.wtf(tag, msg, tr)
        }
        if (this::remoteLogger.isInitialized) {
            remoteLogger.error("WTF // $tag: $msg", tr)
        }
    }
}