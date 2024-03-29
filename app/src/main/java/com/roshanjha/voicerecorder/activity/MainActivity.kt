package com.roshanjha.voicerecorder.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.roshanjha.voicerecorder.R
import com.roshanjha.voicerecorder.components.RecordButton
import com.roshanjha.voicerecorder.db.RecordingDatabase
import com.roshanjha.voicerecorder.items.RecordingItem
import com.roshanjha.voicerecorder.models.RecordingData
import com.roshanjha.voicerecorder.mvvm.RecordingViewModel
import com.roshanjha.voicerecorder.playback.AndroidAudioPlayer
import com.roshanjha.voicerecorder.record.AndroidAudioRecorder
import com.roshanjha.voicerecorder.repository.RecordingRepository
import com.roshanjha.voicerecorder.ui.theme.Blue
import com.roshanjha.voicerecorder.ui.theme.VoiceRecorderTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val recorder by lazy {
        AndroidAudioRecorder(this@MainActivity)
    }

    private val player by lazy {
        AndroidAudioPlayer(this@MainActivity)
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
                            return RecordingViewModel(RecordingRepository(RecordingDatabase(this@MainActivity))) as T
                        }
                    }
                )
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(viewModel, recorder, player)
                }
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()

        try {
            recorder.stop()
            player.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Stable
@Composable
fun HomeScreen(viewModel: RecordingViewModel, recorder: AndroidAudioRecorder, player: AndroidAudioPlayer) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var audioFile: File? = null

    val sdfDate = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
    val sdfTime = SimpleDateFormat("hh:mm a", Locale.US)

    Surface(color = if (isSystemInDarkTheme()) Color.Black else Color.White,
        modifier = Modifier
        .fillMaxSize()) {
        Column(
            Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f, false)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(imageVector = Icons.Outlined.KeyboardArrowLeft,
                        contentDescription = "back_icon",
                        colorFilter = ColorFilter.tint(color = Blue),
                        modifier = Modifier
                            .clip(CircleShape)
                            .width(32.dp)
                            .height(32.dp)
                            .clickable {
                                context.startActivity(Intent(context, MenuActivity::class.java))
                            })
                    Text(
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterVertically)
                            .padding(end = 10.dp)
                            .clickable {
                                context.startActivity(
                                    Intent(
                                        context,
                                        FavRecordingActivity::class.java
                                    )
                                )
                            },
                        text = "Favourites", color = Blue,
                        textAlign = TextAlign.End, fontWeight = FontWeight.Normal, fontSize = 15.sp
                    )
                }

                Text(
                    text = "All Recordings", fontSize = 26.sp,
                    fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                )

                var list: MutableList<RecordingData> by remember {
                    mutableStateOf(mutableListOf())
                }
                val listState = rememberLazyListState()

                viewModel.getAllRecordings().observe(lifecycleOwner) {
                    list = it as MutableList<RecordingData>
                    Log.d("RecordingData", "HomeScreen: $list")
                }

                if (list.isEmpty()) {
                    Box( modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        Image(painter = painterResource(id = R.drawable.no_data),
                            contentDescription = "no_data",
                            modifier = Modifier.size(128.dp))
                    }
                } else {
                    LazyColumn(state = listState, content = {
                        itemsIndexed(items = list) { _, recording ->
                            RecordingItem(data = recording, onItemClick = {
                                if (it) {
                                    viewModel.selectedItemPosition.postValue(recording.id)
                                } else {
                                    viewModel.selectedItemPosition.postValue(-1)
                                }
                            }, onStart = {
                                if (it>0.00) {
                                    player.resume(File(recording.filePath))
                                } else {
                                    player.playFile(File(recording.filePath))
                                }
                            }, onProgress = {
                                player.playFromMid(it)
                            }, onPause = {
                                player.pause()
                            }, onFavorite = {
                                viewModel.updateRecording(
                                    RecordingData(
                                        id = recording.id,
                                        filePath = recording.filePath,
                                        fileName = recording.fileName,
                                        time = recording.time,
                                        date = recording.date,
                                        isFav = it)
                                )
                            }, onDelete = {
                                viewModel.updateRecording(
                                    RecordingData(
                                        id = recording.id,
                                        filePath = recording.filePath,
                                        fileName = recording.fileName,
                                        time = recording.time,
                                        date = recording.date,
                                        isFav = recording.isFav,
                                        isInTrash = true))
                            }, player = player, viewModel = viewModel)
                        }
                    }, modifier = Modifier.padding(top = 10.dp))
                }
            }

            RecordButton(onStart = {
                File(context.cacheDir, "${System.currentTimeMillis()}_audio.mp3").also {
                    recorder.start(it)
                    audioFile = it
                }
            }, onStop = {
                recorder.stop()
                viewModel.saveRecording(
                    RecordingData(
                        filePath = audioFile!!.absolutePath,
                        fileName = audioFile!!.name,
                        time = sdfTime.format(Date()),
                        date = sdfDate.format(Date()),
                        isFav = false
                    )
                )
            })
        }
    }
}




@Preview(showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun HomeScreenPreview() {
    VoiceRecorderTheme {
        val context = LocalContext.current
        HomeScreen(RecordingViewModel(RecordingRepository(RecordingDatabase(context))),
            AndroidAudioRecorder(context), AndroidAudioPlayer(context)
        )
    }
}