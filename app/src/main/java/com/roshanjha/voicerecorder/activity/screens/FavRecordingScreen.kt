package com.roshanjha.voicerecorder.activity.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roshanjha.voicerecorder.R
import com.roshanjha.voicerecorder.items.RecordingItem
import com.roshanjha.voicerecorder.models.RecordingData
import com.roshanjha.voicerecorder.mvvm.RecordingViewModel
import com.roshanjha.voicerecorder.playback.AndroidAudioPlayer
import com.roshanjha.voicerecorder.ui.theme.Blue
import java.io.File

@SuppressLint("MutableCollectionMutableState")
@Stable
@Composable
fun FavRecordingScreen(viewModel: RecordingViewModel, player: AndroidAudioPlayer) {
    val context = LocalContext.current
    val activity = context as Activity
    val lifecycleOwner = LocalLifecycleOwner.current

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

                Image(imageVector = Icons.Outlined.KeyboardArrowLeft,
                    contentDescription = "back_icon",
                    colorFilter = ColorFilter.tint(color = Blue),
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .clip(CircleShape)
                        .width(32.dp)
                        .height(32.dp)
                        .clickable {
                            activity.finish()
                        })

                Text(
                    text = "Fav Recordings", fontSize = 26.sp,
                    fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                )

                var list: MutableList<RecordingData> by remember {
                    mutableStateOf(mutableListOf())
                }

                viewModel.getFavRecordings().observe(lifecycleOwner) {
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
                    LazyColumn(content = {
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
                                        isInTrash = true)
                                )
                            }, player = player, viewModel = viewModel)
                        }
                    }, modifier = Modifier.padding(top = 10.dp))
                }
            }
        }
    }
}