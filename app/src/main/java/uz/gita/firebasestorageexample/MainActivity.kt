package uz.gita.firebasestorageexample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.firebasestorageexample.adapter.MyAdapter
import uz.gita.firebasestorageexample.data.UploadData
import uz.gita.firebasestorageexample.databinding.ActivityMainBinding
import uz.gita.firebasestorageexample.repository.AppRepository
import uz.gita.firebasestorageexample.util.myLog
import uz.gita.firebasestorageexample.util.toast
import uz.gita.firebasestorageexample.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { MyAdapter() }
    private val appRepository = AppRepository()
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel.imagesData.observe(this) {
            adapter.setData(it)
        }



        binding.apply {
            recycler.layoutManager = LinearLayoutManager(this@MainActivity)
            recycler.adapter = adapter
        }

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { callBack ->
                callBack.data?.data?.let {
                    val file = it.toFile()
                    if (file.length() > 5 * 1024 * 1024) {
                        appRepository.uploadImage(it)
                            .onEach { result ->
                                result.onSuccess { uploadData ->
                                    when (uploadData) {
                                        UploadData.Cancel -> {
                                            myLog("cancel")
                                        }

                                        UploadData.Success -> {
                                            myLog("success")
                                        }

                                        UploadData.Pause -> {
                                            myLog("pause")
                                        }

                                        UploadData.Complete -> {
                                            myLog("comple")
                                        }

                                        else -> {
                                            toast("Error")
                                        }
                                    }
                                }
                                result.onFailure {
                                    //
                                }
                            }
                            .launchIn(scope)
                    } else {
                        toast("You can't upload image")
                    }
                }
            }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        launcher.launch(intent)
    }
}