package com.mr0xf00.lensgenerator

import androidx.compose.ui.unit.IntSize
import java.util.Date

@GenerateLens
sealed interface Entry {
    val name: String
    companion object
}

@GenerateLens
data class AudioEntry(override val name: String, val lengthMS: Long) : Entry {
    companion object
}

@GenerateLens
data class ImageEntry(override val name: String, val size: IntSize) : Entry {
    companion object
}

@GenerateLens
data class UserEntry(override val name: String, val photoUrl: String) : Entry {
    companion object
}
