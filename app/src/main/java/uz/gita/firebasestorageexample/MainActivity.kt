package uz.gita.firebasestorageexample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.firebasestorageexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { MyAdapter() }
    private val repository = Repository()
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        repository.getImages()
            .onEach { listImgUrl ->
                listImgUrl.onSuccess {
                    adapter.setData(it)
                }
                listImgUrl.onFailure {
                    Log.d("AAA", "Main = ${it.message}")
                }
            }.launchIn(scope)


        binding.apply {
            recycler.layoutManager = LinearLayoutManager(this@MainActivity)
            recycler.adapter = adapter
        }
    }
}