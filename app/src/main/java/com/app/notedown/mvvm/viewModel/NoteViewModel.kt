package com.app.notedown.mvvm.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.notedown.mvvm.Repository
import com.app.notedown.mvvm.room.NoteEntity
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: Repository) : ViewModel() {

    private val getAllLiveData = MutableLiveData<List<NoteEntity>>()
    val getAllNotesResponse get() = getAllLiveData

    private val getNoteDataByIdLiveData = MutableLiveData<NoteEntity>()
    val getNoteResponse get() = getNoteDataByIdLiveData

    fun getAllNotes() = viewModelScope.launch {
        getAllLiveData.value = repository.getAllNotes()
    }

    fun getNotesById(id: Int) = viewModelScope.launch {
        getNoteDataByIdLiveData.value = repository.getNotesById(id)
    }

    fun insertNote(noteEntity: NoteEntity) = viewModelScope.launch {
        repository.insert(noteEntity)
        getAllNotes()
    }

    fun deleteNote(noteEntity: NoteEntity) = viewModelScope.launch {
        repository.delete(noteEntity)
        getAllNotes()
    }

    fun updateNote(noteEntity: NoteEntity) = viewModelScope.launch {
        repository.update(noteEntity)
        getAllNotes()
    }

    fun deleteAllNotes() = viewModelScope.launch {
        repository.deleteAllNotes()
        getAllNotes()
    }

    fun deleteById(id: Int) = viewModelScope.launch {
        repository.deleteById(id)
        getAllNotes()
    }


}