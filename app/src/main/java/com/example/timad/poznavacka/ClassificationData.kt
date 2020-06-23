package com.example.timad.poznavacka

import android.os.Parcel
import android.os.Parcelable

data class ClassificationData(var classification: ArrayList<String>?)/* : Parcelable {
    val CREATOR: Parcelable.Creator<*> = object : Parcelable.Creator<Any?> {
        override fun createFromParcel(`in`: Parcel) {
            return ClassificationData(`in`)
        }

        override fun newArray(size: Int): Array<ClassificationData?> {
            return arrayOfNulls(size)
        }
    }


    fun ClassificationData(`in`: Parcel) {
        this.classification = `in`.readArrayList(String::class.java.classLoader) as ArrayList<String>
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeList(this.classification as List<String>?)
    }*/ : Parcelable {
    constructor(parcel: Parcel) : this(classification = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>?) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(classification as List<*>?)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClassificationData> {
        override fun createFromParcel(parcel: Parcel): ClassificationData {
            return ClassificationData(parcel)
        }

        override fun newArray(size: Int): Array<ClassificationData?> {
            return arrayOfNulls(size)
        }
    }
}
