package com.alexkolydas.mygarage.core.ui.util

import com.alexkolydas.mygarage.core.ui.model.UrgencyUi
import com.alexkolydas.mygarage.core.ui.theme.*
import java.util.Locale

val URGENCY_NONE   = UrgencyUi("none",   "No service set", UrgencyNoneFg,   UrgencyNoneBg,   null)
val URGENCY_LOGGED = UrgencyUi("logged", "Logged",         UrgencyLoggedFg, UrgencyLoggedBg, null)

fun computeUrgency(curKm: Int, nextKm: Int?): UrgencyUi {
    if (nextKm == null) return URGENCY_NONE
    val rem = nextKm - curKm
    return when {
        rem <= 0    -> UrgencyUi("overdue", "Overdue",      UrgencyOverdueFg, UrgencyOverdueBg, rem)
        rem <= 1000 -> UrgencyUi("soon",    "Service soon", UrgencySoonFg,    UrgencySoonBg,    rem)
        else        -> UrgencyUi("ok",      "Up to date",   UrgencyOkFg,      UrgencyOkBg,      rem)
    }
}

fun statusText(urgency: UrgencyUi): String = when (urgency.key) {
    "none"    -> "No service scheduled"
    "overdue" -> "Overdue by ${String.format(Locale.US, "%,d", Math.abs(urgency.remaining!!))} km"
    else      -> "${String.format(Locale.US, "%,d", urgency.remaining!!)} km to go"
}

fun Int.formatKm(): String = String.format(Locale.US, "%,d", this)
