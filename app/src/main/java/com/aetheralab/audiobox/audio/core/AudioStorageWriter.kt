package com.aetheralab.audiobox.audio.core

interface AudioStorageWriter {
    fun open()
    fun write(buffer: ByteArray, length: Int)
    fun close()
}