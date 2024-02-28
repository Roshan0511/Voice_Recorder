package com.roshanjha.voicerecorder.models

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Immutable
@Entity
data class RecordingData (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val filePath: String,
    val fileName: String,
    val time: String,
    val date: String,
    val isFav: Boolean) :
    Serializable