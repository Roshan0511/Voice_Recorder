package com.roshanjha.voicerecorder.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roshanjha.voicerecorder.models.RecordingData
import com.roshanjha.voicerecorder.repository.RecordingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordingViewModel(private val repositoryObject: RecordingRepository) : ViewModel() {

    var selectedItemPosition = MutableLiveData(-1)

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