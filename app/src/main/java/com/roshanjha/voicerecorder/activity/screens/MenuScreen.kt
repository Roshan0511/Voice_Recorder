package com.roshanjha.voicerecorder.activity.screens

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roshanjha.voicerecorder.R
import com.roshanjha.voicerecorder.activity.DeleteRecordingsActivity
import com.roshanjha.voicerecorder.activity.FavRecordingActivity
import com.roshanjha.voicerecorder.mvvm.RecordingViewModel
import com.roshanjha.voicerecorder.ui.theme.Blue
import com.roshanjha.voicerecorder.ui.theme.DarkGray
import com.roshanjha.voicerecorder.ui.theme.LightBackground
import com.roshanjha.voicerecorder.ui.theme.LightGray
import com.roshanjha.voicerecorder.ui.theme.StrokeGrayLight

@Stable
@Composable
fun MenuScreen(viewModel: RecordingViewModel) {
    val activity = LocalContext.current as Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    var allRecordingsNo by remember {
        mutableIntStateOf(0)
    }

    var recentlyDeletedNo by remember {
        mutableIntStateOf(0)
    }

    viewModel.getAllRecordings().observe(lifecycleOwner) {
        allRecordingsNo = it.size
    }

    viewModel.getTrashData().observe(lifecycleOwner) {
        recentlyDeletedNo = it.size
    }

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

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp, top = 15.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            activity.startActivity(Intent(activity, FavRecordingActivity::class.java))
                        },
                    text = "Favourites", color = Blue,
                    textAlign = TextAlign.End, fontWeight = FontWeight.Normal, fontSize = 15.sp
                )

                Text(
                    text = "Voice Memos", fontSize = 27.sp,
                    fontFamily = FontFamily.Serif, fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                )

                Box(modifier = Modifier
                    .padding(all = 10.dp)
                    .fillMaxWidth()
                    .background(
                        color = if (isSystemInDarkTheme()) DarkGray else LightBackground,
                        shape = RoundedCornerShape(15.dp)
                    )) {
                    Column(
                        verticalArrangement = Arrangement.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(all = 10.dp).clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                activity.finish()
                            }) {
                            Icon(painter = painterResource(id = R.drawable.voice_icon),
                                contentDescription = "All_recordings",
                                tint = Blue)
                            Text(text = "All Recordings",
                                fontWeight = FontWeight.W400,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 15.dp))
                            Text(text = allRecordingsNo.toString(),
                                color = if (isSystemInDarkTheme()) LightGray else StrokeGrayLight,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .padding(end = 3.dp)
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                            Icon(imageVector = Icons.Default.KeyboardArrowRight,
                                modifier = Modifier.size(20.dp), contentDescription = "All_recordings",
                                tint = if (isSystemInDarkTheme()) LightGray else StrokeGrayLight)
                        }

                        Divider(modifier = Modifier
                            .padding(start = 45.dp)
                            .fillMaxWidth())

                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(all = 10.dp).clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                activity.startActivity(Intent(activity, DeleteRecordingsActivity::class.java))
                            }) {
                            Icon(painter = painterResource(id = R.drawable.delete_icon),
                                contentDescription = "All_recordings",
                                tint = Blue)
                            Text(text = "Recently Deleted",
                                fontWeight = FontWeight.W400,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 15.dp))
                            Text(text = recentlyDeletedNo.toString(),
                                color = if (isSystemInDarkTheme()) LightGray else StrokeGrayLight,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .padding(end = 3.dp)
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                            Icon(imageVector = Icons.Default.KeyboardArrowRight,
                                modifier = Modifier.size(20.dp), contentDescription = "All_recordings",
                                tint = if (isSystemInDarkTheme()) LightGray else StrokeGrayLight)
                        }
                    }
                }
            }
        }
    }
}