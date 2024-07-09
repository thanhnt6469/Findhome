package com.app.findhome.ui.pages.article

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.findhome.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@SuppressLint("AutoboxingStateCreation")
class ArticleViewModel : ViewModel() {
    var id by mutableIntStateOf(0)
    var title by mutableStateOf("")
    var type by mutableStateOf("")
    var selectedCity by mutableStateOf("Đà Nẵng")
    var selectedTown by mutableStateOf("Ngũ Hành Sơn")
    var address by mutableStateOf("")
    var description by mutableStateOf("")
    var pickPath by mutableStateOf("")
    var price by mutableStateOf(99)
    var member by mutableIntStateOf(0)
    var wifi by mutableStateOf(false)
    var garage by mutableStateOf(false)
    var size by mutableIntStateOf(0)
    var score by mutableStateOf(4.9)
    var imageUri by mutableStateOf<Uri?>(null)
    var userId by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    private var documentId by mutableStateOf<String?>(null)
    val isDataValid: Boolean
        get() = title.isNotBlank() && type.isNotBlank() && selectedCity.isNotBlank() &&
                selectedTown.isNotBlank() && address.isNotBlank() &&
                description.isNotBlank() && price > 0 && size > 0 && (imageUri != null || pickPath.isNotBlank())

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        auth.currentUser?.let { user ->
            userId = user.uid
        }
        initializeId()
    }

    private fun initializeId() {
        viewModelScope.launch {
            try {
                val docRef = db.collection("settings").document("articleId")
                val docSnapshot = docRef.get().await()
                if (docSnapshot.exists()) {
                    id = docSnapshot.getLong("currentId")?.toInt() ?: 0
                } else {
                    docRef.set(mapOf("currentId" to 0)).await()
                }
            } catch (e: Exception) {
                Log.e("initializeId", "Failed to initialize ID: ${e.message}")
            }
        }
    }

    private suspend fun getNextId(): Int {
        val docRef = db.collection("settings").document("articleId")
        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentId = snapshot.getLong("currentId")?.toInt() ?: 0
            val nextId = currentId + 1
            transaction.update(docRef, "currentId", nextId)
            nextId
        }.await()
    }

    fun resetData() {
        title = ""
        type = ""
        address = ""
        description = ""
        pickPath = ""
        price = 99
        member = 0
        wifi = false
        garage = false
        size = 0
        score = 4.9
        imageUri = null
        userId = auth.currentUser?.uid ?: ""
    }

    fun postArticle(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onFailure(Exception("User is not authenticated"))
            return
        }
        viewModelScope.launch {
            isLoading = true
            try {
                id = getNextId()
                Log.d("imageUri", "postArticle: $imageUri")
                val imageUrl = uploadImageToFirebaseStorage(imageUri)
                val article = mapOf(
                    "id" to id,
                    "type" to type,
                    "title" to title,
                    "address" to "$address, $selectedTown, $selectedCity",
                    "pickPath" to imageUrl,
                    "price" to price,
                    "member" to member,
                    "size" to size,
                    "score" to score,
                    "description" to description,
                    "userId" to userId,
                    "facilities" to mapOf("wifi" to wifi, "garage" to garage)
                )
                Log.d("article", "postArticle: $article")
                db.collection("rooms")
                    .add(article)
                    .await()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun getDocumentIdById(articleId: Int): String? {
        return try {
            val querySnapshot = db.collection("rooms")
                .whereEqualTo("id", articleId)
                .limit(1)
                .get()
                .await()

            if (querySnapshot.documents.isNotEmpty()) {
                querySnapshot.documents[0].id
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("getDocumentIdById", "Failed to get document ID: ${e.message}")
            null
        }
    }


    fun updateArticle(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onFailure(Exception("User is not authenticated"))
            return
        }
        viewModelScope.launch {
            isLoading = true
            try {
                documentId = getDocumentIdById(id)

                if (documentId == null) {
                    onFailure(Exception("Document ID is not found"))
                    return@launch
                }

                val imageUrl = imageUri?.let { uploadImageToFirebaseStorage(it) } ?: pickPath
                val article = mapOf(
                    "id" to id,
                    "type" to type,
                    "title" to title,
                    "address" to "$address, $selectedTown, $selectedCity",
                    "pickPath" to imageUrl,
                    "price" to price,
                    "member" to member,
                    "size" to size,
                    "score" to score,
                    "description" to description,
                    "userId" to userId,
                    "facilities" to mapOf("wifi" to wifi, "garage" to garage)
                )
                db.collection("rooms").document(documentId!!).set(article).await()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteArticle(articleId: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val documentId = getDocumentIdById(articleId)

                if (documentId == null) {
                    onFailure(Exception("Document ID is not found"))
                    return@launch
                }

                db.collection("rooms").document(documentId).delete().await()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    private suspend fun uploadImageToFirebaseStorage(uri: Uri?): String? {
        return uri?.let {
            val storageRef = storage.reference.child("images/${UUID.randomUUID()}")
            val uploadTask = storageRef.putFile(it).await()
            storageRef.downloadUrl.await().toString()
        }
    }
}
