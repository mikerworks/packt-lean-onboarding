package com.packt.tellastory.models

import android.os.Parcel
import android.os.Parcelable

class Story : Parcelable {
    var id: String? = null
    var title: String? = null
    var initiator: String? = null
    var lastUpdate: String? = null

    var contributions = mutableListOf<Contribution>()

    constructor() {

    }

    constructor(title: String, initiator: String, lastUpdate: String) {
        this.title = title
        this.initiator = initiator
        this.lastUpdate = lastUpdate
        contributions = ArrayList()
    }

    fun getFullStory(includeAuthors: Boolean): String {
        val builder = StringBuilder()

        if (contributions != null) {
            for (build in contributions?.indices) {
                if (includeAuthors) {
                    builder.append(contributions[build].contributor + ": ")
                }
                builder.append(contributions[build].paragraph.toString() + "\n")
            }
            return builder.toString()

        } else {
            return "This story has not started yet!"
        }
    }

    val summary: String
        get() {
            val builder = StringBuilder()
            if (contributions != null) {
                var start = contributions.size - 3
                if (start <= 0) {
                    start = 0
                }
                for (build in start..contributions.size - 1) {
                    builder.append(contributions[build].paragraph.toString() + "\n")
                }
                return builder.toString()

            } else {
                return "This story has not started yet!"
            }
        }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.id)
        dest.writeTypedList(this.contributions)
    }

    protected constructor(`in`: Parcel) {
        this.id = `in`.readString()
        this.contributions = `in`.createTypedArrayList(Contribution.CREATOR)
    }

    companion object {

        @JvmField val CREATOR: Parcelable.Creator<Story> = object : Parcelable.Creator<Story> {
            override fun createFromParcel(source: Parcel): Story {
                return Story(source)
            }

            override fun newArray(size: Int): Array<Story?> {
                return arrayOfNulls(size)
            }
        }
    }
}
