package com.app.notedown.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.notedown.databinding.FragmentNoteShowBinding
import com.app.notedown.mvvm.Repository
import com.app.notedown.mvvm.room.NoteDatabase
import com.app.notedown.mvvm.room.NoteEntity
import com.app.notedown.mvvm.viewModel.NoteViewModel
import com.app.notedown.mvvm.viewModel.factory.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val ARG_ID = "param1"

class NoteShowFragment : Fragment() {
    private var id: Int? = null
    private lateinit var viewModel: NoteViewModel
    private lateinit var binding: FragmentNoteShowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt(ARG_ID)
        }

        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(Repository(NoteDatabase.getDatabase(requireContext()).getNoteDao()))
        )[NoteViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNoteShowBinding.inflate(inflater, container, false)
        viewModel.getNotesById(id!!)

        viewModel.getNoteResponse.observe(viewLifecycleOwner) {
            binding.etTitle.setText(it.noteTitle)
            binding.etNote.setText(it.noteDesc)
        }

        binding.etNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentTime = Calendar.getInstance().time
                val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val formattedTime = formatter.format(currentTime)
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormatter.format(currentTime)
                if (s != null) {
                    viewModel.updateNote(
                        NoteEntity(
                            id = id!!,
                            noteTitle = binding.etTitle.text.toString(),
                            noteDesc = s.toString().trim(),
                            noteTime = "$formattedDate,$formattedTime (Edited)"
                        )
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentTime = Calendar.getInstance().time
                val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val formattedTime = formatter.format(currentTime)
                if (s != null) {
                    viewModel.updateNote(
                        NoteEntity(
                            id = id!!,
                            noteTitle = s.toString().trim(),
                            noteDesc = binding.etNote.text.toString(),
                            noteTime = formattedTime
                        )
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(id: Int) =
            NoteShowFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID, id)
                }
            }
    }

}