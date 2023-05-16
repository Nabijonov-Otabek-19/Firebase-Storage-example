package uz.gita.firebasestorageexample.domain.repository

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AppRepository {

    private val storage = Firebase.storage

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
}