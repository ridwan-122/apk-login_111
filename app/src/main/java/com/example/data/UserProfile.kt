package com.example.data

import androidx.annotation.Keep
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StyleBerryEnd
import com.example.ui.theme.StyleBerryStart
import com.example.ui.theme.StyleCoralEnd
import com.example.ui.theme.StyleCoralStart
import com.example.ui.theme.StyleRoseQuartzEnd
import com.example.ui.theme.StyleRoseQuartzStart
import com.example.ui.theme.StyleSakuraEnd
import com.example.ui.theme.StyleSakuraStart
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class PinkStyle(
  val label: String,
  val tagline: String,
  val startColor: Color,
  val endColor: Color,
) {
  ROSE_QUARTZ(
    label = "Rose Quartz",
    tagline = "Klasik elegan",
    startColor = StyleRoseQuartzStart,
    endColor = StyleRoseQuartzEnd,
  ),
  SAKURA_SOFT(
    label = "Sakura Soft",
    tagline = "Lembut & manis",
    startColor = StyleSakuraStart,
    endColor = StyleSakuraEnd,
  ),
  BERRY_GLOW(
    label = "Berry Glow",
    tagline = "Berani & cerah",
    startColor = StyleBerryStart,
    endColor = StyleBerryEnd,
  ),
  CORAL_BLUSH(
    label = "Coral Blush",
    tagline = "Hangat & segar",
    startColor = StyleCoralStart,
    endColor = StyleCoralEnd,
  );

  companion object {
    fun fromName(raw: String?): PinkStyle {
      return entries.firstOrNull { it.name == raw } ?: ROSE_QUARTZ
    }
  }
}

@Keep
@IgnoreExtraProperties
data class UserProfile(
  @DocumentId val id: String = "",
  val userId: String = "",
  val email: String = "",
  val fullName: String = "",
  val username: String = "",
  val pinkStyle: String = PinkStyle.ROSE_QUARTZ.name,
  val bio: String = "",
  val photoUrl: String? = null,
  val loginCount: Long = 1L,
  @ServerTimestamp val lastLoginAt: Timestamp? = null,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
) {
  fun resolvedPinkStyle(): PinkStyle = PinkStyle.fromName(pinkStyle)
}

@Keep
@IgnoreExtraProperties
data class LoginLog(
  @DocumentId val id: String = "",
  val userId: String = "",
  val provider: String = "google.com",
  val email: String = "",
  val deviceLabel: String = "",
  @ServerTimestamp val loginAt: Timestamp? = null,
)

object ProfileValidation {
  private val USERNAME_REGEX = Regex("^[a-zA-Z0-9_.]+$")

  fun isValidFullName(value: String): Boolean {
    val trimmed = value.trim()
    return trimmed.length in 2..60
  }

  fun isValidUsername(value: String): Boolean {
    val trimmed = value.trim()
    return trimmed.length in 3..30 && USERNAME_REGEX.matches(trimmed)
  }

  fun isValidBio(value: String): Boolean {
    return value.trim().length <= 160
  }

  fun suggestUsername(displayName: String?, email: String?): String {
    val raw =
      when {
        !email.isNullOrBlank() -> email.substringBefore("@")
        !displayName.isNullOrBlank() -> displayName
        else -> "blush.user"
      }
    val cleaned =
      raw
        .lowercase()
        .replace(Regex("[^a-z0-9_.]"), "_")
        .replace(Regex("_+"), "_")
        .trim('_', '.')
        .take(24)
    return if (cleaned.length >= 3) cleaned else "blush_${cleaned.ifEmpty { "member" }}"
  }
}
