package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LoginLog
import com.example.data.PinkStyle
import com.example.data.UserProfile
import com.example.data.UserRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
  data object Loading : ProfileUiState

  data object NeedsRegistration : ProfileUiState

  data class Registered(val profile: UserProfile) : ProfileUiState

  data class Error(val message: String) : ProfileUiState
}

data class FormActionState(
  val isSubmitting: Boolean = false,
  val submitError: String? = null,
  val isEditingProfile: Boolean = false,
  val showSaveSuccess: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModel(
  private val repository: UserRepository,
  private val currentUserId: String,
) : ViewModel() {

  private val retryTick = MutableStateFlow(0)
  private var sessionRecorded = false

  val profileState: StateFlow<ProfileUiState> =
    retryTick
      .flatMapLatest {
        repository
          .observeUserProfile(currentUserId)
          .map<UserProfile?, ProfileUiState> { profile ->
            if (profile == null) ProfileUiState.NeedsRegistration
            else ProfileUiState.Registered(profile)
          }
          .catch { error ->
            val firestoreError =
              generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val rootError = firestoreError ?: error
            Log.w(TAG, "Error observing user profile", rootError)
            emit(
              ProfileUiState.Error(
                rootError.localizedMessage ?: "Gagal memuat profil dari server."
              )
            )
          }
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ProfileUiState.Loading,
      )

  val loginLogsState: StateFlow<List<LoginLog>> =
    retryTick
      .flatMapLatest {
        repository
          .observeLoginLogs(currentUserId)
          .catch { error ->
            val firestoreError =
              generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            Log.w(TAG, "Error observing login logs", firestoreError ?: error)
            emit(emptyList())
          }
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = emptyList(),
      )

  private val _formState = MutableStateFlow(FormActionState())
  val formState: StateFlow<FormActionState> = _formState.asStateFlow()

  fun retryObserve() {
    retryTick.update { it + 1 }
  }

  fun recordLoginIfNeeded() {
    if (sessionRecorded) return
    sessionRecorded = true
    viewModelScope.launch {
      repository.recordLoginSession().onFailure {
        sessionRecorded = false
      }
    }
  }

  fun openEditProfile() {
    _formState.update {
      it.copy(isEditingProfile = true, submitError = null, showSaveSuccess = false)
    }
  }

  fun closeEditProfile() {
    _formState.update { it.copy(isEditingProfile = false, submitError = null) }
  }

  fun dismissSaveSuccess() {
    _formState.update { it.copy(showSaveSuccess = false) }
  }

  fun submitRegistration(
    fullName: String,
    username: String,
    phone: String,
    city: String,
    pinkStyle: PinkStyle,
    bio: String,
    photoUrl: String?,
  ) {
    if (_formState.value.isSubmitting) return
    viewModelScope.launch {
      _formState.update { it.copy(isSubmitting = true, submitError = null) }
      sessionRecorded = true
      val result =
        repository.registerProfile(
          fullName = fullName,
          username = username,
          phone = phone,
          city = city,
          pinkStyle = pinkStyle,
          bio = bio,
          photoUrl = photoUrl,
        )
      result
        .onSuccess {
          _formState.update {
            it.copy(isSubmitting = false, submitError = null, isEditingProfile = false)
          }
        }
        .onFailure { error ->
          sessionRecorded = false
          _formState.update {
            it.copy(
              isSubmitting = false,
              submitError = error.localizedMessage ?: "Gagal menyimpan registrasi.",
            )
          }
        }
    }
  }

  fun submitProfileUpdate(
    fullName: String,
    username: String,
    phone: String,
    city: String,
    pinkStyle: PinkStyle,
    bio: String,
    photoUrl: String?,
  ) {
    if (_formState.value.isSubmitting) return
    viewModelScope.launch {
      _formState.update { it.copy(isSubmitting = true, submitError = null) }
      val result =
        repository.updateProfile(
          fullName = fullName,
          username = username,
          phone = phone,
          city = city,
          pinkStyle = pinkStyle,
          bio = bio,
          photoUrl = photoUrl,
        )
      result
        .onSuccess {
          _formState.update {
            it.copy(
              isSubmitting = false,
              submitError = null,
              isEditingProfile = false,
              showSaveSuccess = true,
            )
          }
        }
        .onFailure { error ->
          _formState.update {
            it.copy(
              isSubmitting = false,
              submitError = error.localizedMessage ?: "Gagal memperbarui profil.",
            )
          }
        }
    }
  }

  private companion object {
    const val TAG = "UserVM"
    const val STOP_TIMEOUT_MILLIS = 5000L
  }
}
