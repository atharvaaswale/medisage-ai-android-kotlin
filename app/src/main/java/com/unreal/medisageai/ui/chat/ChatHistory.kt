package com.unreal.medisageai.ui.chat

import com.unreal.medisageai.data.ChatMessage
import com.unreal.medisageai.data.Sender

/** A single (mocked) past conversation surfaced in the navigation drawer's Recent History. */
data class ChatHistoryItem(
    val id: String,
    val title: String,
    val dateLabel: String,
    val transcript: List<ChatMessage>,
)

private fun ai(text: String, timeLabel: String? = null, suggestions: List<String> = emptyList()) =
    ChatMessage(text = text, sender = Sender.AI, timeLabel = timeLabel, suggestions = suggestions)

private fun user(text: String, timeLabel: String? = null) =
    ChatMessage(text = text, sender = Sender.USER, timeLabel = timeLabel)

/**
 * Inline-mocked Recent History. Static so message ids stay stable across drawer taps.
 * "Lab results analysis" mirrors the Screen3.0 transcript exactly — including the red critical
 * value (`[[crit]]…[[/crit]]`) and the follow-up suggestion chips.
 */
val mockChatHistory: List<ChatHistoryItem> = listOf(
    ChatHistoryItem(
        id = "fever",
        title = "What is fever?",
        dateLabel = "Today, 10:24 AM",
        transcript = listOf(
            ai(DR_GREETING),
            user("What is fever?"),
            ai(
                "Fever is a temporary rise in body temperature, usually above " +
                    "**38°C (100.4°F)**, most often triggered by infection as part of the body's " +
                    "immune response.\n\n" +
                    "**General guidance:**\n" +
                    "• Encourage rest and hydration\n" +
                    "• Antipyretics (e.g. paracetamol) per dosing guidance\n" +
                    "• Escalate if it persists beyond 3 days or exceeds **39.4°C (103°F)**\n\n" +
                    "*Always correlate with the full clinical picture.*",
            ),
        ),
    ),
    ChatHistoryItem(
        id = "antibiotic",
        title = "Antibiotic dosage",
        dateLabel = "Yesterday",
        transcript = listOf(
            ai(DR_GREETING),
            user("What's the standard amoxicillin dosage for an adult respiratory infection?"),
            ai(
                "For uncomplicated adult respiratory infections a common regimen is " +
                    "**500 mg every 8 hours** (or 875 mg every 12 hours) for 5–7 days.\n\n" +
                    "*Adjust for renal function and confirm against local antimicrobial " +
                    "stewardship guidelines.*",
            ),
        ),
    ),
    ChatHistoryItem(
        id = "labs",
        title = "Lab results analysis",
        dateLabel = "Oct 24, 2023",
        transcript = listOf(
            ai(DR_GREETING, timeLabel = "09:41 AM"),
            user(
                "I need to review the recent lab results for Patient ID: #88392. " +
                    "There are concerns about their potassium levels.",
                timeLabel = "09:42 AM",
            ),
            ai(
                "Understood. Retrieving lab results for Patient #88392. Here are the most " +
                    "recent findings regarding electrolytes:\n\n" +
                    "**Lab Results for Patient #88392:**\n\n" +
                    "• Potassium (K+): [[crit]]5.8 mmol/L (Critical)[[/crit]]\n" +
                    "• Sodium (Na+): 138 mmol/L\n\n" +
                    "**Clinical Impression:**\n" +
                    "*Hyperkalemia noted. Suggest immediate clinical correlation and review of " +
                    "medication history (ACE inhibitors/ARBs).*",
                timeLabel = "09:43 AM",
                suggestions = listOf("Medication History", "ECG Protocol"),
            ),
        ),
    ),
    ChatHistoryItem(
        id = "cough",
        title = "Chronic cough follow-up",
        dateLabel = "Oct 22, 2023",
        transcript = listOf(
            ai(DR_GREETING),
            user("Patient has had a persistent cough for 6 weeks. What should I consider?"),
            ai(
                "A cough lasting **>8 weeks** is classified as chronic; at 6 weeks it is " +
                    "subacute. Common considerations:\n\n" +
                    "• Post-infectious cough\n" +
                    "• Asthma / cough-variant asthma\n" +
                    "• GERD\n" +
                    "• Upper airway cough syndrome (post-nasal drip)\n\n" +
                    "*Consider chest imaging if red-flag features (weight loss, hemoptysis, " +
                    "significant smoking history) are present.*",
            ),
        ),
    ),
)
