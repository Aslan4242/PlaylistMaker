package com.example.playlistmaker.search.presentation.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun toastTrackText(trackCount: Int): String {
    val preLastDigit = trackCount % 100 / 10
    val trackText = if (preLastDigit == 1) {
        "ов"
    } else when (trackCount % 10) {
        1 -> ""
        2, 3, 4 -> "а"
        else -> "ов"
    }
    return "$trackCount трек$trackText"
}

fun toastMinuteText(count: Long): String {
    val preLastDigit = count % 100 / 10
    val minuteText = if (preLastDigit == 1L) {
        ""
    } else when (count % 10) {
        1L -> "а"
        2L, 3L, 4L -> "ы"
        else -> ""
    }
    return "$count минут$minuteText"
}

fun <T> debounce(delayMillis: Long,
                 coroutineScope: CoroutineScope,
                 useLastParam: Boolean,
                 action: (T) -> Unit): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        if (useLastParam) {
            debounceJob?.cancel()
        }
        if (debounceJob?.isCompleted != false || useLastParam) {
            debounceJob = coroutineScope.launch {
                delay(delayMillis)
                action(param)
            }
        }
    }
}