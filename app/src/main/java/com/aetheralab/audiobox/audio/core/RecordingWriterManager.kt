package com.aetheralab.audiobox.audio.core

interface RecordingWriterManager {
    fun createRecordingWriter(): AudioStorageWriter
}