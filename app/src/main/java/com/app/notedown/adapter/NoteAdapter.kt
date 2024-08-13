package com.app.notedown.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.notedown.databinding.NoteRvItemBinding
import com.app.notedown.mvvm.room.NoteEntity
import com.app.notedown.util.NoteClickListener

class NoteAdapter(private val noteList : List<NoteEntity>, private val clickListener: NoteClickListener):RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    class ViewHolder(val binding : NoteRvItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(noteEntity: NoteEntity) {
            binding.tvNote.text = noteEntity.noteTitle
            binding.tvNote.isSelected = true
            binding.tvDesc.text = noteEntity.noteDesc
            binding.tvDate.text = noteEntity.noteTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(NoteRvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount() = noteList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(noteList[position])
        holder.binding.deleteBtn.setOnClickListener {
            clickListener.onNoteDeleteClick(noteList[position].id)
        }
        holder.binding.root.setOnClickListener {
            clickListener.onNoteClick(noteList[position].id)
        }
    }
}