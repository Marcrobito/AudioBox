package com.aetheralab.audiobox.audio.core

interface AudioEncodeTransformer {
    fun encode(buffer: ByteArray, length: Int): ByteArray
}