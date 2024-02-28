package com.roshanjha.voicerecorder.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.roshanjha.voicerecorder.models.RecordingData

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecording(recordingData: RecordingData)

    @Update
    suspend fun updateRecording(recordingData: RecordingData)

    @Query("SELECT * FROM RecordingData WHERE isInTrash=0 ORDER BY id DESC")
    fun getAllRecordings(): LiveData<List<RecordingData>>

    @Query("SELECT * FROM RecordingData WHERE isInTrash=0 AND isFav=1 ORDER BY id DESC")
    fun getFavRecordings(): LiveData<List<RecordingData>>

    @Query("SELECT * FROM RecordingData WHERE isInTrash=1 ORDER BY id DESC")
    fun getTrashData(): LiveData<List<RecordingData>>

    @Query("SELECT * FROM RecordingData WHERE fileName LIKE :query OR filePath LIKE :query OR date LIKE :query ORDER BY id DESC")
    fun searchRecording(query: String): LiveData<List<RecordingData>>

    @Delete
    suspend fun deleteRecording(recordingData: RecordingData)
}