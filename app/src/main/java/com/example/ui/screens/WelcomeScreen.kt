package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.LoginLog
import com.example.data.UserProfile
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
  profile: UserProfile,
  loginLogs: List<LoginLog>,
  showSaveSuccess: Boolean,
  onDismissSaveSuccess: () -> Unit,
  onEditProfile: () -> Unit,
  onSignOut: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val pinkStyle = profile.resolvedPinkStyle()
  val indonesiaLocale = remember { Locale.forLanguageTag("id-ID") }
  val formattedDate = remember(profile.createdAt, profile.updatedAt, indonesiaLocale) {
    val ts = profile.createdAt ?: profile.updatedAt ?: Timestamp.now()
    SimpleDateFormat("dd MMM yyyy", indonesiaLocale).format(ts.toDate())
  }
  val formattedLastLogin = remember(profile.lastLoginAt, profile.updatedAt, indonesiaLocale) {
    val ts = profile.lastLoginAt ?: profile.updatedAt ?: profile.createdAt ?: Timestamp.now()
    SimpleDateFormat("dd MMM yyyy, HH:mm", indonesiaLocale).format(ts.toDate())
  }

  LaunchedEffect(showSaveSuccess) {
    if (showSaveSuccess) {
      delay(3500)
      onDismissSaveSuccess()
    }
  }

  val bgBrush =
    Brush.verticalGradient(
      colors =
        listOf(
          MaterialTheme.colorScheme.surfaceContainerLow,
          MaterialTheme.colorScheme.background,
          MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        )
    )

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = Color.Transparent,
    contentWindowInsets = WindowInsets.safeDrawing,
  ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize().background(bgBrush)) {
      Column(
        modifier =
          Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().widthIn(max = 560.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          // Top Brand Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Surface(
              color = MaterialTheme.colorScheme.primaryContainer,
              shape = CircleShape,
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Favorite,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp),
                )
                Text(
                  text = stringResource(R.string.app_name),
                  style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  fontWeight = FontWeight.Bold,
                )
              }
            }

            IconButton(
              onClick = onSignOut,
              modifier =
                Modifier.minimumInteractiveComponentSize().testTag("top_sign_out_button"),
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.Logout,
                contentDescription = stringResource(R.string.cd_sign_out),
                tint = MaterialTheme.colorScheme.primary,
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          AnimatedVisibility(visible = showSaveSuccess) {
            Surface(
              color = MaterialTheme.colorScheme.primaryContainer,
              shape = MaterialTheme.shapes.medium,
              border =
                BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
              modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Icon(
                  imageVector = Icons.Rounded.CheckCircle,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp),
                )
                Text(
                  text = stringResource(R.string.welcome_save_success),
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  fontWeight = FontWeight.Medium,
                )
              }
            }
          }

          // Hero Welcome Banner with Overlapping Avatar
          Box(modifier = Modifier.fillMaxWidth()) {
            Card(
              shape = MaterialTheme.shapes.large,
              elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                Image(
                  painter = painterResource(id = R.drawable.img_welcome_banner),
                  contentDescription = stringResource(R.string.cd_welcome_banner),
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize(),
                )
                Surface(
                  color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.92f),
                  shape = CircleShape,
                  modifier = Modifier.align(Alignment.TopEnd).padding(14.dp),
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.Verified,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(16.dp),
                    )
                    Text(
                      text = stringResource(R.string.welcome_badge_verified),
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurface,
                    )
                  }
                }
              }
            }

            AsyncImage(
              model = profile.photoUrl,
              contentDescription = stringResource(R.string.cd_user_avatar),
              placeholder = painterResource(id = R.drawable.img_avatar_default),
              error = painterResource(id = R.drawable.img_avatar_default),
              fallback = painterResource(id = R.drawable.img_avatar_default),
              contentScale = ContentScale.Crop,
              modifier =
                Modifier
                  .align(Alignment.BottomCenter)
                  .offset(y = 42.dp)
                  .size(88.dp)
                  .clip(CircleShape)
                  .border(4.dp, MaterialTheme.colorScheme.surfaceContainerLowest, CircleShape),
            )
          }

          Spacer(modifier = Modifier.height(52.dp))

          // Greeting Headline
          Text(
            text = stringResource(R.string.welcome_title_format, profile.fullName),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("welcome_greeting_text"),
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
          )

          Spacer(modifier = Modifier.height(22.dp))

          // Personalized Pink Gradient Member Card
          Card(
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Box(
              modifier =
                Modifier
                  .fillMaxWidth()
                  .background(
                    Brush.linearGradient(
                      colors = listOf(pinkStyle.startColor, pinkStyle.endColor)
                    )
                  )
                  .padding(22.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Text(
                    text = stringResource(R.string.welcome_card_member),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.9f),
                  )
                  Surface(
                    color = Color.White.copy(alpha = 0.22f),
                    shape = CircleShape,
                  ) {
                    Text(
                      text = pinkStyle.label,
                      style = MaterialTheme.typography.labelMedium,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    )
                  }
                }

                Text(
                  text = profile.fullName,
                  style = MaterialTheme.typography.headlineMedium,
                  color = Color.White,
                )
                Text(
                  text = "@${profile.username}",
                  style = MaterialTheme.typography.titleSmall,
                  color = Color.White.copy(alpha = 0.92f),
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Profile Details Card
          Card(
            shape = MaterialTheme.shapes.large,
            colors =
              CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
              ),
            border =
              BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
              ProfileDetailRow(
                icon = Icons.Rounded.AlternateEmail,
                label = stringResource(R.string.welcome_label_username),
                value = "@${profile.username}",
              )
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
              ProfileDetailRow(
                icon = Icons.Rounded.Email,
                label = stringResource(R.string.welcome_label_email),
                value = profile.email,
              )
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
              ProfileDetailRow(
                icon = Icons.Rounded.Phone,
                label = stringResource(R.string.welcome_label_phone),
                value = profile.phone.ifBlank { "-" },
              )
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
              ProfileDetailRow(
                icon = Icons.Rounded.LocationCity,
                label = stringResource(R.string.welcome_label_city),
                value = profile.city.ifBlank { "-" },
              )
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
              ProfileDetailRow(
                icon = Icons.Rounded.Palette,
                label = stringResource(R.string.welcome_label_style),
                value = "${pinkStyle.label} • ${pinkStyle.tagline}",
              )
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
              ProfileDetailRow(
                icon = Icons.Rounded.CalendarToday,
                label = stringResource(R.string.welcome_label_joined),
                value = formattedDate,
              )
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                  text = stringResource(R.string.welcome_label_bio),
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                  text =
                    profile.bio.ifBlank { stringResource(R.string.welcome_empty_bio) },
                  style = MaterialTheme.typography.bodyMedium,
                  color =
                    if (profile.bio.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Firebase Auth & Firestore Login Session Card
          Card(
            shape = MaterialTheme.shapes.large,
            colors =
              CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
              ),
            border =
              BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth().testTag("firebase_auth_status_card"),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                  )
                  Text(
                    text = stringResource(R.string.welcome_auth_card_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                  )
                }

                Surface(
                  color = MaterialTheme.colorScheme.primaryContainer,
                  shape = CircleShape,
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.CloudDone,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(14.dp),
                    )
                    Text(
                      text = stringResource(R.string.welcome_auth_badge_connected),
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                  }
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Surface(
                  color = MaterialTheme.colorScheme.surfaceContainerLow,
                  shape = MaterialTheme.shapes.medium,
                  modifier = Modifier.weight(1f),
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                      text = stringResource(R.string.welcome_label_total_logins),
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = "${maxOf(1L, profile.loginCount)}x",
                      style = MaterialTheme.typography.titleLarge,
                      color = MaterialTheme.colorScheme.primary,
                      fontWeight = FontWeight.Bold,
                    )
                  }
                }
                Surface(
                  color = MaterialTheme.colorScheme.surfaceContainerLow,
                  shape = MaterialTheme.shapes.medium,
                  modifier = Modifier.weight(1.4f),
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                      text = stringResource(R.string.welcome_label_last_login),
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = formattedLastLogin,
                      style = MaterialTheme.typography.titleSmall,
                      color = MaterialTheme.colorScheme.onSurface,
                    )
                  }
                }
              }

              ProfileDetailRow(
                icon = Icons.Rounded.Verified,
                label = stringResource(R.string.welcome_label_provider),
                value = stringResource(R.string.welcome_value_provider),
              )

              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                  )
                  Text(
                    text = stringResource(R.string.welcome_label_recent_sessions),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }

                if (loginLogs.isEmpty()) {
                  Text(
                    text = stringResource(R.string.welcome_empty_logs),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                } else {
                  loginLogs.forEach { log ->
                    val logTime =
                      remember(log.loginAt, indonesiaLocale) {
                        val ts = log.loginAt ?: Timestamp.now()
                        SimpleDateFormat("dd MMM yyyy • HH:mm", indonesiaLocale)
                          .format(ts.toDate())
                      }
                    Surface(
                      color = MaterialTheme.colorScheme.surfaceContainerLow,
                      shape = MaterialTheme.shapes.small,
                      modifier = Modifier.fillMaxWidth(),
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                      ) {
                        Column(modifier = Modifier.weight(1f)) {
                          Text(
                            text = log.deviceLabel.ifBlank { "Android • Blush App" },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                          )
                          Text(
                            text = log.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                          )
                        }
                        Text(
                          text = logTime,
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.primary,
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Action Buttons
          FilledTonalButton(
            onClick = onEditProfile,
            shape = RoundedCornerShape(18.dp),
            colors =
              ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
              ),
            modifier =
              Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .testTag("edit_profile_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Edit,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = stringResource(R.string.welcome_button_edit_profile),
              style = MaterialTheme.typography.labelLarge,
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedButton(
            onClick = onSignOut,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
            modifier =
              Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .testTag("sign_out_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.Logout,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = stringResource(R.string.welcome_button_sign_out),
              style = MaterialTheme.typography.labelLarge,
            )
          }

          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }
}

@Composable
private fun ProfileDetailRow(
  icon: ImageVector,
  label: String,
  value: String,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Surface(
      color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
      shape = CircleShape,
      modifier = Modifier.size(36.dp),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp),
        )
      }
    }
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Medium,
      )
    }
  }
}
