package com.example.grub.data

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

interface StorageService {
    fun getDealImageReference(dealId: String): StorageReference
    fun uploadDealImage(
        dealId: String,
        fileUri: Uri,
        onSuccess: (String) -> Unit, // Callback with download URL
        onFailure: (Exception) -> Unit // Callback with error
    )
}

class FirebaseStorageService : StorageService {
    private val storage = FirebaseStorage.getInstance().reference
    override fun getDealImageReference(dealId: String): StorageReference {
        return storage.child("deal_images/$dealId")
    }

    override fun uploadDealImage(
        dealId: String,
        fileUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val imageRef = getDealImageReference(dealId)
        val uploadTask = imageRef.putFile(fileUri)

        uploadTask
            .addOnSuccessListener {
                imageRef.getDownloadUrl().addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }.addOnFailureListener { exception ->
                    onFailure(exception)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}