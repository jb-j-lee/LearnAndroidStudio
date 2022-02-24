package com.apress.gerber.reminders.adapter

import android.content.Context
import android.database.Cursor
import android.view.View
import android.view.ViewGroup
import androidx.cursoradapter.widget.SimpleCursorAdapter
import com.apress.gerber.reminders.R

class RemindersSimpleCursorAdapter(
    context: Context?, layout: Int, c: Cursor?,
    from: Array<String?>?, to: IntArray?, flags: Int
) : SimpleCursorAdapter(context, layout, c, from, to, flags) {
    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return super.newView(context, cursor, parent)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        super.bindView(view, context, cursor)
        var holder = view.tag
        if (holder == null) {
            holder = ViewHolder()
            holder.colImp = cursor.getColumnIndexOrThrow(COL_IMPORTANT)
            holder.listTab = view.findViewById(R.id.row_tab)
            view.tag = holder
        }
        holder as ViewHolder
        if (cursor.getInt(holder.colImp) > 0) {
            holder.listTab!!.setBackgroundColor(context.resources.getColor(R.color.orange))
        } else {
            holder.listTab!!.setBackgroundColor(context.resources.getColor(R.color.green))
        }
    }

    internal class ViewHolder {
        // 칼럼 인덱스를 저장한다
        var colImp = 0

        // 뷰를 저장한다
        var listTab: View? = null
    }

    companion object {
        const val COL_CONTENT = "content"
        private const val COL_IMPORTANT = "important"
    }
}