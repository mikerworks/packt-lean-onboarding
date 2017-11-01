package com.packt.tellastory.fragments

import android.app.Fragment
import com.packt.tellastory.activities.MainActivity
import com.twitter.sdk.android.tweetcomposer.TweetComposer
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.packt.tellastory.R
import com.packt.tellastory.models.Story
import kotlinx.android.synthetic.main.fragment_story_detail.*

class StoryDetailFragment : Fragment() {

    private var mStory: Story? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStory = getArguments().getParcelable(ARG_STORY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                     savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_story_detail, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        val displayStory = mStory
        if (displayStory != null) {
            story_detail_text.setText(displayStory.getFullStory(true))
        }

        story_detail_button_contribute.setOnClickListener { onContribute() }
        story_detail_button_share.setOnClickListener { onShare() }
    }

    private fun onShare() {
        val builder = TweetComposer.Builder(getActivity())
                .text(String.format(getString(R.string.sharing_text), mStory?.title))
        builder.show()
    }

    private fun onContribute() {
        val displayStory = mStory
        if (displayStory != null) {
            (activity as MainActivity).onContribute(displayStory)
        }
    }

    companion object {

        private val ARG_STORY = "ARG_STORY"

        fun newInstance(story: Story): StoryDetailFragment {
            val fragment = StoryDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_STORY, story)
            fragment.setArguments(bundle)
            return fragment
        }
    }
}
