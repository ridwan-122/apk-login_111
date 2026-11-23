package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.auth.authStateFlow
import com.example.auth.signOut
import com.example.data.PinkStyle
import com.example.data.ProfileValidation
import com.example.data.UserRepository
import com.example.ui.ProfileUiState
import com.example.ui.UserViewModel
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.RegistrationScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.BlushTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      BlushTheme {
        AppNavigation()
      }
    }
  }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
  val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
  val user = currentUser

  if (user == null) {
    AuthScreen(onAuthSuccess = {})
  } else {
    key(user.uid) {
      AuthenticatedSession(user = user)
    }
  }
}

@Composable
private fun AuthenticatedSession(user: FirebaseUser) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val credentialManager = remember(context) { CredentialManager.create(context) }

  val viewModel: UserViewModel =
    viewModel(
      key = user.uid,
      factory =
        viewModelFactory {
          initializer {
            val app =
              checkNotNull(this[APPLICATION_KEY]) {
                "APPLICATION_KEY missing from CreationExtras"
              }
            val databaseId = app.getString(R.string.firestore_database_id)
            val db = FirebaseFirestore.getInstance(databaseId)
            UserViewModel(UserRepository(db), user.uid)
          }
        },
    )

  val profileState by viewModel.profileState.collectAsStateWithLifecycle()
  val loginLogs by viewModel.loginLogsState.collectAsStateWithLifecycle()
  val formState by viewModel.formState.collectAsStateWithLifecycle()

  androidx.compose.runtime.LaunchedEffect(profileState) {
    if (profileState is ProfileUiState.Registered) {
      viewModel.recordLoginIfNeeded()
    }
  }

  val handleSignOut: () -> Unit = {
    signOut(credentialManager = credentialManager, scope = scope)
  }

  AnimatedContent(
    targetState = Pair(profileState, formState.isEditingProfile),
    transitionSpec = {
      fadeIn(spring(stiffness = Spring.StiffnessMediumLow)) togetherWith
        fadeOut(spring(stiffness = Spring.StiffnessMediumLow))
    },
    label = "authenticated_flow",
  ) { (state, isEditing) ->
    when (state) {
      is ProfileUiState.Loading -> {
        BlushLoadingScreen()
      }

      is ProfileUiState.NeedsRegistration -> {
        val suggestedUsername =
          remember(user.uid, user.displayName, user.email) {
            ProfileValidation.suggestUsername(user.displayName, user.email)
          }
        RegistrationScreen(
          email = user.email ?: "${user.uid.take(12)}@blush.app",
          initialFullName = user.displayName?.take(60).orEmpty(),
          initialUsername = suggestedUsername,
          initialBio = "",
          initialPinkStyle = PinkStyle.ROSE_QUARTZ,
          photoUrl = user.photoUrl?.toString(),
          isEditMode = false,
          isSubmitting = formState.isSubmitting,
          submitError = formState.submitError,
          onSubmit = { fullName, username, pinkStyle, bio ->
            viewModel.submitRegistration(
              fullName = fullName,
              username = username,
              pinkStyle = pinkStyle,
              bio = bio,
              photoUrl = user.photoUrl?.toString(),
            )
          },
          onCancelEdit = {},
          onSignOut = handleSignOut,
        )
      }

      is ProfileUiState.Registered -> {
        if (isEditing) {
          RegistrationScreen(
            email = state.profile.email,
            initialFullName = state.profile.fullName,
            initialUsername = state.profile.username,
            initialBio = state.profile.bio,
            initialPinkStyle = state.profile.resolvedPinkStyle(),
            photoUrl = state.profile.photoUrl ?: user.photoUrl?.toString(),
            isEditMode = true,
            isSubmitting = formState.isSubmitting,
            submitError = formState.submitError,
            onSubmit = { fullName, username, pinkStyle, bio ->
              viewModel.submitProfileUpdate(
                fullName = fullName,
                username = username,
                pinkStyle = pinkStyle,
                bio = bio,
                photoUrl = state.profile.photoUrl ?: user.photoUrl?.toString(),
              )
            },
            onCancelEdit = { viewModel.closeEditProfile() },
            onSignOut = handleSignOut,
          )
        } else {
          WelcomeScreen(
            profile = state.profile,
            loginLogs = loginLogs,
            showSaveSuccess = formState.showSaveSuccess,
            onDismissSaveSuccess = { viewModel.dismissSaveSuccess() },
            onEditProfile = { viewModel.openEditProfile() },
            onSignOut = handleSignOut,
          )
        }
      }

      is ProfileUiState.Error -> {
        BlushErrorScreen(
          message = state.message,
          onRetry = { viewModel.retryObserve() },
          onSignOut = handleSignOut,
        )
      }
    }
  }
}

@Composable
private fun BlushLoadingScreen() {
  val bgBrush =
    Brush.verticalGradient(
      colors =
        listOf(
          MaterialTheme.colorScheme.surfaceContainerLow,
          MaterialTheme.colorScheme.background,
        )
    )
  Scaffold(contentWindowInsets = WindowInsets.safeDrawing) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().background(bgBrush).padding(innerPadding),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Text(
          text = stringResource(R.string.welcome_loading),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
private fun BlushErrorScreen(
  message: String,
  onRetry: () -> Unit,
  onSignOut: () -> Unit,
) {
  Scaffold(contentWindowInsets = WindowInsets.safeDrawing) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().widthIn(max = 420.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.ErrorOutline,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.size(48.dp),
        )
        Text(
          text = stringResource(R.string.welcome_error_title),
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onBackground,
          textAlign = TextAlign.Center,
        )
        Text(
          text = message,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
          onClick = onRetry,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth().testTag("retry_button"),
        ) {
          Text(stringResource(R.string.welcome_retry))
        }
        OutlinedButton(
          onClick = onSignOut,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth().testTag("error_sign_out_button"),
        ) {
          Text(stringResource(R.string.welcome_button_sign_out))
        }
      }
    }
  }
}
