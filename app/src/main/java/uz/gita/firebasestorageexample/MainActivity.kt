package uz.gita.firebasestorageexample

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.gita.firebasestorageexample.adapter.MyAdapter
import uz.gita.firebasestorageexample.databinding.ActivityMainBinding
import uz.gita.firebasestorageexample.util.myLog
import uz.gita.firebasestorageexample.util.toast
import uz.gita.firebasestorageexample.viewmodel.MainViewModel
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { MyAdapter() }
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter.setLongClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
            ) {
                askPermissions(it)
            } else {
                bottomSheetDialog(it)
            }
        }

        viewModel.imagesData.observe(this) {
            adapter.setData(it)
        }

        viewModel.errorData.observe(this) {
            toast(it)
            myLog(it)
        }

        binding.apply {
            recycler.layoutManager = GridLayoutManager(this@MainActivity, 2)
            recycler.adapter = adapter
        }
    }

    private fun askPermissions(uri: Uri) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("Permission required to save photos from the Web.")
                    .setPositiveButton("Accept") { dialog, id ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                        )
                        finish()
                    }
                    .setNegativeButton("Deny") { dialog, id -> dialog.cancel() }
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
            }
        } else {
            downloadImage(uri.toString())
        }
    }

    // This method is not working
    @SuppressLint("Recycle")
    private fun isImageExists(url: String): Boolean {
        val fileName = url.substring(url.lastIndexOf("/") + 1)

        val projection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )

        val selection = "${MediaStore.Images.Media.DATA} like ?"
        val selectionArgs = arrayOf(Environment.DIRECTORY_PICTURES + "/" + fileName)

        val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null)

        return (cursor?.count ?: 0) > 0
    }

    private fun downloadImage(url: String) {
        val directory = File(Environment.DIRECTORY_PICTURES)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
            )
                .setAllowedOverRoaming(false)
                .setTitle(url.substring(url.lastIndexOf("/") + 1))
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    url.substring(url.lastIndexOf("/") + 1)
                )
        }
        downloadManager.enqueue(request)
    }

    private fun bottomSheetDialog(uri: Uri) {
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(false)
        val view = dialog.layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

        val btnSave = view.findViewById<AppCompatTextView>(R.id.btnSave)
        val btmSetFon = view.findViewById<AppCompatTextView>(R.id.btnSetFon)

        btnSave.setOnClickListener {
            if (isImageExists(uri.toString())) {
                myLog("Exist" + isImageExists(uri.toString()).toString())
                toast("Image is already saved")

            } else downloadImage(uri.toString())
            dialog.dismiss()
        }

        btmSetFon.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }
}