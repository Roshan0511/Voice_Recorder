package com.roshanjha.voicerecorder.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.roshanjha.voicerecorder.models.RecordingData

@Database(
    entities = [RecordingData::class],
    version = 1,
    exportSchema = false
)
abstract class RecordingDatabase : RoomDatabase() {

    abstract fun getRecordingDao() : Dao

    companion object {

        @Volatile
        private var instance : RecordingDatabase? = null
        private val LOCK = Any()

        operator fun invoke (context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase (context: Context) = Room.databaseBuilder(
            context.applicationContext,
            RecordingDatabase::class.java,
            "recording_database"
        ).build()
    }

}