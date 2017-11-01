package com.packt.tellastory.fragments

import android.app.Fragment
import com.packt.tellastory.activities.MainActivity
import com.packt.tellastory.AuthenticationHelper
import com.packt.tellastory.models.Contribution
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.packt.tellastory.R
import com.packt.tellastory.models.Story
import kotlinx.android.synthetic.main.fragment_story_contribute.*

class StoryContributeFragment : Fragment() {

    private var mStory: Story? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStory = getArguments().getParcelable(ARG_STORY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                     savedInstanceState: Bundle?): View {

       return inflater.inflate(R.layout.fragment_story_contribute, container, false)
    }

    override fun onResume() {
        super.onResume()

        if (mStory?.id == null) {
            contribute_text_summary.visibility = View.GONE
            contribute_text_previously.setVisibility(View.GONE)
        } else {
            contribute_edit_title.setVisibility(View.GONE)
            contribute_text_title.setVisibility(View.GONE)
            contribute_text_summary.setText(mStory?.summary)
        }

        contribute_button_add.setOnClickListener { onContribute() }
    }

    private fun onContribute() {
        if (contribute_edit.text.toString().length > 0) {

            val contribution = Contribution()
            contribution.paragraph = contribute_edit.text.toString()

            if (mStory?.id == null) {
                mStory?.title = contribute_edit_title.text.toString()
            }

            mStory?.contributions?.add(contribution)

            if (AuthenticationHelper.isAuthenticated) {
                contribution.contributor = AuthenticationHelper.userName

                val updateStory = mStory
                if (updateStory !=  null) {
                    (activity as MainActivity).repository.updateContributions(updateStory)
                    (activity as MainActivity).onList()
                }
            }
            else {
                val updateStory = mStory
                if (updateStory != null) {
                    (activity as MainActivity).onLateOnboarding(updateStory)
                }
            }
        }
    }

    companion object {

        private val ARG_STORY = "ARG_STORY"

        fun newInstance(story: Story): StoryContributeFragment {
            val fragment = StoryContributeFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_STORY, story)
            fragment.setArguments(bundle)
            return fragment
        }
    }
}
