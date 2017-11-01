package com.packt.tellastory.models

import android.os.Parcel
import android.os.Parcelable

class Contribution : Parcelable {
    var id: String? = null
    var paragraph: String?= null
    var contributor: String? = null

    constructor() {

    }

    constructor(paragraph: String, contributor: String) {
        this.paragraph = paragraph
        this.contributor = contributor
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.id)
        dest.writeString(this.paragraph)
        dest.writeString(this.contributor)
    }

    protected constructor(`in`: Parcel) {
        this.id = `in`.readString()
        this.paragraph = `in`.readString()
        this.contributor = `in`.readString()
    }

    companion object {

        @JvmField val CREATOR: Parcelable.Creator<Contribution> = object : Parcelable.Creator<Contribution> {
            override fun createFromParcel(source: Parcel): Contribution {
                return Contribution(source)
            }

            override fun newArray(size: Int): Array<Contribution?> {
                return arrayOfNulls(size)
            }
        }
    }

}
