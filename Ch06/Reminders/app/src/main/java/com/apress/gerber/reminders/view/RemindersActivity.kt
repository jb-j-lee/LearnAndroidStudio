package com.apress.gerber.reminders.view

import android.annotation.TargetApi
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityRemindersBinding = DataBindingUtil.setContentView(this, R.layout.activity_reminders)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

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
                binding.remindersListView.adapter = mCursorAdapter
            }
        }

        binding.remindersListView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, masterListPosition: Int, _: Long ->
            val builder = AlertDialog.Builder(this@RemindersActivity)
            val modeListView = ListView(this@RemindersActivity)
            val modes = arrayOf(getString(R.string.dialog_title_update), getString(R.string.dialog_title_delete))
            val modeAdapter = ArrayAdapter(
                this@RemindersActivity,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                modes
            )
            modeListView.adapter = modeAdapter
            builder.setView(modeListView)
            val dialog: Dialog = builder.create()
            dialog.show()
            modeListView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                if (position == 0) {
                    val nId = getIdFromPosition(masterListPosition)
                    CoroutineScope(Dispatchers.IO).launch {
                        val reminder = viewModel.selectById(nId)
                        withContext(Dispatchers.Main) {
                            fireCustomDialog(reminder)
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.deleteById(getIdFromPosition(masterListPosition))

                        val cursor = viewModel.selectCursor()

                        withContext(Dispatchers.Main) {
                            mCursorAdapter?.changeCursor(cursor)
                        }
                    }
                }
                dialog.dismiss()
            }
        }
        binding.remindersListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
        binding.remindersListView.setMultiChoiceModeListener(object : MultiChoiceModeListener {
            override fun onItemCheckedStateChanged(
                mode: ActionMode, position: Int,
                id: Long, checked: Boolean
            ) {
            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val inflater = mode.menuInflater
                inflater.inflate(R.menu.cam_menu, menu)
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
                                if (binding.remindersListView.isItemChecked(count)) {
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
            R.id.action_new -> {
                fireCustomDialog(null)
                true
            }
            R.id.action_exit -> {
                finish()
                true
            }
            else -> false
        }
    }

    private fun fireCustomDialog(reminder: Reminder?) {
        val isEditOperation = reminder != null

        val builder = AlertDialog.Builder(this, R.style.AppDialog)

        val view: DialogCustomBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_custom, null, false);
        builder.setView(view.root)

        val alertDialog = builder.create()

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Commit") { dialog, _ ->
            val reminderText = view.edittext.text.toString()
            if (isEditOperation) {
                val reminderEdited = Reminder(
                    reminderText, if (view.checkbox.isChecked) 1 else 0
                )
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.update(reminderEdited)
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.insert(Reminder(reminderText, if (view.checkbox.isChecked) 1 else 0))
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                val cursor = viewModel.selectCursor()
                withContext(Dispatchers.Main) {
                    mCursorAdapter?.changeCursor(cursor)
                }
            }
            dialog.dismiss()
        }
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { dialog, _ -> dialog.dismiss() }

        val editCustom = view.edittext
        val checkBox = view.checkbox

        if (isEditOperation) {
            alertDialog.setTitle(R.string.dialog_title_update)
            checkBox.isChecked = reminder?.important == 1
            editCustom.setText(reminder?.content)
        } else {
            alertDialog.setTitle(R.string.dialog_title_create)
        }

//        val buttonCancel = dialog.findViewById<View>(R.id.custom_button_cancel) as Button
//        buttonCancel.setOnClickListener { dialog.dismiss() }
        alertDialog.show()
    }

    fun test(): String {
        return "test"
    }
}