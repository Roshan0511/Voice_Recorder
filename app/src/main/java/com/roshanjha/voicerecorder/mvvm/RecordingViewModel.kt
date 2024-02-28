package com.roshanjha.voicerecorder.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roshanjha.voicerecorder.models.RecordingData
import com.roshanjha.voicerecorder.repository.RecordingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RecordingViewModel(private val repositoryObject: RecordingRepository) : ViewModel() {

    var selectedItemPosition = MutableLiveData<Int>(-1)

    fun saveRecording(newRecording: RecordingData) = viewModelScope.launch(Dispatchers.IO) {
        repositoryObject.addRecording(newRecording)
    }

    fun updateRecording(existingRecording: RecordingData) = viewModelScope.launch(Dispatchers.IO) {
        repositoryObject.updateRecording(existingRecording)
    }

    fun deleteRecording(existingRecording: RecordingData) = viewModelScope.launch(Dispatchers.IO) {
        repositoryObject.deleteRecording(existingRecording)
    }

    fun searchRecording(query: String): LiveData<List<RecordingData>> {
        return repositoryObject.searchRecording(query)
    }

    fun getAllRecordings(): LiveData<List<RecordingData>> = repositoryObject.getRecordings()

    fun getTrashData(): LiveData<List<RecordingData>> = repositoryObject.getTrashData()

    fun getFavRecordings(): LiveData<List<RecordingData>> = repositoryObject.getFavRecordings()
}