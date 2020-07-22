package com.cheng.automate.core.helper

import android.os.Parcel
import android.os.Parcelable

/**
 * @author zijian.cheng
 * @date 2020/7/22
 */
class OnlyClickNodeInfo() : Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OnlyClickNodeInfo> {
        override fun createFromParcel(parcel: Parcel): OnlyClickNodeInfo {
            return OnlyClickNodeInfo(parcel)
        }

        override fun newArray(size: Int): Array<OnlyClickNodeInfo?> {
            return arrayOfNulls(size)
        }
    }
}