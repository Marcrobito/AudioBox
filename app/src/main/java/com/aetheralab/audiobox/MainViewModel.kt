package com.aetheralab.audiobox

import androidx.lifecycle.ViewModel
import com.aetheralab.audiobox.audio.core.AudioPlayer
import com.aetheralab.audiobox.audio.core.AudioRecorder
import com.aetheralab.audiobox.audio.core.RecordingCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val recorder: AudioRecorder,
    private val player: AudioPlayer,
    private val catalog: RecordingCatalog
) : ViewModel() {

    // ---- Recording ----

    fun startRecording() {
        recorder.startRecording()
    }

    fun stopRecording() {
        recorder.stopRecording()
    }

    // ---- Playback state ----

    private var currentlyPlayingId: String? = null

    fun onRecordingClicked(id: String) {
        if (currentlyPlayingId == id) {
            player.stop()
            currentlyPlayingId = null
        } else {
            player.stop()
            currentlyPlayingId = id
            player.play(id) {
                currentlyPlayingId = null
            }
        }
    }

    fun isRecordingPlaying(id: String): Boolean {
        return currentlyPlayingId == id
    }

    fun stopPlayback() {
        player.stop()
        currentlyPlayingId = null
    }

    // ---- Catalog (hot flow) ----

    val recordings: StateFlow<List<String>>
        get() = catalog.recordings

    fun deleteRecording(id: String) {
        if (currentlyPlayingId == id) {
            stopPlayback()
        }
        catalog.deleteRecording(id)
    }
}