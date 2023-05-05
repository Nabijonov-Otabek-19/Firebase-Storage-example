package uz.gita.firebasestorageexample.repository

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import uz.gita.firebasestorageexample.data.UploadData
import java.util.UUID

class AppRepository {

    private val storage = Firebase.storage
    private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null

    fun cancelUpload() {
        uploadTask?.cancel()
    }

    fun pause() {
        uploadTask?.pause()
    }

    fun deleteImage(name: String): Flow<Result<Unit>> = callbackFlow {
        storage.reference.child("images/$name")
            .delete()
            .addOnSuccessListener {
                trySend(Result.success(Unit))
            }
            .addOnFailureListener {
                trySend(Result.failure(it))
            }
        awaitClose()
    }

    fun getImages(): Flow<Result<List<Uri>>> = callbackFlow {
        val imageList = ArrayList<Uri>()
        storage.reference.child("images").listAll()
            .addOnSuccessListener { listResult ->
                val imageRefs = listResult.items

                for (imageRef in imageRefs) {
                    imageRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            imageList.add(uri)
                            trySend(Result.success(imageList))
                        }
                        .addOnFailureListener {
                            trySend(Result.failure(it))
                        }
                }
            }
            .addOnFailureListener {
                trySend(Result.failure(it))
            }
        awaitClose()
    }

    fun uploadImage(imageUri: Uri): Flow<Result<UploadData>> = callbackFlow {
        uploadTask = storage.reference.child("images/${UUID.randomUUID()}.png")
            .putFile(imageUri)
            .addOnCompleteListener {
                uploadTask = null
                trySend(Result.success(UploadData.Complete))
            }
            .addOnCanceledListener { trySend(Result.success(UploadData.Cancel)) }
            .addOnPausedListener { trySend(Result.success(UploadData.Pause)) }
            .addOnProgressListener {
                val progress = (it.bytesTransferred * 100 / it.totalByteCount).toInt()
                trySend(Result.success(UploadData.Progress(progress)))
            }
            .addOnSuccessListener {

                trySend(Result.success(UploadData.Success))
            }
            .addOnFailureListener { trySend(Result.failure(it)) }
        awaitClose()
    }
}
