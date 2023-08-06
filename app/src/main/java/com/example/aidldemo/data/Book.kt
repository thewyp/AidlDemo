package com.example.aidldemo.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * 如果需要 被aidl 定向tag out修饰， 则需要手动实现readFromParcel方法
 */
@Parcelize
data class Book(
    var name: String = "",
    var authorName: String = ""
) : Parcelable {

    fun readFromParcel(_reply: Parcel) {
        name = _reply.readString() ?: ""
        authorName = _reply.readString()?: ""
    }
}