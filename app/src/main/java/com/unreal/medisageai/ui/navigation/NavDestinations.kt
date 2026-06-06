package com.unreal.medisageai.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Compile-time type-safe navigation destinations for MediSage AI.
 *
 * Each route maps to one high-fidelity screen target. Using [Serializable] objects/classes
 * (Navigation-Compose type safety) means the compiler — not strings — guarantees that every
 * `navigate(...)` call and `composable<...>` handler line up, and arguments are passed as real
 * Kotlin types instead of stringly-typed bundles.
 */

/** Screen1 — Login. The graph's start destination. */
@Serializable
object Login

/** Screen2 — Create Account. */
@Serializable
object CreateAccount

/**
 * Screen3.0 — Chat (and its Screen3.1 modal navigation drawer).
 *
 * @param sessionId optional id of a history session to restore on entry. `null` opens a fresh chat.
 */
@Serializable
data class ActiveChat(val sessionId: String? = null)
