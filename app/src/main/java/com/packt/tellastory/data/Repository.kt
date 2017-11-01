package com.packt.tellastory.data

import android.content.Context
import com.packt.tellastory.models.Contribution
import com.packt.tellastory.models.Story
import java.util.*

class Repository(private val context: Context) {

    fun getStories(handler: OnRepositoryResult) {
        val content = getDummyContent()
        handler.onResult(content)
    }

    fun updateContributions(story: Story) {
        if (story.id == null) {
            addStory(story)
        }

        dummyContentList.forEach {
            if (it.id.equals(story.id, ignoreCase = true)){
                it.contributions  = story.contributions
            }
        }
    }

    fun addStory(story: Story) {
        if (story.id == null) {
            story.id = UUID.randomUUID().toString()
        }
        dummyContentList.add(story)
    }

    companion object {

        private var dummyContentList = mutableListOf<Story>()
        private fun getDummyContent(): List<Story> {

            if (dummyContentList.isEmpty()) {
                val dummy = mutableListOf<Story>()
                val s1 = Story("A first story", "MikeR", "Today")
                s1.id = "1"
                s1.contributions
                s1.contributions.add(Contribution("Once upon a time", "MikeR"))
                s1.contributions.add(Contribution("a giant rabbit did exist", "Pete"))
                s1.contributions.add(Contribution("in a galaxy far faw away", "Floris"))

                val s2 = Story("A second story", "MikeR", "Yesterday")
                s2.id = "2"
                s2.contributions.add(Contribution("Once upon a time", "MikeR"))
                s2.contributions.add(Contribution("a giant rabbit did exist", "Pete"))
                s2.contributions.add(Contribution("in a galaxy far faw away", "Floris"))

                val s3 = Story("A third story", "MikeR", "Two days ago")
                s3.contributions.add(Contribution("Once upon a time", "MikeR"))
                s3.contributions.add(Contribution("a giant rabbit did exist", "Pete"))
                s3.contributions.add(Contribution("in a galaxy far faw away", "Floris"))
                s3.id = "3"

                val s4 = Story("A fourth story", "MikeR", "January 12")
                s4.contributions.add(Contribution("Once upon a time", "MikeR"))
                s4.contributions.add(Contribution("a giant rabbit did exist", "Pete"))
                s4.contributions.add(Contribution("in a galaxy far faw away", "Floris"))
                s4.id = "4"

                dummy.add(s1)
                dummy.add(s2)
                dummy.add(s3)
                dummy.add(s4)

                dummyContentList = dummy
            }
            return dummyContentList
        }
    }
}
