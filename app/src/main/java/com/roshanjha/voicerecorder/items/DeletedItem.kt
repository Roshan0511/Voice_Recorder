package com.roshanjha.voicerecorder.items

import android.media.MediaMetadataRetriever
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roshanjha.voicerecorder.R
import com.roshanjha.voicerecorder.models.RecordingData
import com.roshanjha.voicerecorder.mvvm.RecordingViewModel
import com.roshanjha.voicerecorder.playback.AndroidAudioPlayer
import com.roshanjha.voicerecorder.ui.theme.Blue
import com.roshanjha.voicerecorder.ui.theme.DarkGray
import com.roshanjha.voicerecorder.ui.theme.LightGray
import java.io.File

@Stable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletedItem(data: RecordingData,
                  onItemClick: (Boolean) -> Unit,
                  onStart : (Float) -> Unit,
                  onProgress: (Float) -> Unit,
                  onPause: () -> Unit,
                  onRecover: () -> Unit,
                  onDelete: (RecordingData) -> Unit,
                  player: AndroidAudioPlayer,
                  viewModel: RecordingViewModel
) {

    val context = LocalContext.current

    var isItemClicked by remember {
        mutableStateOf(false)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    viewModel.selectedItemPosition.observe(lifecycleOwner) {
        isItemClicked = it==data.id
    }

    var isPlay by remember {
        mutableStateOf(false)
    }

    var isDeleteButtonClicked by remember {
        mutableStateOf(false)
    }

    var progressValue by remember {
        mutableFloatStateOf(0.0f)
    }

    val handler = Handler(Looper.myLooper()!!)

    val mmr = MediaMetadataRetriever()
    mmr.setDataSource(data.filePath)
    val length = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    val timeInMilliSec: Long = length!!.toLong()

    Box(Modifier.clickable {
        isItemClicked = !isItemClicked
        onItemClick(isItemClicked)
    }) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = LinearOutSlowInEasing
                )
            ),) {
            Divider(Modifier.padding(bottom = 5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = data.fileName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                if (!File(data.filePath).exists()) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "corrupt_icon",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Row(modifier = Modifier.padding(top = 5.dp, bottom = if (isItemClicked) 12.5.dp else 5.dp)) {
                Text(
                    text = data.date.uppercase(),
                    modifier = Modifier.weight(1f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light
                )
                Text(text = data.time.uppercase(), fontSize = 13.sp, fontWeight = FontWeight.Light)
            }

            if (isItemClicked) {
                Slider(
                    modifier = Modifier.height(20.dp),
                    value = progressValue,
                    onValueChange = {
                        progressValue = it
                        onProgress(it)
                    },
                    valueRange = 0f..timeInMilliSec.toFloat(),
                    thumb = {
                        Spacer(
                            modifier = Modifier
                                .size(ButtonDefaults.IconSize)
                                .padding(all = 3.dp)
                                .clip(CircleShape)
                                .background(color = if (isSystemInDarkTheme()) LightGray else DarkGray)
                        )
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                )
                Row(modifier = Modifier.padding(start = 7.5.dp, end = 7.5.dp, bottom = 7.5.dp)) {
                    Text(
                        text = progressValue.toLong().convertToText(),
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = timeInMilliSec.convertToText(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.padding(bottom = 10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Recover",
                        fontSize = 14.sp,
                        color = Blue,
                        modifier = Modifier.clickable {
                            onRecover()
                        })

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Icon(painter = painterResource(id = if (isPlay) R.drawable.stop_icon else R.drawable.play_icon),
                            contentDescription = "play_pause",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (!player.isPlayingSomething()) {
                                        isPlay = !isPlay
                                        if (isPlay) {
                                            onStart(progressValue)

                                            handler.post(object : Runnable {
                                                override fun run() {
                                                    if (!isPlay) {
                                                        progressValue = 0.0f
                                                        player.stop()
                                                        handler.removeMessages(0)
                                                    }
                                                    progressValue =
                                                        player
                                                            .getCurrentPosition()
                                                            .toFloat()

                                                    if (isPlay) {
                                                        if (player
                                                                .getCurrentPosition()
                                                                .toFloat() >= timeInMilliSec.toFloat()
                                                        ) {
                                                            isPlay = false
                                                        }
                                                        handler.postDelayed(this, 1000)
                                                    }
                                                }
                                            })
                                        } else {
                                            onPause()
                                        }
                                    } else {
                                        if (isPlay) {
                                            onPause()
                                            isPlay = !isPlay
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Media is already playing",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                    }
                                }
                                .padding(5.dp), tint = if (isPlay) Color.Red else MaterialTheme.colorScheme.onPrimary)
                    }

                    Text(
                        text = "Delete",
                        fontSize = 14.sp,
                        color = Blue,
                        modifier = Modifier.clickable {
                            isDeleteButtonClicked = true
                        })
                }
            }
        }
    }


    if (isDeleteButtonClicked) {
        DeleteRecordingDialog(fileName = data.fileName, onDismiss = {
            isDeleteButtonClicked = false
        }, onDelete = {
            isDeleteButtonClicked = false
            onDelete(data)
        })
    }
}


private fun Long.convertToText(): String {
    val sec = this / 1000
    val minutes = sec / 60
    val seconds = sec % 60

    val minutesString = if (minutes < 10) {
        "0$minutes"
    } else {
        minutes.toString()
    }
    val secondsString = if (seconds < 10) {
        "0$seconds"
    } else {
        seconds.toString()
    }
    return "$minutesString:$secondsString"
}