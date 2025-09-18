package com.yourdomain.iotcontroller.comm

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.*
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket

class AudioReceiver(
    private val context: Context,
    private val esp32Ip: String = "192.168.4.1",
    private val port: Int = 7071, // Downlink audio port from ESP32
    private val useSpeaker: Boolean = false
) {
    private var job: Job? = null

    fun start() {
        stop()
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                audioManager.isSpeakerphoneOn = useSpeaker

                val bufferSize = AudioTrack.getMinBufferSize(
                    8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT
                )
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(8000)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .build()

                Socket().use { socket ->
                    socket.connect(InetSocketAddress(esp32Ip, port), 2000)
                    val input: InputStream = socket.getInputStream()
                    val buffer = ByteArray(1024)
                    audioTrack.play()
                    while (isActive) {
                        val read = input.read(buffer)
                        if (read > 0) audioTrack.write(buffer, 0, read)
                    }
                    audioTrack.stop()
                    audioTrack.release()
                }
            } catch (_: Exception) { }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
