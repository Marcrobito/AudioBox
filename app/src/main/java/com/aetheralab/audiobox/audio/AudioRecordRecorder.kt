package com.aetheralab.audiobox.audio

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import com.aetheralab.audiobox.audio.core.AudioEncodeTransformer
import com.aetheralab.audiobox.audio.core.AudioRecorder
import com.aetheralab.audiobox.audio.core.RecordingWriterManager

private const val sampleRate = 48_000
private const val channelConfig = AudioFormat.CHANNEL_IN_MONO
private const val audioFormat = AudioFormat.ENCODING_PCM_16BIT

class AudioRecordRecorder(
    private val transformer: AudioEncodeTransformer,
    private val manager: RecordingWriterManager
) : AudioRecorder {
    private val minBufferSize =
        AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    @Volatile
    private var isRecording = false

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun startRecording() {
        if (isRecording) return
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize
        )

        val writer = manager.createRecordingWriter()

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize
        )

        audioRecord?.startRecording()
        writer.open()
        isRecording = true

        recordingThread = Thread {
            val buffer = ByteArray(minBufferSize)

            while (isRecording) {
                val bytesRead = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (bytesRead > 0) {
                    transformer.encode(buffer, bytesRead)
                    writer.write(buffer, bytesRead)
                }
            }
            writer.close()
        }.also { it.start() }
    }

    override fun stopRecording() {
        if (!isRecording) return

        isRecording = false

        recordingThread?.join()
        recordingThread = null

        audioRecord?.apply {
            stop()
            release()
        }
        audioRecord = null
    }
}