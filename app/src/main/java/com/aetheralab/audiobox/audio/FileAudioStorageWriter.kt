package com.aetheralab.audiobox.audio

import com.aetheralab.audiobox.audio.core.AudioStorageWriter
import java.io.File
import java.io.FileOutputStream

class FileAudioStorageWriter(
    private val file: File,
    private val onClose: () -> Unit
) : AudioStorageWriter {

    private var outputStream: FileOutputStream? = null

    override fun open() {
        outputStream = FileOutputStream(file)
    }

    override fun write(buffer: ByteArray, length: Int) {
        outputStream?.write(buffer, 0, length)
    }

    override fun close() {
        outputStream?.flush()
        outputStream?.close()
        outputStream = null
        onClose()
    }
}