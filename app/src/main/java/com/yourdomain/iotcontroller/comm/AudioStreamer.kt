package com.yourdomain.iotcontroller.comm

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import kotlinx.coroutines.*
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

object AudioStreamer {
    // Audio config (match ESP32 codec & transport)
    private const val SAMPLE_RATE = 8000
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private const val BUFFER_SIZE = 2048

    private const val AUDIO_PORT = 7070 // ESP32 socket port for audio
    private const val DEVICE_IP = "192.168.4.1"

    private var recordingJob: Job? = null
    private var playbackJob: Job? = null

    fun startStreamingAudio() {
        stopStreamingAudio() // Ensure any existing audio session is stopped

        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            // Setup AudioRecord to read microphone
            val minBuffer = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
            val audioRecorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                minBuffer
            )
            val buffer = ByteArray(BUFFER_SIZE)

            Socket().use { socket ->
                socket.connect(InetSocketAddress(DEVICE_IP, AUDIO_PORT), 2000)
                val outputStream: OutputStream = socket.getOutputStream()
                audioRecorder.startRecording()
                while (isActive) {
                    val read = audioRecorder.read(buffer, 0, buffer.size)
                    if (read > 0) {
                        outputStream.write(buffer, 0, read)
                    }
                }
                audioRecorder.stop()
                audioRecorder.release()
                outputStream.flush()
            }
        }

        playbackJob = CoroutineScope(Dispatchers.IO).launch {
            val minBuffer = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AUDIO_FORMAT)
            val audioTrack = AudioTrack.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AUDIO_FORMAT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuffer)
                .build()
            
            Socket().use { socket ->
                socket.connect(InetSocketAddress(DEVICE_IP, AUDIO_PORT + 1), 2000)
                val inputStream: InputStream = socket.getInputStream()
                val buffer = ByteArray(BUFFER_SIZE)
                audioTrack.play()
                while (isActive) {
                    val read = inputStream.read(buffer)
                    if (read > 0) {
                        audioTrack.write(buffer, 0, read)
                    }
                }
                audioTrack.stop()
                audioTrack.release()
            }
        }
    }

    fun stopStreamingAudio() {
        recordingJob?.cancel()
        playbackJob?.cancel()
        recordingJob = null
        playbackJob = null
    }
}
