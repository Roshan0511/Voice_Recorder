package com.roshanjha.voicerecorder.playback

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import java.io.File

class AndroidAudioPlayer(
    private val context: Context
): AudioPlayer {

    private var player: MediaPlayer? = null

    override fun playFile(file: File) {
        MediaPlayer.create(context, file.toUri()).apply {
            player = this
            start()
        }
    }

    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }

    fun pause() {
        player?.pause()
    }

    fun resume(file:File) {
        if (player!=null) {
            player?.start()
        } else {
            playFile(file = file)
        }
    }

    fun playFromMid(value: Float) {
        if (player!=null && player?.isPlaying==true) {
            player?.seekTo(value.toInt())
        }
    }

    fun getCurrentPosition(): Int {
        return if (player==null) 0 else player!!.currentPosition
    }

    fun isPlayingSomething(): Boolean {
        return !(player==null||!player!!.isPlaying)
    }
}