package com.example.aidldemo.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Author(
    val name: String,
    val books: List<Book>
): Parcelable
