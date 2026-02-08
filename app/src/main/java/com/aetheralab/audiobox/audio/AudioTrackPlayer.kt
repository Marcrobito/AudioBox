package com.aetheralab.audiobox.audio

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.aetheralab.audiobox.audio.core.AudioDecodeTransformer
import com.aetheralab.audiobox.audio.core.AudioPlayer
import java.io.File
import java.io.FileInputStream

class AudioTrackPlayer(
    private val context: Context,
    private val transformer: AudioDecodeTransformer
) : AudioPlayer {

    private val sampleRate = 48_000
    private val channelConfig = AudioFormat.CHANNEL_OUT_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    private var audioTrack: AudioTrack? = null
    private var playThread: Thread? = null
    @Volatile private var isPlaying = false

    override fun play(recordingId: String, onCompletion: () -> Unit) {
        if (isPlaying) return

        val file = File(context.filesDir, recordingId)
        if (!file.exists()) return

        val minBufferSize =
            AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize,
            AudioTrack.MODE_STREAM
        )

        isPlaying = true
        audioTrack?.play()

        playThread = Thread {
            FileInputStream(file).use { input ->
                val buffer = ByteArray(minBufferSize)

                while (isPlaying) {
                    val bytesRead = input.read(buffer)
                    if (bytesRead <= 0) break

                    transformer.decode(buffer, bytesRead)
                    audioTrack?.write(buffer, 0, bytesRead)
                }
            }

            stopInternal()
            onCompletion()
        }.also { it.start() }
    }

    override fun stop() {
        if (!isPlaying) return
        isPlaying = false
        playThread?.join()
    }

    private fun stopInternal() {
        audioTrack?.apply {
            stop()
            release()
        }
        audioTrack = null
        playThread = null
        isPlaying = false
    }
}