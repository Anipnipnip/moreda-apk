package com.example.cd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MlAdapter(
    private val movies: List<Movie>
) : RecyclerView.Adapter<MlAdapter.MlMovieViewHolder>() {

    class MlMovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movieTitle: TextView = itemView.findViewById(R.id.movieTitleml)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MlMovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item_ml, parent, false)
        return MlMovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MlMovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.movieTitle.text = movie.title
    }

    override fun getItemCount(): Int = movies.size
}
