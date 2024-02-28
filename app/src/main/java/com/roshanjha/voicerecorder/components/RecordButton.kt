package com.roshanjha.voicerecorder.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roshanjha.voicerecorder.ui.theme.LightGray
import com.roshanjha.voicerecorder.ui.theme.StrokeGrayLight
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Stable
@Composable
fun RecordButton(onStart :() -> Unit, onStop: () -> Unit) {
    var isRecordingStarted by remember {
        mutableStateOf(false)
    }

    var timer by remember {
        mutableStateOf("00:00:00")
    }

    var duration by remember {
        mutableLongStateOf(0L)
    }

    var startTime by remember {
        mutableLongStateOf(0L)
    }

    LaunchedEffect(isRecordingStarted) {
        while (isRecordingStarted) {
            delay(1000)
            duration = System.currentTimeMillis()-startTime
            timer = formatTime(duration)
        }
    }

    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.onSurface)
            .fillMaxWidth()) {
        Column(modifier = Modifier
            .padding(top = 20.dp, bottom = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (isRecordingStarted) {
                Text(
                    text = timer,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W700,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
            }
            Box(modifier = Modifier
                .size(60.dp)
                .border(
                    width = 2.5.dp,
                    color = if (isSystemInDarkTheme()) LightGray else StrokeGrayLight,
                    shape = CircleShape
                )
                .padding(all = if (isRecordingStarted) 15.dp else 5.dp)
                .clip(if (isRecordingStarted) RoundedCornerShape(5.dp) else CircleShape)
                .background(color = Color.Red)
                .clickable {
                    isRecordingStarted = !isRecordingStarted
                    if (!isRecordingStarted) {
                        onStop()

                        duration = 0L
                        timer = "00:00:00"
                    } else {
                        onStart()

                        startTime = System.currentTimeMillis() - duration
                    }
                })
        }
    }
}



private fun formatTime(time: Long) : String {
    val hours = TimeUnit.MILLISECONDS.toHours(time)
    val min = TimeUnit.MILLISECONDS.toMinutes(time) % 60
    val sec = TimeUnit.MILLISECONDS.toSeconds(time) % 60

    return String.format("%02d:%02d:%02d", hours, min, sec)
}