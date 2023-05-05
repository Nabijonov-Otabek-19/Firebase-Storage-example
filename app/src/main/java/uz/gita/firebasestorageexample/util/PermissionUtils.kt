package uz.gita.firebasestorageexample.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

fun ComponentActivity.checkPermissions(array: List<String>, block: () -> Unit) {
    Dexter.withContext(this).withPermissions(array)
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                report.let {
                    if (report.areAllPermissionsGranted()) {
                        block.invoke()
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest>,
                token: PermissionToken
            ) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }).check()
}

fun myLog(ms: String) {
    Log.d("TTT", ms)
}

fun Context.toast(message: CharSequence, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}
