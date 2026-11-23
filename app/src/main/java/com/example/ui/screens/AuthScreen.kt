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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.HowToReg
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.auth.attemptAutoSignIn
import com.example.auth.onGoogleSignInClicked
import com.example.ui.theme.StyleBerryEnd
import com.example.ui.theme.StyleRoseQuartzStart

private enum class AuthMode {
  LOGIN,
  REGISTER,
}

@Composable
fun AuthScreen(
  onAuthSuccess: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val credentialManager = remember(context) { CredentialManager.create(context) }

  var authMode by rememberSaveable { mutableStateOf(AuthMode.LOGIN) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(Unit) {
    attemptAutoSignIn(
      context = context,
      credentialManager = credentialManager,
      onAuthSuccess = onAuthSuccess,
      onUnauthenticated = {},
      scope = scope,
    )
  }

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

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = Color.Transparent,
    contentWindowInsets = WindowInsets.safeDrawing,
  ) { innerPadding ->
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .background(bgBrush)
    ) {
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
            .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
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

          Spacer(modifier = Modifier.height(20.dp))

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
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
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
                modifier = Modifier.align(Alignment.BottomStart).padding(14.dp),
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
                    text = "Tema Pink Eksklusif",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

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

          // Main Auth Action Card
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
              modifier = Modifier.fillMaxWidth().padding(24.dp),
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
                ) {
                  Text(
                    text =
                      if (mode == AuthMode.LOGIN) stringResource(R.string.auth_login_title)
                      else stringResource(R.string.auth_register_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text =
                      if (mode == AuthMode.LOGIN) stringResource(R.string.auth_login_subtitle)
                      else stringResource(R.string.auth_register_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                  )

                  if (mode == AuthMode.REGISTER) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                      color = MaterialTheme.colorScheme.surfaceContainerLow,
                      shape = MaterialTheme.shapes.medium,
                      modifier = Modifier.fillMaxWidth(),
                    ) {
                      Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                      ) {
                        RegistrationStepRow("1", "Verifikasi cepat dengan akun Google")
                        RegistrationStepRow("2", "Isi nama, username & gaya pink favorit")
                        RegistrationStepRow("3", "Masuk ke halaman Selamat Datang")
                      }
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(20.dp))

              AnimatedVisibility(visible = errorMessage != null) {
                Surface(
                  color = MaterialTheme.colorScheme.errorContainer,
                  shape = MaterialTheme.shapes.small,
                  modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                ) {
                  Text(
                    text = errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                  )
                }
              }

              // Primary Google Sign-In CTA
              Button(
                onClick = {
                  errorMessage = null
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
                    text = "Menghubungkan…",
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

              Spacer(modifier = Modifier.height(14.dp))

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
        }
      }
    }
  }
}

@Composable
private fun AuthModeTab(
  selected: Boolean,
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
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

@Composable
private fun RegistrationStepRow(stepNumber: String, text: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Box(
      modifier =
        Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(colors = listOf(StyleRoseQuartzStart, StyleBerryEnd))
          ),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = stepNumber,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        fontWeight = FontWeight.Bold,
      )
    }
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurface,
    )
  }
}
