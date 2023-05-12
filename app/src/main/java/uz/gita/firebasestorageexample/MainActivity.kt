package uz.gita.firebasestorageexample

import android.net.Uri
import android.os.Bundle
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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { MyAdapter() }
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        adapter.setLongClickListener {
            myLog(it.toString())
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
    private fun bottomSheetDialog(uri: Uri) {
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(false)
        val view = dialog.layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)

        val btnSave = view.findViewById<AppCompatTextView>(R.id.btnSave)
        val btmSetFon = view.findViewById<AppCompatTextView>(R.id.btnSetFon)

        btnSave.setOnClickListener {
            dialog.dismiss()
        }

        btmSetFon.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }
}