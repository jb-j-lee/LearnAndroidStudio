package com.apress.gerber.reminders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.apress.gerber.reminders.databinding.RemindersRowBinding
import com.apress.gerber.reminders.model.entity.Reminder

class RemindersAdapter : ListAdapter<Reminder, RemindersAdapter.MyHolder>(ReminderDiffUtil) {
    private var mList = mutableListOf<Reminder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding =
            RemindersRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyHolder(binding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.onBind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onCurrentListChanged(
        previousList: MutableList<Reminder>,
        currentList: MutableList<Reminder>
    ) {
        mList = currentList
    }

    inner class MyHolder(private val binding: RemindersRowBinding) : ViewHolder(binding.root) {
        fun onBind(reminder: Reminder) {
            binding.reminder = reminder
            binding.root.setOnLongClickListener {
                //TODO Injection or https://parkho79.tistory.com/153?category=798724
                return@setOnLongClickListener true
            }
//            binding.executePendingBindings()
        }
    }

    companion object ReminderDiffUtil : DiffUtil.ItemCallback<Reminder>() {
        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem.content == newItem.content && oldItem.important == newItem.important && oldItem.id == newItem.id
        }

        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem == newItem
        }
    }
}