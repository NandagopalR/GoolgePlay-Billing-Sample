package com.nanda.inappbilling.data.model

import com.android.billingclient.api.SkuDetails
import com.google.gson.annotations.SerializedName

/**
 * Created by Nandagopal on 2020-01-09.
 */
data class BooksModel(
    @SerializedName("id")
    var id: String,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("publishedDate")
    var publishedDate: String? = null,
    @SerializedName("thumbnailUrl")
    var thumbnailUrl: String? = null,
    @SerializedName("shortDescription")
    var shortDescription: String? = null,
    @SerializedName("longDescription")
    var longDescription: String? = null,
    @SerializedName("authors")
    var authors: String? = null,
    var skuDetail: SkuDetails? = null,
    var isPurchased: Boolean = false
)