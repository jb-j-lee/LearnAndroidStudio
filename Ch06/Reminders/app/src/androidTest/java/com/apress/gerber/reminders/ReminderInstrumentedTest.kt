package com.apress.gerber.reminders

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [Testing Fundamentals](http://d.android.com/tools/testing/testing_android.html)
 */
@RunWith(AndroidJUnit4::class)
internal class ReminderInstrumentedTest {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.apress.gerber.reminders", appContext.packageName)
    }

//    @Test
//    fun insertTest() {
//        // Context of the app under test.
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//
//        val max = 10
//        for (i: Int in 1..max) {
//            val item = Reminder(i, "TEST$i", i % 2)
//            ReminderDatabase.getInstance(appContext)?.reminderDao()?.insert(item)
//            var returnItem = ReminderDatabase.getInstance(appContext)?.reminderDao()?.selectById(item._id)
//            assertEquals(item, returnItem)
//        }
//    }
}