package com.aetheralab.audiobox.audio.core

interface AudioPlayer {
    fun play(recordingId: String, onCompletion: () -> Unit)
    fun stop()
}