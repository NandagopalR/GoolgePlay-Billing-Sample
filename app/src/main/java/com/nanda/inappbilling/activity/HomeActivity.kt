package com.nanda.inappbilling.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.*
import com.google.gson.GsonBuilder
import com.nanda.inappbilling.R
import com.nanda.inappbilling.adapter.BooksAdapter
import com.nanda.inappbilling.base.BaseActivity
import com.nanda.inappbilling.data.model.BooksModel
import com.nanda.inappbilling.data.response.BooksResponse
import com.nanda.inappbilling.utils.JavaUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.nio.charset.Charset

class HomeActivity : BaseActivity(), BooksAdapter.BookClickListener, PurchasesUpdatedListener,
    PurchaseHistoryResponseListener {

    private lateinit var adapter: BooksAdapter
    private var booksList = ArrayList<BooksModel>()

    private lateinit var billingClient: BillingClient
    private var productIdsList: List<String>? = null

    companion object {
        fun getCallingIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    loadProducts()
                    queryInventoryAsync()
                }
            }

            override fun onBillingServiceDisconnected() {
                showToast("Service Disconnected...")
            }
        })
        adapter = BooksAdapter(this)
        booksList = getBooksList()
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
        adapter.setBookList(booksList)
    }

    private fun loadProducts() {
        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, this)
    }

    /**
     *
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult?,
        purchaseList: MutableList<Purchase>?
    ) {
        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
            for (purchase in purchaseList!!) {
                acknowledgePurchase(purchase.purchaseToken)
            }
        } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            showToast("You've cancelled the Google play billing process...")
        } else {
            showToast("Item not found or Google play billing error...")
        }
    }

    override fun onPurchaseHistoryResponse(
        billingResult: BillingResult?,
        purchaseHistoryList: MutableList<PurchaseHistoryRecord>?
    ) {
        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
            if (!JavaUtils.isNullOrEmpty(purchaseHistoryList)) {
                for (purchase in purchaseHistoryList!!) {
                    for (books in booksList) {
                        if (purchase.sku == books.id) {
                            books.isPurchased = true
                        }
                    }
                }
            }
        }
        adapter.setBookList(booksList)
    }

    override fun onPurchaseClicked(model: BooksModel) {
        if (!model.isPurchased) {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(model.skuDetail)
                .build()
            billingClient.launchBillingFlow(this, flowParams)
        } else {
            showToast("${model.title} purchased already")
        }
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val debugMessage = billingResult.debugMessage
                showToast("Item Purchased")
            }
        }
    }

    private fun getProductIdsList(modelList: List<BooksModel>?): List<String>? {
        return modelList?.map { model ->
            model.id
        }
    }

    private fun queryInventoryAsync() {
        if (JavaUtils.isNullOrEmpty(productIdsList)) {
            return
        }
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(productIdsList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(
            params.build()
        ) { billingResult: BillingResult, skuDetailsList: List<SkuDetails> ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (!JavaUtils.isNullOrEmpty(skuDetailsList)) {
                    for (item in skuDetailsList.withIndex()) {
                        val skuItem = item.value
                        for (model in booksList) {
                            if (skuItem.sku == model.id) {
                                model.skuDetail = skuItem
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getBooksList(): ArrayList<BooksModel> {
        val json = loadJsonFromAsset()
        val gson = GsonBuilder().serializeNulls().create()
        val response: BooksResponse = gson.fromJson(
            json,
            BooksResponse::class.java
        )
        for (model in response.booksModelList) {
            val id = "${packageName}.${model.id}"
            model.id = id
        }
        productIdsList = getProductIdsList(response.booksModelList)
        return response.booksModelList
    }

    private fun loadJsonFromAsset(): String? {
        val json: String?
        json = try {
            val `is` = assets.open("books.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}
