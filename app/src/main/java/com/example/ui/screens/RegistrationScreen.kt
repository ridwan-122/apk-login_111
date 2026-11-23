package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.PinkStyle
import com.example.data.ProfileValidation
import com.example.data.UserProfile

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegistrationScreen(
  email: String,
  initialFullName: String,
  initialUsername: String,
  initialBio: String,
  initialPinkStyle: PinkStyle,
  photoUrl: String?,
  isEditMode: Boolean,
  isSubmitting: Boolean,
  submitError: String?,
  onSubmit: (fullName: String, username: String, pinkStyle: PinkStyle, bio: String) -> Unit,
  onCancelEdit: () -> Unit,
  onSignOut: () -> Unit,
  modifier: Modifier = Modifier,
) {
  if (isEditMode) {
    BackHandler(enabled = !isSubmitting) { onCancelEdit() }
  }

  var fullName by rememberSaveable(initialFullName) { mutableStateOf(initialFullName) }
  var username by rememberSaveable(initialUsername) { mutableStateOf(initialUsername) }
  var bio by rememberSaveable(initialBio) { mutableStateOf(initialBio) }
  var selectedStyleName by rememberSaveable(initialPinkStyle.name) {
    mutableStateOf(initialPinkStyle.name)
  }
  var attemptedSubmit by rememberSaveable { mutableStateOf(false) }

  val selectedStyle = PinkStyle.fromName(selectedStyleName)
  val isFullNameValid = ProfileValidation.isValidFullName(fullName)
  val isUsernameValid = ProfileValidation.isValidUsername(username)
  val isBioValid = ProfileValidation.isValidBio(bio)
  val canSubmit = isFullNameValid && isUsernameValid && isBioValid && !isSubmitting

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
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Column(modifier = Modifier.fillMaxWidth().widthIn(max = 560.dp)) {
          // Top Action Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            if (isEditMode) {
              IconButton(
                onClick = onCancelEdit,
                enabled = !isSubmitting,
                modifier = Modifier.minimumInteractiveComponentSize().testTag("back_button"),
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                  contentDescription = stringResource(R.string.cd_back),
                  tint = MaterialTheme.colorScheme.onSurface,
                )
              }
            } else {
              Spacer(modifier = Modifier.size(48.dp))
            }

            TextButton(
              onClick = onSignOut,
              enabled = !isSubmitting,
              modifier =
                Modifier.minimumInteractiveComponentSize().testTag("reg_sign_out_button"),
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.Logout,
                contentDescription = stringResource(R.string.cd_sign_out),
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = stringResource(R.string.welcome_button_sign_out),
                style = MaterialTheme.typography.labelLarge,
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Header + Google Avatar
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
          ) {
            AsyncImage(
              model = photoUrl,
              contentDescription = stringResource(R.string.cd_user_avatar),
              placeholder = painterResource(id = R.drawable.img_avatar_default),
              error = painterResource(id = R.drawable.img_avatar_default),
              fallback = painterResource(id = R.drawable.img_avatar_default),
              contentScale = ContentScale.Crop,
              modifier =
                Modifier
                  .size(64.dp)
                  .clip(CircleShape)
                  .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            )
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text =
                  if (isEditMode) stringResource(R.string.reg_edit_header_title)
                  else stringResource(R.string.reg_header_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = stringResource(R.string.reg_header_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Verified Google Email Chip
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Rounded.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
              )
              Text(
                text = email,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Form Card
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
              modifier = Modifier.fillMaxWidth().padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
              val fieldColors =
                OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = MaterialTheme.colorScheme.primary,
                  focusedLabelColor = MaterialTheme.colorScheme.primary,
                  cursorColor = MaterialTheme.colorScheme.primary,
                )

              OutlinedTextField(
                value = fullName,
                onValueChange = { if (it.length <= 60) fullName = it },
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
                isError = (attemptedSubmit || fullName.isNotEmpty()) && !isFullNameValid,
                supportingText = {
                  if ((attemptedSubmit || fullName.isNotEmpty()) && !isFullNameValid) {
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
                modifier = Modifier.fillMaxWidth().testTag("full_name_input"),
              )

              OutlinedTextField(
                value = username,
                onValueChange = { raw ->
                  val filtered = raw.filter { it.isLetterOrDigit() || it == '_' || it == '.' }
                  if (filtered.length <= 30) username = filtered
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
                isError = (attemptedSubmit || username.isNotEmpty()) && !isUsernameValid,
                supportingText = {
                  if ((attemptedSubmit || username.isNotEmpty()) && !isUsernameValid) {
                    Text(stringResource(R.string.reg_error_username))
                  }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                shape = MaterialTheme.shapes.medium,
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth().testTag("username_input"),
              )

              OutlinedTextField(
                value = bio,
                onValueChange = { if (it.length <= 160) bio = it },
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
                isError = !isBioValid,
                supportingText = {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                  ) {
                    Text(
                      text =
                        if (!isBioValid) stringResource(R.string.reg_error_bio) else ""
                    )
                    Text(text = "${bio.trim().length}/160")
                  }
                },
                shape = MaterialTheme.shapes.medium,
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth().testTag("bio_input"),
              )

              // Pink Style Selector
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                  )
                  Text(
                    text = stringResource(R.string.reg_label_pink_style),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                  )
                }

                FlowRow(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                  verticalArrangement = Arrangement.spacedBy(10.dp),
                  maxItemsInEachRow = 2,
                ) {
                  PinkStyle.entries.forEach { styleOption ->
                    val isSelected = styleOption == selectedStyle
                    PinkStyleOptionCard(
                      styleOption = styleOption,
                      selected = isSelected,
                      onClick = { selectedStyleName = styleOption.name },
                      modifier =
                        Modifier
                          .weight(1f)
                          .testTag("pink_style_${styleOption.name}"),
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          AnimatedVisibility(visible = submitError != null) {
            Surface(
              color = MaterialTheme.colorScheme.errorContainer,
              shape = MaterialTheme.shapes.small,
              modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            ) {
              Text(
                text = submitError.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(12.dp),
              )
            }
          }

          Button(
            onClick = {
              attemptedSubmit = true
              if (canSubmit) {
                onSubmit(fullName, username, selectedStyle, bio)
              }
            },
            enabled = !isSubmitting,
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
                .testTag("submit_registration_button"),
          ) {
            if (isSubmitting) {
              CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.5.dp,
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Menyimpan…",
                style = MaterialTheme.typography.labelLarge,
              )
            } else {
              Text(
                text =
                  if (isEditMode) stringResource(R.string.reg_button_save)
                  else stringResource(R.string.reg_button_submit),
                style = MaterialTheme.typography.labelLarge,
              )
            }
          }

          if (isEditMode) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
              onClick = onCancelEdit,
              enabled = !isSubmitting,
              shape = RoundedCornerShape(18.dp),
              modifier =
                Modifier
                  .fillMaxWidth()
                  .heightIn(min = 48.dp)
                  .testTag("cancel_edit_button"),
            ) {
              Text(
                text = stringResource(R.string.reg_button_cancel),
                style = MaterialTheme.typography.labelLarge,
              )
            }
          }

          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }
}

@Composable
private fun PinkStyleOptionCard(
  styleOption: PinkStyle,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val borderColor =
    if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
  val containerColor =
    if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
    else MaterialTheme.colorScheme.surfaceContainerLow

  Surface(
    color = containerColor,
    shape = MaterialTheme.shapes.medium,
    border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
    modifier =
      modifier
        .minimumInteractiveComponentSize()
        .clip(MaterialTheme.shapes.medium)
        .clickable(onClick = onClick),
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Box(
        modifier =
          Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                colors = listOf(styleOption.startColor, styleOption.endColor)
              )
            ),
        contentAlignment = Alignment.Center,
      ) {
        if (selected) {
          Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp),
          )
        }
      }
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = styleOption.label,
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface,
          fontWeight = FontWeight.SemiBold,
        )
        Text(
          text = styleOption.tagline,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}
