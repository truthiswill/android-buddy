package com.likethesalad.android.common.models

data class AndroidBuddyLibraryInfo(
    val id: String,
    val group: String,
    val name: String,
    val pluginNames: Set<String>
)
