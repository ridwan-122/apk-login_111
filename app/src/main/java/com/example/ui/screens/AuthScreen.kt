package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.HowToReg
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.auth.onGoogleSignInClicked
import com.example.data.PendingProfileDraft
import com.example.data.PinkStyle
import com.example.data.ProfileValidation
import com.example.ui.components.PinkStyleSelector

private enum class AuthMode {
  LOGIN,
  REGISTER,
}

@Composable
fun AuthScreen(
  onPrepareDraft: (PendingProfileDraft) -> Unit,
  onAuthSuccess: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val credentialManager = remember(context) { CredentialManager.create(context) }

  var authMode by rememberSaveable { mutableStateOf(AuthMode.LOGIN) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  // Login personal data inputs
  var loginEmail by rememberSaveable { mutableStateOf("") }
  var loginFullName by rememberSaveable { mutableStateOf("") }
  var attemptedLogin by rememberSaveable { mutableStateOf(false) }

  // Register personal data inputs
  var regEmail by rememberSaveable { mutableStateOf("") }
  var regFullName by rememberSaveable { mutableStateOf("") }
  var regUsername by rememberSaveable { mutableStateOf("") }
  var regPhone by rememberSaveable { mutableStateOf("") }
  var regCity by rememberSaveable { mutableStateOf("") }
  var regBio by rememberSaveable { mutableStateOf("") }
  var regPinkStyleName by rememberSaveable { mutableStateOf(PinkStyle.ROSE_QUARTZ.name) }
  var attemptedRegister by rememberSaveable { mutableStateOf(false) }

  val regPinkStyle = PinkStyle.fromName(regPinkStyleName)

  val isLoginEmailValid = ProfileValidation.isValidEmail(loginEmail)
  val isLoginNameValid = ProfileValidation.isValidFullName(loginFullName)
  val isLoginFormValid = isLoginEmailValid && isLoginNameValid

  val isRegEmailValid = ProfileValidation.isValidEmail(regEmail)
  val isRegFullNameValid = ProfileValidation.isValidFullName(regFullName)
  val isRegUsernameValid = ProfileValidation.isValidUsername(regUsername)
  val isRegPhoneValid = ProfileValidation.isValidPhone(regPhone)
  val isRegCityValid = ProfileValidation.isValidCity(regCity)
  val isRegBioValid = ProfileValidation.isValidBio(regBio)
  val isRegisterFormValid =
    isRegEmailValid &&
      isRegFullNameValid &&
      isRegUsernameValid &&
      isRegPhoneValid &&
      isRegCityValid &&
      isRegBioValid

  val bgBrush =
    Brush.verticalGradient(
      colors =
        listOf(
          MaterialTheme.colorScheme.surfaceContainerLow,
          MaterialTheme.colorScheme.background,
          MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
        )
    )

  val orbPrimary = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
  val orbSecondary = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)

  val fieldColors =
    OutlinedTextFieldDefaults.colors(
      focusedBorderColor = MaterialTheme.colorScheme.primary,
      focusedLabelColor = MaterialTheme.colorScheme.primary,
      cursorColor = MaterialTheme.colorScheme.primary,
    )

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = Color.Transparent,
    contentWindowInsets = WindowInsets.safeDrawing,
  ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize().background(bgBrush)) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
          color = orbPrimary,
          radius = size.minDimension * 0.42f,
          center = Offset(size.width * 0.9f, size.height * 0.12f),
        )
        drawCircle(
          color = orbSecondary,
          radius = size.minDimension * 0.35f,
          center = Offset(size.width * 0.08f, size.height * 0.85f),
        )
      }

      Column(
        modifier =
          Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().widthIn(max = 520.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          // Brand Header Pill
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
              )
              Text(
                text = stringResource(R.string.app_name).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = stringResource(R.string.auth_brand_tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Hero Illustration Card
          Card(
            shape = MaterialTheme.shapes.large,
            colors =
              CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
              ),
            border =
              BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Box(modifier = Modifier.fillMaxWidth().height(148.dp)) {
              Image(
                painter = painterResource(id = R.drawable.img_login_hero),
                contentDescription = stringResource(R.string.cd_login_hero),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
              )
              Box(
                modifier =
                  Modifier
                    .fillMaxSize()
                    .background(
                      Brush.verticalGradient(
                        colors =
                          listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                          )
                      )
                    )
              )
              Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.92f),
                shape = CircleShape,
                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                  )
                  Text(
                    text = "Tema Pink • Firebase Auth & Firestore",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Mode Switcher (Masuk / Daftar)
          Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(4.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              AuthModeTab(
                selected = authMode == AuthMode.LOGIN,
                label = stringResource(R.string.auth_tab_login),
                icon = Icons.AutoMirrored.Rounded.Login,
                onClick = {
                  authMode = AuthMode.LOGIN
                  errorMessage = null
                },
                modifier = Modifier.weight(1f).testTag("auth_tab_login"),
              )
              AuthModeTab(
                selected = authMode == AuthMode.REGISTER,
                label = stringResource(R.string.auth_tab_register),
                icon = Icons.Rounded.HowToReg,
                onClick = {
                  authMode = AuthMode.REGISTER
                  errorMessage = null
                },
                modifier = Modifier.weight(1f).testTag("auth_tab_register"),
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Main Auth Form & Action Card
          Card(
            shape = MaterialTheme.shapes.large,
            colors =
              CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
              ),
            border =
              BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(22.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              AnimatedContent(
                targetState = authMode,
                transitionSpec = {
                  fadeIn(spring(stiffness = Spring.StiffnessMediumLow)) togetherWith
                    fadeOut(spring(stiffness = Spring.StiffnessMediumLow))
                },
                label = "auth_mode_content",
              ) { mode ->
                Column(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                  Text(
                    text =
                      if (mode == AuthMode.LOGIN) stringResource(R.string.auth_login_title)
                      else stringResource(R.string.auth_register_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                  )
                  Text(
                    text =
                      if (mode == AuthMode.LOGIN) stringResource(R.string.auth_login_subtitle)
                      else stringResource(R.string.auth_register_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                  )

                  if (mode == AuthMode.LOGIN) {
                    // Data Diri Login (Email + Nama Lengkap)
                    OutlinedTextField(
                      value = loginEmail,
                      onValueChange = { if (it.length <= 120) loginEmail = it },
                      label = { Text(stringResource(R.string.reg_label_email)) },
                      placeholder = { Text(stringResource(R.string.reg_hint_email)) },
                      leadingIcon = {
                        Icon(
                          imageVector = Icons.Rounded.Email,
                          contentDescription = null,
                          tint = MaterialTheme.colorScheme.primary,
                        )
                      },
                      singleLine = true,
                      isError = (attemptedLogin || loginEmail.isNotEmpty()) && !isLoginEmailValid,
                      supportingText = {
                        if ((attemptedLogin || loginEmail.isNotEmpty()) && !isLoginEmailValid) {
                          Text(stringResource(R.string.reg_error_email))
                        }
                      },
                      keyboardOptions =
                        KeyboardOptions(
                          keyboardType = KeyboardType.Email,
                          imeAction = ImeAction.Next,
                        ),
                      shape = MaterialTheme.shapes.medium,
                      colors = fieldColors,
                      modifier = Modifier.fillMaxWidth().testTag("login_email_input"),
                    )

                    OutlinedTextField(
                      value = loginFullName,
                      onValueChange = { if (it.length <= 60) loginFullName = it },
                      label = { Text(stringResource(R.string.reg_label_full_name)) },
                      placeholder = { Text(stringResource(R.string.reg_hint_full_name)) },
                      leadingIcon = {
                        Icon(
                          imageVector = Icons.Rounded.Person,
                          contentDescription = null,
                          tint = MaterialTheme.colorScheme.primary,
                        )
                      },
                      singleLine = true,
                      isError =
                        (attemptedLogin || loginFullName.isNotEmpty()) && !isLoginNameValid,
                      supportingText = {
                        if ((attemptedLogin || loginFullName.isNotEmpty()) && !isLoginNameValid) {
                          Text(stringResource(R.string.reg_error_full_name))
                        }
                      },
                      keyboardOptions =
                        KeyboardOptions(
                          capitalization = KeyboardCapitalization.Words,
                          imeAction = ImeAction.Done,
                        ),
                      shape = MaterialTheme.shapes.medium,
                      colors = fieldColors,
                      modifier = Modifier.fillMaxWidth().testTag("login_name_input"),
                    )
                  } else {
                    // Data Diri Daftar Lengkap
                    OutlinedTextField(
                      value = regEmail,
                      onValueChange = { if (it.length <= 120) regEmail = it },
                      label = { Text(stringResource(R.string.reg_label_email)) },
                      placeholder = { Text(stringResource(R.string.reg_hint_email)) },
                      leadingIcon = {
                        Icon(
                          imageVector = Icons.Rounded.Email,
                          contentDescription = null,
                          tint = MaterialTheme.colorScheme.primary,
                        )
                      },
                      singleLine = true,
                      isError = (attemptedRegister || regEmail.isNotEmpty()) && !isRegEmailValid,
                      supportingText = {
                        if ((attemptedRegister || regEmail.isNotEmpty()) && !isRegEmailValid) {
                          Text(stringResource(R.string.reg_error_email))
                        }
                      },
                      keyboardOptions =
                        KeyboardOptions(
                          keyboardType = KeyboardType.Email,
                          imeAction = ImeAction.Next,
                        ),
                      shape = MaterialTheme.shapes.medium,
                      colors = fieldColors,
                      modifier = Modifier.fillMaxWidth().testTag("register_email_input"),
                    )

                    OutlinedTextField(
                      value = regFullName,
                      onValueChange = { if (it.length <= 60) regFullName = it },
                      label = { Text(stringResource(R.string.reg_label_full_name)) },
                      placeholder = { Text(stringResource(R.string.reg_hint_full_name)) },
                      leadingIcon = {
                        Icon(
                          imageVector = Icons.Rounded.Person,
                          contentDescription = null,
                          tint = MaterialTheme.colorScheme.primary,
                        )
                      },
                      singleLine = true,
                      isError =
                        (attemptedRegister || regFullName.isNotEmpty()) && !isRegFullNameValid,
                      supportingText = {
                        if (
                          (attemptedRegister || regFullName.isNotEmpty()) && !isRegFullNameValid
                        ) {
                          Text(stringResource(R.string.reg_error_full_name))
                        }
                      },
                      keyboardOptions =
                        KeyboardOptions(
                          capitalization = KeyboardCapitalization.Words,
                          imeAction = ImeAction.Next,
                        ),
                      shape = MaterialTheme.shapes.medium,
                      colors = fieldColors,
                      modifier = Modifier.fillMaxWidth().testTag("register_full_name_input"),
                    )

                    OutlinedTextField(
                      value = regUsername,
                      onValueChange = { raw ->
                        val filtered = raw.filter { it.isLetterOrDigit() || it == '_' || it == '.' }
                        if (filtered.length <= 30) regUsername = filtered
                      },
                      label = { Text(stringResource(R.string.reg_label_username)) },
                      placeholder = { Text(stringResource(R.string.reg_hint_username)) },
                      leadingIcon = {
                        Icon(
                          imageVector = Icons.Rounded.AlternateEmail,
                          contentDescription = null,
                          tint = MaterialTheme.colorScheme.primary,
                        )
                      },
                      singleLine = true,
                      isError =
                        (attemptedRegister || regUsername.isNotEmpty()) && !isRegUsernameValid,
                      supportingText = {
                        if (
                          (attemptedRegister || regUsername.isNotEmpty()) && !isRegUsernameValid
                        ) {
                          Text(stringResource(R.string.reg_error_username))
                        }
                      },
                      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                      shape = MaterialTheme.shapes.medium,
                      colors = fieldColors,
                      modifier = Modifier.fillMaxWidth().testTag("register_username_input"),
                    )

                    OutlinedTextField(
                      value = regPhone,
                      onValueChange = { raw ->
                        val filtered = raw.filter { it.isDigit() || it == '+' || it == '-' || it == ' ' }
                        if (filtered.length <= 20) regPhone = filtered
                      },
                      label = { Text(stringResource(R.string.reg_label_phone)) },
                      placeholder = { Text(stringResource(R.string.reg_hint_phone)) },
                      leadingIcon = {
                        Icon(
                          imageVector = Icons.Rounded.Phone,
                          contentDescription = null,
                          tint = MaterialTheme.colorScheme.primary,
                        )
                      },
                      singleLine = true,
                      isError = (attemptedRegister || regPhone.isNotEmpty()) && !isRegPhoneValid,
                      supportingText = {
                        if ((attemptedRegister || regPhone.isNotEmpty()) && !isRegPhoneValid) {
                          Text(stringResource(R.string.reg_error_phone))
                        }
                      },
                      keyboardOptions =
                        KeyboardOptions(
                          keyboardType = KeyboardType.Phone,
                          imeAction = ImeAction.Next,
                        ),
                      shape = MaterialTheme.shapes.medium,
                      colors = fieldColors,
                      modifier = Modifier.fillMaxWidth().testTag("register_phone_input"),
                    )

                    OutlinedTextField(
                      value = regCity,
                      onValueChange = { if (it.length <= 50) regCity = it },
                      label = { Text(stringResource(R.string.reg_label_city)) },
                      placeholder = { Text(stringResource(R.string.reg_hint_city)) },
                      leadingIcon = {
                        Icon(
                          imageVector = Icons.Rounded.LocationCity,
                          contentDescription = null,
                          tint = MaterialTheme.colorScheme.primary,
                        )
                      },
                      singleLine = true,
                      isError = (attemptedRegister || regCity.isNotEmpty()) && !isRegCityValid,
                      supportingText = {
                        if ((attemptedRegister || regCity.isNotEmpty()) && !isRegCityValid) {
                          Text(stringResource(R.string.reg_error_city))
                        }
                      },
                      keyboardOptions =
                        KeyboardOptions(
                          capitalization = KeyboardCapitalization.Words,
                          imeAction = ImeAction.Next,
                        ),
                      shape = MaterialTheme.shapes.medium,
                      colors = fieldColors,
                      modifier = Modifier.fillMaxWidth().testTag("register_city_input"),
                    )

                    OutlinedTextField(
                      value = regBio,
                      onValueChange = { if (it.length <= 160) regBio = it },
                      label = { Text(stringResource(R.string.reg_label_bio)) },
                      placeholder = { Text(stringResource(R.string.reg_hint_bio)) },
                      leadingIcon = {
                        Icon(
                          imageVector = Icons.Rounded.EditNote,
                          contentDescription = null,
                          tint = MaterialTheme.colorScheme.primary,
                        )
                      },
                      minLines = 2,
                      maxLines = 3,
                      isError = !isRegBioValid,
                      supportingText = {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                          Text(if (!isRegBioValid) stringResource(R.string.reg_error_bio) else "")
                          Text("${regBio.trim().length}/160")
                        }
                      },
                      shape = MaterialTheme.shapes.medium,
                      colors = fieldColors,
                      modifier = Modifier.fillMaxWidth().testTag("register_bio_input"),
                    )

                    PinkStyleSelector(
                      selectedStyle = regPinkStyle,
                      onSelectStyle = { regPinkStyleName = it.name },
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              AnimatedVisibility(visible = errorMessage != null) {
                Surface(
                  color = MaterialTheme.colorScheme.errorContainer,
                  shape = MaterialTheme.shapes.small,
                  modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                ) {
                  Text(
                    text = errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                  )
                }
              }

              // Primary Google Sign-In CTA (requires valid Data Diri first)
              Button(
                onClick = {
                  errorMessage = null
                  if (authMode == AuthMode.LOGIN) {
                    attemptedLogin = true
                    if (!isLoginFormValid) {
                      errorMessage = "Lengkapi Email dan Nama Lengkap yang valid untuk masuk."
                      return@Button
                    }
                    onPrepareDraft(
                      PendingProfileDraft(
                        isRegisterMode = false,
                        enteredEmail = loginEmail.trim(),
                        fullName = loginFullName.trim(),
                      )
                    )
                  } else {
                    attemptedRegister = true
                    if (!isRegisterFormValid) {
                      errorMessage = "Lengkapi semua data diri wajib sebelum daftar."
                      return@Button
                    }
                    onPrepareDraft(
                      PendingProfileDraft(
                        isRegisterMode = true,
                        enteredEmail = regEmail.trim(),
                        fullName = regFullName.trim(),
                        username = regUsername.trim(),
                        phone = regPhone.trim(),
                        city = regCity.trim(),
                        bio = regBio.trim(),
                        pinkStyle = regPinkStyle,
                      )
                    )
                  }

                  isLoading = true
                  onGoogleSignInClicked(
                    context = context,
                    credentialManager = credentialManager,
                    onAuthSuccess = {
                      isLoading = false
                      onAuthSuccess()
                    },
                    onAuthError = { msg ->
                      isLoading = false
                      errorMessage = msg
                    },
                    scope = scope,
                    onAuthCancelled = { isLoading = false },
                  )
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(18.dp),
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                  ),
                modifier =
                  Modifier
                    .fillMaxWidth()
                    .heightIn(min = 54.dp)
                    .testTag(
                      if (authMode == AuthMode.LOGIN) "login_google_button"
                      else "register_google_button"
                    ),
              ) {
                if (isLoading) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.5.dp,
                  )
                  Spacer(modifier = Modifier.width(12.dp))
                  Text(
                    text = "Menghubungkan Firebase…",
                    style = MaterialTheme.typography.labelLarge,
                  )
                } else {
                  Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text =
                      if (authMode == AuthMode.LOGIN) {
                        stringResource(R.string.auth_button_login_google)
                      } else {
                        stringResource(R.string.auth_button_register_google)
                      },
                    style = MaterialTheme.typography.labelLarge,
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth(),
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.Top,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp).padding(top = 2.dp),
                  )
                  Text(
                    text = stringResource(R.string.auth_email_provider_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Icon(
                  imageVector = Icons.Rounded.VerifiedUser,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp),
                )
                Text(
                  text = stringResource(R.string.auth_security_note),
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    }
  }
}

@Composable
private fun AuthModeTab(
  selected: Boolean,
  label: String,
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val containerColor =
    if (selected) MaterialTheme.colorScheme.surfaceContainerLowest else Color.Transparent
  val contentColor =
    if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant

  Surface(
    color = containerColor,
    shape = RoundedCornerShape(20.dp),
    shadowElevation = if (selected) 2.dp else 0.dp,
    modifier =
      modifier
        .minimumInteractiveComponentSize()
        .clip(RoundedCornerShape(20.dp))
        .clickable(onClick = onClick),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 12.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = contentColor,
        modifier = Modifier.size(18.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = contentColor,
      )
    }
  }
}
