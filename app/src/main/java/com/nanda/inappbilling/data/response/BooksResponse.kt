package com.nanda.inappbilling.data.response

import com.google.gson.annotations.SerializedName
import com.nanda.inappbilling.data.model.BooksModel

class BooksResponse(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("totalItems")
    val totalItems: Int = 0,
    @SerializedName("items")
    val booksModelList: ArrayList<BooksModel>
)