package com.app.notedown.mvvm.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {

    //For Inserting data into table
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: NoteEntity)

    //For Updating data into table
    @Update
    suspend fun update(note: NoteEntity)

    //For Deleting data from table
    @Delete
    suspend fun delete(note: NoteEntity)

    //For Deleting data from table by id
    @Query("Delete from NotesTable where id = :id")
    suspend fun deleteById(id: Int)

    //For deleting all data from table
    @Query("Delete from NotesTable")
    suspend fun deleteAllNotes()

    //For selecting all data from table
    @Query("Select * from NotesTable order by id DESC")
    suspend fun getAllNotes(): List<NoteEntity>

    //For selecting data from table by id
    @Query("Select * from NotesTable where id = :id")
    suspend fun getNotesById(id:Int): NoteEntity
}