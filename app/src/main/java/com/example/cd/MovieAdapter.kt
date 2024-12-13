package com.example.cd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(
    private val movies: List<Movie>,
    private val wishlistSet: MutableSet<Movie>, // Pass the wishlistSet from the activity/fragment
    private val onWishlistClick: (Movie, Boolean) -> Unit // Notify with the movie and its new state
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movieTitle: TextView = itemView.findViewById(R.id.movieTitle)
        val wishlistButton: ImageButton = itemView.findViewById(R.id.wishlistButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.movieTitle.text = movie.title

        // Set the drawable based on wishlist status
        val isInWishlist = wishlistSet.contains(movie)
        holder.wishlistButton.setImageResource(
            if (isInWishlist) R.drawable.filled else R.drawable.favorite
        )

        // Handle button click
        holder.wishlistButton.setOnClickListener {
            val newState = if (isInWishlist) {
                wishlistSet.remove(movie) // Remove from wishlist
                false
            } else {
                wishlistSet.add(movie) // Add to wishlist
                true
            }
            holder.wishlistButton.setImageResource(
                if (newState) R.drawable.filled else R.drawable.favorite
            )
            onWishlistClick(movie, newState) // Notify activity/fragment about the change
        }
    }


    override fun getItemCount(): Int = movies.size
}
