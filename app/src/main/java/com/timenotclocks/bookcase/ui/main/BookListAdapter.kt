/*
* 2021 Fenimore Love
* */

package com.timenotclocks.bookcase.ui.main

import android.content.Intent
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.squareup.picasso.Picasso
import com.timenotclocks.bookcase.BookEditActivity
import com.timenotclocks.bookcase.BookViewActivity
import com.timenotclocks.bookcase.R
import com.timenotclocks.bookcase.database.Book


val EXTRA_BOOK = "Bookintent"

class BookListAdapter : ListAdapter<Book, BookListAdapter.BookViewHolder>(BOOKS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {

        return BookViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current)
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, BookViewActivity::class.java).apply {

                val book: String = Klaxon().toJsonString(current)
                putExtra(EXTRA_BOOK, book)
            }

            it.context.startActivity(intent)
        }
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.title_view)
        private val subTitleView: TextView = itemView.findViewById(R.id.subtitle_view)
        private val authorView: TextView = itemView.findViewById(R.id.author_view)
        private val coverView: ImageView = itemView.findViewById(R.id.cover_view)
        private val ratingView: RatingBar = itemView.findViewById(R.id.list_rating_bar)
        private val dateView: TextView = itemView.findViewById(R.id.date_added_view)

        fun bind(book: Book?) {
            book?.title.let { titleView.text = it }
            book?.subtitle?.let { subTitleView.text = it }
            book?.author?.let { authorView.text = "by " + it }
            book?.rating?.let{ ratingView.setRating(it.toFloat())}
            book?.dateRead?.let {
                dateView.setText(it.toString())
            } ?: book?.dateAdded?.let {
                dateView.setText(it.toString())
            }
            book?.isbn13?.let {
                val url = "http://covers.openlibrary.org/b/isbn/$it-M.jpg?default=false"
                Picasso.get().load(url).into(coverView)
            }
        }

        companion object {
            fun create(parent: ViewGroup): BookViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return BookViewHolder(view)
            }
        }
    }

    companion object {
        private val BOOKS_COMPARATOR = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }
}
