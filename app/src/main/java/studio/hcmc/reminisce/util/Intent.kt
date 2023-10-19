package studio.hcmc.reminisce.util

import android.app.Activity
import android.content.Intent
import android.os.Build
import java.io.Serializable

fun Intent.setActivity(activity: Activity?, resultCode: Int) {
    activity?.setResult(resultCode, this)
}

//inline fun <reified T: Serializable> Intent.getSerializableContent(key: String): T? {
//    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//        getSerializableExtra(key, T::class.java)
//    } else {
//        getSerializableExtra(key) as? T
//    }
//}

fun <T: Serializable> Intent.getSerializableContent(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getSerializableExtra(key, clazz)
    } else {
        this.getSerializableExtra(key) as T?
    }
}