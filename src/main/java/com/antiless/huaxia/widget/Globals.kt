package com.antiless.huaxia.widget

import android.util.Log

var DEBUG = BuildConfig.DEBUG
fun debuglog(text: String) {
    if (DEBUG) Log.i("Globals.debuglog", text)
}

fun Char.isPunct(): Boolean {
    return toString().contains(Regex("[\\p{Punct}]"))
}