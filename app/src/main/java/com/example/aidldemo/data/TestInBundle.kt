package com.example.aidldemo.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TestInBundle(
    val id: Int
) : Parcelable
