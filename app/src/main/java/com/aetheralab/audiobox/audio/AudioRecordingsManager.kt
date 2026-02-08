package com.aetheralab.audiobox.audio

import android.content.Context
import com.aetheralab.audiobox.audio.core.AudioStorageWriter
import com.aetheralab.audiobox.audio.core.RecordingCatalog
import com.aetheralab.audiobox.audio.core.RecordingWriterManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class AudioRecordingsManager(private val context: Context) : RecordingWriterManager,
    RecordingCatalog {

    private val _recordings = MutableStateFlow<List<String>>(emptyList())
    override val recordings: StateFlow<List<String>> = _recordings

    init {
        refreshRecordings()
    }

    override fun createRecordingWriter(): AudioStorageWriter {
        val fileName = "audio_${System.currentTimeMillis()}.pcm"
        val file = File(context.filesDir, fileName)
        return FileAudioStorageWriter(file) { refreshRecordings() }
    }

    override fun deleteRecording(id: String): Boolean {
        val file = File(context.filesDir, id)
        return file.exists() && file.delete()
    }

    private fun refreshRecordings() {
        _recordings.value =
            context.filesDir
                .listFiles { f -> f.extension == "pcm" }
                ?.map { it.name }
                ?: emptyList()
    }

}