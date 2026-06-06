package com.unreal.medisageai.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Screen2 — Create Account.
 *
 * Registration form (email, password, confirm-password) with live criteria matching: the Sign Up
 * CTA stays disabled until the email is present, the password is at least 8 characters, and both
 * password fields match. Includes the legal disclaimer and a "Back to Login" anchor.
 */
@Composable
fun CreateAccountScreen(
    onSignUpClick: () -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }

    val passwordLongEnough = password.length >= 8
    val passwordsMismatch = confirm.isNotEmpty() && password != confirm
    val formValid = email.contains("@") && passwordLongEnough && password == confirm && confirm.isNotEmpty()

    AuthBackground(modifier = modifier) {
        Spacer(Modifier.height(40.dp))
        BrandHeader(compact = true)
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Create your account",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                AuthTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    placeholder = "name@medical-institution.org",
                    leadingIcon = Icons.Outlined.MailOutline,
                    keyboardType = KeyboardType.Email,
                )
                Spacer(Modifier.height(16.dp))
                AuthTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Min. 8 characters",
                    leadingIcon = Icons.Outlined.Lock,
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                )
                Spacer(Modifier.height(16.dp))
                AuthTextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    label = "Confirm Password",
                    placeholder = "Repeat your password",
                    leadingIcon = Icons.Outlined.VerifiedUser,
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                    isError = passwordsMismatch,
                )
                if (passwordsMismatch) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Passwords do not match",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Spacer(Modifier.height(16.dp))
                LegalDisclaimer()
                Spacer(Modifier.height(16.dp))
                PrimaryCtaButton(
                    text = "Sign Up",
                    onClick = onSignUpClick,
                    enabled = formValid,
                )

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = onBackToLogin,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Login,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(Modifier.height(0.dp))
                        Text(
                            text = "  Back to Login",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
