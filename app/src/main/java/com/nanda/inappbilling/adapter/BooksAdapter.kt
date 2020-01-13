package com.nanda.inappbilling.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nanda.inappbilling.R
import com.nanda.inappbilling.data.model.BooksModel

/**
 * Created by Nandagopal on 2020-01-09.
 */
class BooksAdapter(private val listener: BookClickListener) :
    RecyclerView.Adapter<BooksAdapter.BooksViewHolder>() {

    private var context: Context? = null
    private var bookList = ArrayList<BooksModel>()

    fun setBookList(itemList: ArrayList<BooksModel>) {
        bookList.clear()
        bookList.addAll(itemList)
        notifyDataSetChanged()
    }

    interface BookClickListener {
        fun onPurchaseClicked(model: BooksModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        context = parent.context
        return BooksViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_books, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val model = bookList[position]
        holder.bindDataToViews(model)
    }

    inner class BooksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val tvTitle = itemView.findViewById<TextView>(R.id.tv_title)
        private val tvDesc = itemView.findViewById<TextView>(R.id.tv_desc)
        private val imgThumb = itemView.findViewById<ImageView>(R.id.img_thumb)
        private val cbPurchase = itemView.findViewById<CheckBox>(R.id.cb_purchase)

        init {
            itemView.setOnClickListener(this)
        }

        fun bindDataToViews(model: BooksModel) {
            tvTitle.text = model.title
            tvDesc.text = model.shortDescription
            Glide.with(context!!).load(model.thumbnailUrl)
                .apply(RequestOptions.circleCropTransform()).error(R.drawable.ic_empty)
                .placeholder(R.drawable.ic_empty)
                .into(imgThumb)
            cbPurchase.isChecked = model.isPurchased
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position < 0)
                return
            listener.onPurchaseClicked(bookList[position])
        }

    }

}