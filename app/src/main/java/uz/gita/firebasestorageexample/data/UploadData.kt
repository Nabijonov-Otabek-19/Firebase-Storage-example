package uz.gita.firebasestorageexample.data

sealed interface UploadData {
    object Cancel : UploadData
    object Success : UploadData
    object Complete : UploadData
    object Pause : UploadData
    data class Progress(val progress: Int) : UploadData
}