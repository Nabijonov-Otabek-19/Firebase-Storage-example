package uz.gita.firebasestorageexample.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class Repository {

    private val storage = Firebase.storage

    fun getImages(): Flow<Result<List<String>>> = callbackFlow {
        val imageList = ArrayList<String>()
        storage.reference.child("images").listAll()
            .addOnSuccessListener { listResult ->
                val imageRefs = listResult.items

                for (imageRef in imageRefs) {
                    imageRef.downloadUrl
                        .addOnSuccessListener { url ->
                            val imgUrl = url.toString()
                            imageList.add(imgUrl)
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
