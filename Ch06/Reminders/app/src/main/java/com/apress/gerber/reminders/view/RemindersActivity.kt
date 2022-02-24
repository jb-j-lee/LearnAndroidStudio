package com.apress.gerber.reminders.view

import android.annotation.TargetApi
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.AbsListView.MultiChoiceModeListener
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.apress.gerber.reminders.R
import com.apress.gerber.reminders.adapter.RemindersSimpleCursorAdapter
import com.apress.gerber.reminders.databinding.ActivityRemindersBinding
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

        val actionBar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setIcon(R.mipmap.ic_launcher)

        CoroutineScope(Dispatchers.IO).launch {
            //TODO
            val cursor = viewModel.selectCursor()

            withContext(Dispatchers.Main) {
                val from = arrayOf<String?>(
                    RemindersSimpleCursorAdapter.COL_CONTENT
                )
                val to = intArrayOf(
                    R.id.row_text
                )
                mCursorAdapter = RemindersSimpleCursorAdapter( // 컨텍스트
                    this@RemindersActivity,  // 행의 레이아웃
                    R.layout.reminders_row,  // 커서
                    cursor,  // 데이터베이스에 정의된 칼럼
                    from,  // 레이아웃의 뷰 id
                    to,  // 플래그이며 사용하지 않음
                    0
                )

                // 이제는 cursorAdapter(MVC 의 컨트롤러)가
                // db(MVC 의 모델)의 데이터로 ListView(MVC 의 뷰)를 갱신한다.
                binding.remindersListView.adapter = mCursorAdapter
            }
        }

        // ListView 의 항목을 터치할 때 이 리스너의 onItemClick() 메서드가 호출된다
        binding.remindersListView.onItemClickListener = OnItemClickListener { _: AdapterView<*>?, _: View?, masterListPosition: Int, _: Long ->
            val builder = AlertDialog.Builder(this@RemindersActivity)
            val modeListView = ListView(this@RemindersActivity)
            val modes = arrayOf("Edit Reminder", "Delete Reminder")
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
                // 메모 데이터 변경
                if (position == 0) {
                    val nId = getIdFromPosition(masterListPosition)
                    CoroutineScope(Dispatchers.IO).launch {
                        val reminder = viewModel.selectById(nId)
                        withContext(Dispatchers.Main) {
                            fireCustomDialog(reminder)
                        }
                    }
                    // 메모 데이터 삭제
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

    private fun getIdFromPosition(nC: Int): Int {
        return mCursorAdapter!!.getItemId(nC).toInt()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // 메뉴를 인플레이트하여 객체로 생성한다
        menuInflater.inflate(R.menu.menu_reminders, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_new -> {
                // 새로운 메모 생성
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
        // 커스텀 대화상자
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_custom)
        val titleView = dialog.findViewById<View>(R.id.custom_title) as TextView
        val editCustom = dialog.findViewById<View>(R.id.custom_edit_reminder) as EditText
        val commitButton = dialog.findViewById<View>(R.id.custom_button_commit) as Button
        val checkBox = dialog.findViewById<View>(R.id.custom_check_box) as CheckBox
        val isEditOperation = reminder != null
        if (isEditOperation) {
            titleView.text = "Edit Reminder"
            checkBox.isChecked = reminder?.important == 1
            editCustom.setText(reminder?.content)
        }
        commitButton.setOnClickListener {
            val reminderText = editCustom.text.toString()
            if (isEditOperation) {
                val reminderEdited = Reminder(
                    reminder!!._id,
                    reminderText, if (checkBox.isChecked) 1 else 0
                )
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.update(reminderEdited)
                }

                // 새로운 메모 생성
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.insert(Reminder(0, reminderText, if (checkBox.isChecked) 1 else 0))
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
        val buttonCancel = dialog.findViewById<View>(R.id.custom_button_cancel) as Button
        buttonCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun test(): String {
        return "test"
    }
}