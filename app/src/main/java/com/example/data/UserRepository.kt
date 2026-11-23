package com.example.data

import android.content.Context
import android.os.Build
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(
  private val db: FirebaseFirestore,
  private val auth: FirebaseAuth = Firebase.auth,
) {
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("Pengguna harus masuk dengan Google terlebih dahulu.")
  }

  private fun resolvedEmail(uid: String): String {
    val rawEmail = auth.currentUser?.email?.trim().orEmpty()
    return if (rawEmail.length in 3..120) rawEmail else "${uid.take(40)}@blush.app"
  }

  private fun deviceLabel(): String {
    val model = Build.MODEL?.trim().orEmpty().ifBlank { "Android" }
    return "$model • Blush App".take(80)
  }

  fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
    val uid = requireUserId()
    check(uid == userId) { "Sesi pengguna berubah; membatalkan pembacaan profil." }
    val docRef = db.collection("users").document(userId)
    val path = docRef.path
    docRef
      .snapshots()
      .map { snapshot ->
        if (snapshot.exists()) {
          snapshot.toObject(
            UserProfile::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
          )
        } else {
          null
        }
      }
      .catch { error ->
        val firestoreError =
          generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == uid) {
          handleFirestoreError(exceptionToLog, OperationType.GET, path)
        }
        throw error
      }
      .collect { emit(it) }
  }

  fun observeLoginLogs(userId: String): Flow<List<LoginLog>> = flow {
    val uid = requireUserId()
    check(uid == userId) { "Sesi pengguna berubah; membatalkan pembacaan riwayat login." }
    val logsCollection = db.collection("users").document(userId).collection("loginLogs")
    val path = logsCollection.path
    logsCollection
      .orderBy("loginAt", Query.Direction.DESCENDING)
      .limit(5)
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(
          LoginLog::class.java,
          DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
        )
      }
      .catch { error ->
        val firestoreError =
          generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == uid) {
          handleFirestoreError(exceptionToLog, OperationType.LIST, path)
        }
        throw error
      }
      .collect { emit(it) }
  }

  suspend fun registerProfile(
    fullName: String,
    username: String,
    phone: String,
    city: String,
    pinkStyle: PinkStyle,
    bio: String,
    photoUrl: String?,
  ): Result<Unit> {
    var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
    return try {
      val uid = requireUserId()
      val cleanFullName = fullName.trim()
      val cleanUsername = username.trim()
      val cleanPhone = phone.trim()
      val cleanCity = city.trim()
      val cleanBio = bio.trim()
      val cleanPhotoUrl = photoUrl?.trim()?.takeIf { it.isNotEmpty() && it.length <= 500 }

      require(ProfileValidation.isValidFullName(cleanFullName)) {
        "Nama lengkap harus 2-60 karakter."
      }
      require(ProfileValidation.isValidUsername(cleanUsername)) {
        "Username harus 3-30 karakter (huruf, angka, titik, garis bawah)."
      }
      require(ProfileValidation.isValidPhone(cleanPhone)) {
        "Nomor telepon harus 8-20 karakter angka."
      }
      require(ProfileValidation.isValidCity(cleanCity)) {
        "Kota domisili harus 2-50 karakter."
      }
      require(ProfileValidation.isValidBio(cleanBio)) {
        "Bio maksimal 160 karakter."
      }

      val safeEmail = resolvedEmail(uid)
      val userDocRef = db.collection("users").document(uid)
      val logDocRef = userDocRef.collection("loginLogs").document()
      docPath = userDocRef.path

      val profilePayload =
        mapOf(
            "userId" to uid,
            "email" to safeEmail,
            "fullName" to cleanFullName,
            "username" to cleanUsername,
            "phone" to cleanPhone,
            "city" to cleanCity,
            "pinkStyle" to pinkStyle.name,
            "bio" to cleanBio.takeIf { it.isNotEmpty() },
            "photoUrl" to cleanPhotoUrl,
            "loginCount" to 1L,
            "lastLoginAt" to FieldValue.serverTimestamp(),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
          )
          .filterValues { it != null }

      val loginLogPayload =
        mapOf(
          "userId" to uid,
          "provider" to "google.com",
          "email" to safeEmail,
          "deviceLabel" to deviceLabel(),
          "loginAt" to FieldValue.serverTimestamp(),
        )

      db.batch()
        .set(userDocRef, profilePayload)
        .set(logDocRef, loginLogPayload)
        .commit()
        .await()

      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docPath)
      Result.failure(e)
    }
  }

  suspend fun recordLoginSession(): Result<Unit> {
    var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
    return try {
      val uid = requireUserId()
      val safeEmail = resolvedEmail(uid)
      val userDocRef = db.collection("users").document(uid)
      val logDocRef = userDocRef.collection("loginLogs").document()
      docPath = userDocRef.path

      val loginLogPayload =
        mapOf(
          "userId" to uid,
          "provider" to "google.com",
          "email" to safeEmail,
          "deviceLabel" to deviceLabel(),
          "loginAt" to FieldValue.serverTimestamp(),
        )

      db.batch()
        .update(
          userDocRef,
          mapOf(
            "loginCount" to FieldValue.increment(1L),
            "lastLoginAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
          ),
        )
        .set(logDocRef, loginLogPayload)
        .commit()
        .await()

      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docPath)
      Result.failure(e)
    }
  }

  suspend fun updateProfile(
    fullName: String,
    username: String,
    phone: String,
    city: String,
    pinkStyle: PinkStyle,
    bio: String,
    photoUrl: String?,
  ): Result<Unit> {
    var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
    return try {
      val uid = requireUserId()
      val cleanFullName = fullName.trim()
      val cleanUsername = username.trim()
      val cleanPhone = phone.trim()
      val cleanCity = city.trim()
      val cleanBio = bio.trim()
      val cleanPhotoUrl = photoUrl?.trim()?.takeIf { it.isNotEmpty() && it.length <= 500 }

      require(ProfileValidation.isValidFullName(cleanFullName)) {
        "Nama lengkap harus 2-60 karakter."
      }
      require(ProfileValidation.isValidUsername(cleanUsername)) {
        "Username harus 3-30 karakter (huruf, angka, titik, garis bawah)."
      }
      require(ProfileValidation.isValidPhone(cleanPhone)) {
        "Nomor telepon harus 8-20 karakter angka."
      }
      require(ProfileValidation.isValidCity(cleanCity)) {
        "Kota domisili harus 2-50 karakter."
      }
      require(ProfileValidation.isValidBio(cleanBio)) {
        "Bio maksimal 160 karakter."
      }

      val docRef = db.collection("users").document(uid)
      docPath = docRef.path

      val updates =
        mutableMapOf<String, Any>(
          "fullName" to cleanFullName,
          "username" to cleanUsername,
          "phone" to cleanPhone,
          "city" to cleanCity,
          "pinkStyle" to pinkStyle.name,
          "bio" to cleanBio,
          "updatedAt" to FieldValue.serverTimestamp(),
        )
      if (cleanPhotoUrl != null) {
        updates["photoUrl"] = cleanPhotoUrl
      }

      docRef.update(updates).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docPath)
      Result.failure(e)
    }
  }
}
