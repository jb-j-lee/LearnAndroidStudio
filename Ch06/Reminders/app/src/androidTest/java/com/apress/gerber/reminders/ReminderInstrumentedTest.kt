package com.apress.gerber.reminders

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.apress.gerber.reminders.view.RemindersActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [Testing Fundamentals](http://d.android.com/tools/testing/testing_android.html)
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ReminderInstrumentedTest {

    private lateinit var testString: String

    @get:Rule
    var activityRule: ActivityTestRule<RemindersActivity> = ActivityTestRule(RemindersActivity::class.java)

    @Before
    fun setUp() {
        testString = "test"
    }

    @After
    fun tearDown() {
        testString = ""
    }

    @Test
    fun testPackageName() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.apress.gerber.reminders", appContext.packageName)
    }

    @Test
    fun testInsert() {
        val result = activityRule.activity.test()

//        openActionBarOverflowOrOptionsMenu(activityRule.activity)
//        onView(withId(R.id.action_new)).perform(click())

        assertEquals(testString, result)
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