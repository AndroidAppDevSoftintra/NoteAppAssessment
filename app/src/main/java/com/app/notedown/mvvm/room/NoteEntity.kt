package com.app.notedown.mvvm.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NotesTable")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id :Int = 0,
    @ColumnInfo(name = "Title") val noteTitle: String,
    @ColumnInfo(name = "Description") val noteDesc: String,
    @ColumnInfo(name = "Time") val noteTime: String,
)