package uz.gita.firebasestorageexample.util

import android.content.Context
import android.util.Log
import android.widget.Toast

fun myLog(ms: String) {
    Log.d("TTT", ms)
}

fun Context.toast(message: CharSequence, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}
