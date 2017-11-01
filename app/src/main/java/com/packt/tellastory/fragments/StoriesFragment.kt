package com.packt.tellastory.fragments

import android.app.Fragment
import com.packt.tellastory.adapters.StoryAdapter
import com.packt.tellastory.models.Story
import com.packt.tellastory.activities.MainActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.packt.tellastory.R
import com.packt.tellastory.data.OnRepositoryResult
import com.packt.tellastory.adapters.OnCardViewClicked
import kotlinx.android.synthetic.main.fragment_stories.*

class StoriesFragment : Fragment(), OnCardViewClicked, OnRepositoryResult {

    private var recyclerView: RecyclerView? = null
    private var adapter: StoryAdapter? = null
    private var viewModel = mutableListOf<Story>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                     savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_stories, container, false)
        recyclerView = view.findViewById(R.id.stories_recycler_view) as RecyclerView


        return view
    }

    override fun onResume() {
        super.onResume()
        stories_fab_add_story.setOnClickListener { (activity as MainActivity).onCreateStory()  }
        loadData()
    }
    private fun loadData() {
        recyclerView?.layoutManager = LinearLayoutManager(getActivity())
        recyclerView?.itemAnimator = DefaultItemAnimator()
        (activity as MainActivity).repository.getStories(this)
    }

    override fun onCardClicked(view: View, position: Int) {
        (activity as MainActivity).onReadStory(viewModel[position])
    }

    override fun onResult(result: List<Story>) {
        viewModel = result.toMutableList()
        adapter = StoryAdapter(viewModel)
        adapter?.setOnCardViewClicked(this)
        recyclerView?.adapter = adapter
    }

    companion object {
        fun newInstance(): StoriesFragment {
            val fragment = StoriesFragment()
            return fragment
        }
    }
}
