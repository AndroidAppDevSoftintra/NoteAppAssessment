package com.app.notedown.mvvm

import com.app.notedown.mvvm.room.NoteDao
import com.app.notedown.mvvm.room.NoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val noteDao: NoteDao) {

    //Room Database operations
    suspend fun getAllNotes() : List<NoteEntity> {
        return withContext(Dispatchers.IO){
            noteDao.getAllNotes()
        }
    }

    suspend fun getNotesById(id: Int) : NoteEntity {
        return withContext(Dispatchers.IO){
            noteDao.getNotesById(id)
        }
    }

    suspend fun insert(noteEntity: NoteEntity){
        withContext(Dispatchers.IO){
            noteDao.insert(noteEntity)
        }
    }

    suspend fun update(noteEntity: NoteEntity){
        withContext(Dispatchers.IO){
            noteDao.update(noteEntity)
        }
    }

    suspend fun delete(noteEntity: NoteEntity){
        withContext(Dispatchers.IO){
            noteDao.delete(noteEntity)
        }
    }

    suspend fun deleteById(id: Int){
        withContext(Dispatchers.IO){
            noteDao.deleteById(id)
        }
    }

    suspend fun deleteAllNotes(){
        withContext(Dispatchers.IO){
            noteDao.deleteAllNotes()
        }
    }


}