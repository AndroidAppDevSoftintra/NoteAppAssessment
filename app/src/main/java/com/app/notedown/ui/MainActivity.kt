package com.app.notedown.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.notedown.adapter.NoteAdapter
import com.app.notedown.databinding.ActivityMainBinding
import com.app.notedown.databinding.DialogAddNoteBinding
import com.app.notedown.mvvm.Repository
import com.app.notedown.mvvm.room.NoteDatabase
import com.app.notedown.mvvm.room.NoteEntity
import com.app.notedown.mvvm.viewModel.NoteViewModel
import com.app.notedown.mvvm.viewModel.factory.ViewModelFactory
import com.app.notedown.util.NoteClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), NoteClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NoteViewModel
    private var noteList = ArrayList<NoteEntity>()
    private lateinit var noteAdapter: NoteAdapter
    private var isContainerVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(NoteDatabase.getDatabase(application).getNoteDao()))
        )[NoteViewModel::class.java]

        initObserver()

        viewModel.getAllNotes()
        noteAdapter = NoteAdapter(noteList, this)
        binding.notesRV.adapter = noteAdapter
        binding.notesRV.layoutManager = LinearLayoutManager(this)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
                if (!query.isNullOrEmpty())
                    filterNotes(query.trim())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty())
                    filterNotes(newText.trim())
                else
                    viewModel.getAllNotes()
                return true
            }
        })


        binding.addNoteBtn.setOnClickListener {
            binding.searchView.setQuery("", false)
            showAddNoteDialog()
        }

        binding.addNoteBtn.setOnLongClickListener {
            AlertDialog.Builder(this).apply {
                setMessage("Are you sure you want to all delete notes?")
                setPositiveButton("Yes") { _, _ ->
                    viewModel.deleteAllNotes()
                }
                setNegativeButton("No") { _, _ -> null }
                setCancelable(true)
            }.create().show()
            true
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterNotes(queryText: String) {
        val filterNotes = noteList.filter {
            it.noteTitle.contains(queryText, ignoreCase = true)
        }
        noteList.clear()
        noteList.addAll(filterNotes)
        noteAdapter.notifyDataSetChanged()
        binding.txtNoData.visibility = if (noteList.isEmpty())VISIBLE else GONE
    }

    private fun showAddNoteDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogBinding = DialogAddNoteBinding.inflate(layoutInflater)
        dialog.apply {
            setContentView(dialogBinding.root)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        }.show()

        // Listen for keyboard visibility changes
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)


        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
        dialogBinding.btnAddNote.setOnClickListener {
            if (dialogBinding.etTitle.text.toString()
                    .isEmpty() || dialogBinding.etNote.text.toString().isEmpty()
            ) {
                Toast.makeText(this, "Please enter title and note", Toast.LENGTH_SHORT).show()
            } else {
                val currentTime = Calendar.getInstance().time
                val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val formattedTime = formatter.format(currentTime)

                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormatter.format(currentTime)
                viewModel.insertNote(
                    NoteEntity(
                        noteTitle = dialogBinding.etTitle.text.toString().trim(),
                        noteDesc = dialogBinding.etNote.text.toString().trim(),
                        noteTime = "$formattedDate,$formattedTime"
                    )
                )
                dialog.dismiss()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        viewModel.getAllNotesResponse.observe(this as LifecycleOwner) {
            if (it.isEmpty()) {
                binding.txtNoData.visibility = VISIBLE
                noteList.clear()
                noteAdapter.notifyDataSetChanged()
            } else {
                binding.txtNoData.visibility = GONE
                noteList.clear()
                noteList.addAll(it)
                noteAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onNoteClick(id: Int) {
        isContainerVisible = true
        binding.container.visibility = VISIBLE
        binding.notesRV.visibility = GONE
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.container.id, NoteShowFragment.newInstance(id), null)
        fragmentTransaction.commit()
        binding.searchView.setQuery("", false)
    }

    override fun onNoteDeleteClick(id: Int) {
        AlertDialog.Builder(this).apply {
            setMessage("Are you sure you want to delete note?")
            setPositiveButton("Yes") { _, _ ->
                viewModel.deleteById(id)
            }
            setNegativeButton("No") { _, _ -> null }
            setCancelable(true)
        }.create().show()

    }

    override fun onBackPressed() {
        if (isContainerVisible) {
            binding.container.visibility = GONE
            binding.notesRV.visibility = VISIBLE
            isContainerVisible = false
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}