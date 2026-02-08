package com.aetheralab.audiobox.audio.core

interface AudioDecodeTransformer {
    fun decode(buffer: ByteArray, length: Int): ByteArray
}