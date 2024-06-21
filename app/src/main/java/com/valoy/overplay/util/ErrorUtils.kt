package com.valoy.overplay.util

import android.util.Log
import kotlinx.coroutines.CancellationException

suspend fun tryCatch(block: suspend () -> Unit) {
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e("Exception", "Error occurred: ${e.message}", e)
    }
}