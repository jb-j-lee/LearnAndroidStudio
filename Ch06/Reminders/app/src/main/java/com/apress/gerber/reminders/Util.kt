package com.apress.gerber.reminders

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apress.gerber.reminders.adapter.RemindersAdapter
import com.apress.gerber.reminders.model.entity.Reminder


class Util {

    companion object Utils {
        @JvmStatic
        @BindingAdapter("bindData")
        fun bindItem(recyclerView: RecyclerView, reminder: List<Reminder>?) {
            if (reminder == null) {
                return
            }

            val adapter = recyclerView.adapter as? RemindersAdapter
            adapter?.submitList(reminder)
        }
    }
}