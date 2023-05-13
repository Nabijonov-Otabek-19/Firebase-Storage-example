package uz.gita.firebasestorageexample

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.gita.firebasestorageexample.adapter.MyAdapter
import uz.gita.firebasestorageexample.databinding.ActivityMainBinding
import uz.gita.firebasestorageexample.util.myLog
import uz.gita.firebasestorageexample.util.toast
import uz.gita.firebasestorageexample.viewmodel.MainViewModel
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { MyAdapter() }
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter.setLongClickListener {
            bottomSheetDialog(it)
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

    private fun setBackground(path: String) {
        if (path == "") {
            myLog("IMage path not found")
            toast("IMage path not found")
        } else {
            val bitmap = BitmapFactory.decodeFile(path)
            val wallpaperManager = WallpaperManager.getInstance(applicationContext)
            try {
                wallpaperManager.setBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("Recycle")
    private fun getImagePath(uri: String): String {
        val imageName = uri.substring(uri.lastIndexOf("%") + 1, uri.indexOf("?"))

        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(imageName)
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        val contentResolver = contentResolver
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        return if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val imagePath = cursor.getString(columnIndex)

            myLog("IMG Path = $imagePath")

            imagePath
        } else {
            // Image not found
            ""
        }
    }

    @SuppressLint("Recycle")
    private fun isImageExists(uri: String): Boolean {
        val imageName = uri.substring(uri.lastIndexOf("%") + 1, uri.indexOf("?"))

        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(imageName)
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        val contentResolver = contentResolver
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        return cursor != null && cursor.moveToFirst()
    }

    private fun downloadImage(uri: String) {
        val directory = File(Environment.DIRECTORY_PICTURES)

        val imageFile = uri.substring(uri.lastIndexOf("%") + 1, uri.indexOf("?"))

        if (!directory.exists()) {
            directory.mkdirs()
        }

        myLog(imageFile)

        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(uri)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
            )
                .setAllowedOverRoaming(false)
                .setTitle(imageFile)
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    imageFile
                )
        }
        downloadManager.enqueue(request)
    }

    private fun bottomSheetDialog(uri: String) {
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(false)
        val view = dialog.layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

        val btnSave = view.findViewById<AppCompatTextView>(R.id.btnSave)
        val btmSetFon = view.findViewById<AppCompatTextView>(R.id.btnSetFon)

        btnSave.setOnClickListener {
            if (isImageExists(uri)) {
                toast("Image is already saved")

            } else downloadImage(uri)
            dialog.dismiss()
        }

        btmSetFon.setOnClickListener {
            val path: String = if (isImageExists(uri)) {
                getImagePath(uri)
            } else {
                downloadImage(uri)
                getImagePath(uri)
            }
            setBackground(path)
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }
}