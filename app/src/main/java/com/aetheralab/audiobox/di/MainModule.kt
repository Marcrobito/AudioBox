package com.aetheralab.audiobox.di

import android.content.Context
import com.aetheralab.audiobox.audio.AudioRecordRecorder
import com.aetheralab.audiobox.audio.AudioRecordingsManager
import com.aetheralab.audiobox.audio.AudioTrackPlayer
import com.aetheralab.audiobox.audio.XorAudioTransformer
import com.aetheralab.audiobox.audio.core.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    // --- Transformer ---

    @Provides
    @Singleton
    fun provideAudioEncodeTransformer(): AudioEncodeTransformer =
        XorAudioTransformer()

    @Provides
    @Singleton
    fun provideAudioDecodeTransformer(): AudioDecodeTransformer =
        XorAudioTransformer()

    // --- Recording manager (singleton, owns files & state) ---

    @Provides
    @Singleton
    fun provideRecordingManager(
        @ApplicationContext context: Context
    ): AudioRecordingsManager =
        AudioRecordingsManager(context)

    @Provides
    fun provideRecordingWriterManager(
        manager: AudioRecordingsManager
    ): RecordingWriterManager = manager

    @Provides
    fun provideRecordingCatalog(
        manager: AudioRecordingsManager
    ): RecordingCatalog = manager

    // --- Recorder ---

    @Provides
    fun provideAudioRecorder(
        encodeTransformer: AudioEncodeTransformer,
        manager: RecordingWriterManager
    ): AudioRecorder =
        AudioRecordRecorder(encodeTransformer, manager)

    // --- Player ---

    @Provides
    fun provideAudioPlayer(
        @ApplicationContext context: Context,
        decodeTransformer: AudioDecodeTransformer
    ): AudioPlayer =
        AudioTrackPlayer(context, decodeTransformer)
}