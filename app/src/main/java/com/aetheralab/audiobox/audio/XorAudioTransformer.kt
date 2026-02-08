package com.aetheralab.audiobox.audio

import com.aetheralab.audiobox.audio.core.AudioDecodeTransformer
import com.aetheralab.audiobox.audio.core.AudioEncodeTransformer
import kotlin.experimental.xor

class XorAudioTransformer : AudioEncodeTransformer, AudioDecodeTransformer {
    private val key: Byte = 0x5A

    override fun encode(buffer: ByteArray, length: Int): ByteArray =
        applyXor(buffer, length)

    override fun decode(buffer: ByteArray, length: Int): ByteArray =
        applyXor(buffer, length)

    private fun applyXor(buffer: ByteArray, length: Int): ByteArray {
        for (i in 0 until length) {
            buffer[i] = buffer[i] xor key
        }
        return buffer
    }

}