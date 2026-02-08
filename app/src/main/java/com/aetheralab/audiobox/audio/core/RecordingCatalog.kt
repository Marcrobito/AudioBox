package com.aetheralab.audiobox.audio.core

import kotlinx.coroutines.flow.StateFlow

interface RecordingCatalog {
    val recordings: StateFlow<List<String>>
    fun deleteRecording(id: String): Boolean
}