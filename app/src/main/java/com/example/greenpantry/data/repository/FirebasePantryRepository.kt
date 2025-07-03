package com.example.greenpantry.data.repository

import android.util.Log
import com.example.greenpantry.data.database.PantryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePantryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val TAG = "FirebasePantryRepo"
        private const val USERS_COLLECTION = "users"
        private const val PANTRY_COLLECTION = "pantryItems"
    }
    
    private val currentUserId: String?
        get() = auth.currentUser?.uid
    
    suspend fun syncPantryItemToFirebase(item: PantryItem): Result<Unit> {
        return try {
            val userId = currentUserId ?: return Result.failure(Exception("User not logged in"))
            
            val itemData = hashMapOf(
                "name" to item.name,
                "description" to item.description,
                "category" to item.category,
                "isPackaged" to item.isPackaged,
                "brand" to item.brand,
                "quantity" to item.quantity,
                "recognitionConfidence" to item.recognitionConfidence,
                "imageUri" to item.imageUri,
                "dateAdded" to item.dateAdded,
            )
            
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PANTRY_COLLECTION)
                .add(itemData)
                .await()
            
            Log.d(TAG, "Pantry item synced to Firebase: ${item.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync pantry item", e)
            Result.failure(e)
        }
    }
    
    suspend fun getAllPantryItemsFromFirebase(): Result<List<PantryItem>> {
        return try {
            val userId = currentUserId ?: return Result.failure(Exception("User not logged in"))
            
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PANTRY_COLLECTION)
                .get()
                .await()
            
            val items = snapshot.documents.mapNotNull { doc ->
                try {
                    PantryItem(
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        imageResId = 0,
                        category = doc.getString("category"),
                        isPackaged = doc.getBoolean("isPackaged") ?: false,
                        brand = doc.getString("brand"),
                        quantity = doc.getString("quantity"),
                        recognitionConfidence = doc.getDouble("recognitionConfidence")?.toFloat(),
                        imageUri = doc.getString("imageUri"),
                        dateAdded = doc.getLong("dateAdded") ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing pantry item", e)
                    null
                }
            }
            
            Log.d(TAG, "Retrieved ${items.size} items from Firebase")
            Result.success(items)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get pantry items from Firebase", e)
            Result.failure(e)
        }
    }
}