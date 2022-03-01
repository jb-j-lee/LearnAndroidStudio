package com.apress.gerber.reminders.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.AbsListView.MultiChoiceModeListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.apress.gerber.reminders.R
import com.apress.gerber.reminders.adapter.RemindersAdapter
import com.apress.gerber.reminders.adapter.RemindersSimpleCursorAdapter
import com.apress.gerber.reminders.databinding.ActivityRemindersBinding
import com.apress.gerber.reminders.databinding.DialogCustomBinding
import com.apress.gerber.reminders.model.entity.Reminder
import com.apress.gerber.reminders.viewmodel.ReminderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemindersActivity : AppCompatActivity() {
    private var mCursorAdapter: RemindersSimpleCursorAdapter? = null

    private val viewModel by lazy { ViewModelProvider(this)[ReminderViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityRemindersBinding = DataBindingUtil.setContentView(this, R.layout.activity_reminders)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = RemindersAdapter()
        binding.recyclerview.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch {
            val cursor = viewModel.selectCursor()

            withContext(Dispatchers.Main) {
                val from = arrayOf<String?>(
                        RemindersSimpleCursorAdapter.COL_CONTENT
                )
                val to = intArrayOf(
                        R.id.row_text
                )
                mCursorAdapter = RemindersSimpleCursorAdapter(
                        this@RemindersActivity,
                        R.layout.reminders_row,
                        cursor,
                        from,
                        to,
                        0
                )
                binding.listview.adapter = mCursorAdapter
            }
        }

        binding.listview.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, masterListPosition: Int, _: Long ->
            fetchActionDialog(masterListPosition)
        }
        binding.listview.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        binding.listview.setMultiChoiceModeListener(object : MultiChoiceModeListener {
            override fun onItemCheckedStateChanged(mode: ActionMode, position: Int, id: Long, checked: Boolean) {
            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                mode.menuInflater.inflate(R.menu.cam_menu, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.menu_item_delete_reminder -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            var count = mCursorAdapter!!.count - 1
                            while (count >= 0) {
                                if (binding.listview.isItemChecked(count)) {
                                    viewModel.deleteById(getIdFromPosition(count))
                                }
                                count--
                            }

                            val cursor = viewModel.selectCursor()
                            withContext(Dispatchers.Main) {
                                mCursorAdapter?.changeCursor(cursor)

                                mode.finish()
                            }
                        }
                        return true
                    }
                }
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {}
        })
    }

    private fun getIdFromPosition(position: Int): Int {
        return mCursorAdapter!!.getItemId(position).toInt()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_reminders, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_create -> {
                fetchEditReminderDialog(null)
                true
            }
            R.id.action_delete_all -> {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.deleteAll()

                    val cursor = viewModel.selectCursor()
                    withContext(Dispatchers.Main) {
                        mCursorAdapter?.changeCursor(cursor)
                    }
                }
                true
            }
            R.id.action_exit -> {
                finish()
                true
            }
            else -> false
        }
    }

    private fun fetchEditReminderDialog(reminder: Reminder?) {
        val builder = AlertDialog.Builder(this)

        val binding: DialogCustomBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_custom, null, false)
        builder.setView(binding.root)

        val isEditMode = reminder != null
        builder.setTitle(if (isEditMode) R.string.dialog_update else R.string.dialog_create)
        if (isEditMode) {
            binding.content = reminder?.content
            binding.important = reminder?.important == 1
        }

        val alertDialog = builder.create()

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.dialog_button_ok)) { dialog, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                val item = Reminder(binding.content.toString(), if (binding.important) 1 else 0)
                if (isEditMode) {
                    item.id = reminder!!.id
                    viewModel.update(item)
                } else {
                    viewModel.insert(item)
                }

                val cursor = viewModel.selectCursor()
                withContext(Dispatchers.Main) {
                    mCursorAdapter?.changeCursor(cursor)
                    dialog.dismiss()
                }
            }
        }
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dialog_button_cancel)) { dialog, _ -> dialog.dismiss() }

        alertDialog.show()
    }

    private fun fetchActionDialog(masterListPosition: Int) {
        val builder = AlertDialog.Builder(this)

        val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                arrayOf(getString(R.string.dialog_update), getString(R.string.dialog_delete))
        )
        builder.setAdapter(adapter) { dialog, which ->
            CoroutineScope(Dispatchers.IO).launch {
                val id = getIdFromPosition(masterListPosition)
                if (which == 0) {
                    val reminder = viewModel.selectById(id)
                    withContext(Dispatchers.Main) {
                        fetchEditReminderDialog(reminder)
                    }
                } else {
                    viewModel.deleteById(id)

                    val cursor = viewModel.selectCursor()
                    withContext(Dispatchers.Main) {
                        mCursorAdapter?.changeCursor(cursor)
                    }
                }
            }
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun test(): String {
        return "test"
    }
}