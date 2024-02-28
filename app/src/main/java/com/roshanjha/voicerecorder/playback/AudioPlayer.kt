package com.roshanjha.voicerecorder.playback

import java.io.File
import java.time.Duration

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
}