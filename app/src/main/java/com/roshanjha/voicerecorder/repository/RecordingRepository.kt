package com.roshanjha.voicerecorder.repository

import com.roshanjha.voicerecorder.db.RecordingDatabase
import com.roshanjha.voicerecorder.models.RecordingData

class RecordingRepository(
    private val db: RecordingDatabase,
) {
    fun getRecordings() =
        db.getRecordingDao().getAllRecordings()

    fun searchRecording(query: String) =
        db.getRecordingDao().searchRecording(query)

    suspend fun addRecording(note: RecordingData) =
        db.getRecordingDao().addRecording(note)

    suspend fun updateRecording(note: RecordingData) =
        db.getRecordingDao().updateRecording(note)

    suspend fun deleteRecording(note: RecordingData) =
        db.getRecordingDao().deleteRecording(note)
}