package com.roshanjha.voicerecorder.activity

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roshanjha.voicerecorder.activity.screens.FavRecordingScreen
import com.roshanjha.voicerecorder.db.RecordingDatabase
import com.roshanjha.voicerecorder.mvvm.RecordingViewModel
import com.roshanjha.voicerecorder.playback.AndroidAudioPlayer
import com.roshanjha.voicerecorder.repository.RecordingRepository
import com.roshanjha.voicerecorder.ui.theme.VoiceRecorderTheme

class FavRecordingActivity : ComponentActivity() {

    private val player by lazy {
        AndroidAudioPlayer(this@FavRecordingActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VoiceRecorderTheme {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    0
                )
                val viewModel = viewModel<RecordingViewModel>(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return RecordingViewModel(RecordingRepository(RecordingDatabase(this@FavRecordingActivity))) as T
                        }
                    }
                )
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FavRecordingScreen(viewModel, player)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        try {
            player.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}