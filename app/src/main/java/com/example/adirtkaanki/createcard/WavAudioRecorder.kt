package com.example.adirtkaanki.createcard

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.File
import java.io.RandomAccessFile
import kotlin.concurrent.thread

class WavAudioRecorder(
    private val context: Context
) {
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var outputFile: File? = null
    @Volatile private var isRecording = false

    fun start(fileName: String): File {
        if (isRecording) {
            throw IllegalStateException("Recorder is already running")
        }

        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        require(minBufferSize > 0) { "Unable to initialize recorder" }

        val file = File(context.cacheDir, fileName)
        if (file.exists()) file.delete()

        val record = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize * 2
        )

        file.outputStream().use { stream ->
            stream.write(ByteArray(44))
        }

        outputFile = file
        audioRecord = record
        isRecording = true

        record.startRecording()
        recordingThread = thread(start = true, name = "wav-recorder") {
            writePcmData(record, file, minBufferSize)
        }

        return file
    }

    fun stop(): File? {
        if (!isRecording) return outputFile

        isRecording = false
        audioRecord?.stop()
        recordingThread?.join()
        audioRecord?.release()
        audioRecord = null
        recordingThread = null

        val file = outputFile
        if (file != null && file.exists()) {
            updateWavHeader(file)
        }
        outputFile = null
        return file
    }

    private fun writePcmData(record: AudioRecord, file: File, bufferSize: Int) {
        val buffer = ByteArray(bufferSize)
        RandomAccessFile(file, "rw").use { raf ->
            raf.seek(44)
            while (isRecording) {
                val read = record.read(buffer, 0, buffer.size)
                if (read > 0) {
                    raf.write(buffer, 0, read)
                }
            }
        }
    }

    private fun updateWavHeader(file: File) {
        RandomAccessFile(file, "rw").use { raf ->
            val totalAudioLen = raf.length() - 44
            val totalDataLen = totalAudioLen + 36
            val channels = 1
            val byteRate = sampleRate * channels * 16 / 8

            raf.seek(0)
            raf.writeBytes("RIFF")
            raf.writeIntLE(totalDataLen.toInt())
            raf.writeBytes("WAVE")
            raf.writeBytes("fmt ")
            raf.writeIntLE(16)
            raf.writeShortLE(1)
            raf.writeShortLE(channels.toShort())
            raf.writeIntLE(sampleRate)
            raf.writeIntLE(byteRate)
            raf.writeShortLE((channels * 16 / 8).toShort())
            raf.writeShortLE(16)
            raf.writeBytes("data")
            raf.writeIntLE(totalAudioLen.toInt())
        }
    }
}

private fun RandomAccessFile.writeIntLE(value: Int) {
    write(byteArrayOf(
        (value and 0xff).toByte(),
        ((value shr 8) and 0xff).toByte(),
        ((value shr 16) and 0xff).toByte(),
        ((value shr 24) and 0xff).toByte()
    ))
}

private fun RandomAccessFile.writeShortLE(value: Short) {
    write(byteArrayOf(
        (value.toInt() and 0xff).toByte(),
        ((value.toInt() shr 8) and 0xff).toByte()
    ))
}
