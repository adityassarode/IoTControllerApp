package com.yourdomain.iotcontroller.comm

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.*
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

object AudioStreamer {
    // Audio config
    private const val SAMPLE_RATE = 8000
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private const val BUFFER_SIZE = 2048

    private const val AUDIO_PORT = 7070 // ESP32 socket port for audio uplink
    private const val DEVICE_IP = "192.168.4.1"

    private var recordingJob: Job? = null

    fun startStreamingAudio() {
        stopStreamingAudio()
        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            val minBuffer = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
            val audioRecorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                minBuffer
            )
            val buffer = ByteArray(BUFFER_SIZE)
            try {
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
            } catch (_: Exception) {
                try { audioRecorder.stop(); audioRecorder.release() } catch (_: Exception) {}
            }
        }
    }

    fun stopStreamingAudio() {
        recordingJob?.cancel()
        recordingJob = null
    }
}
