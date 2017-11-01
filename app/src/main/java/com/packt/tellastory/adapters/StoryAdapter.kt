package com.packt.tellastory.adapters

import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.packt.tellastory.R
import com.packt.tellastory.models.Story

class StoryAdapter(private val mViewModel: List<Story>?) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    private var onCardViewClicked: OnCardViewClicked? = null

    fun setOnCardViewClicked(handler: OnCardViewClicked) {
        this.onCardViewClicked = handler
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_stories, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = mViewModel
        if (model != null) {
            val entry = model[position]
            holder.titleText.setText(entry.title)
            holder.summary.setText(entry.summary)

            holder.view.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    if (onCardViewClicked != null) {
                        onCardViewClicked!!.onCardClicked(v, position)
                    }
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return mViewModel?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var summary: TextView
        var titleText: TextView
        var view: View

        init {
            view = itemView.findViewById(R.id.story_layout)
            titleText = itemView.findViewById(R.id.story_text_title) as TextView
            summary = itemView.findViewById(R.id.story_text_summary) as TextView
        }
    }
}
