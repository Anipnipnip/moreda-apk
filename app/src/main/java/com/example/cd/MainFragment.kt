package com.example.cd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var movieAdapter: MovieAdapter

    private val movieList = mutableListOf<Movie>() // Original list
    private val filteredList = mutableListOf<Movie>() // List displayed in RecyclerView
    private val wishlistSet = mutableSetOf<Movie>() // Track wishlist movies

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the fragment
        return inflater.inflate(R.layout.list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter with wishlistSet
        movieAdapter = MovieAdapter(filteredList, wishlistSet) { movie, isAdded ->
            if (isAdded) {
                addToWishlist(movie)
            }
        }
        recyclerView.adapter = movieAdapter

        fetchMovies()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterMovies(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterMovies(it) }
                return true
            }
        })

    }

    private fun addToWishlist(movie: Movie) {
        val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)

        if (!TextUtils.isEmpty(token)) {
            val apiService = RetrofitInstance.api
            apiService.addWishlist("Bearer $token", movie.movieId).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        wishlistSet.add(movie) // Add to local wishlist
                        Toast.makeText(requireContext(), "${movie.title} added to Wishlist!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to add to Wishlist: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "No token found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun filterMovies(query: String) {
        val lowerCaseQuery = query.lowercase()
        filteredList.clear()

        if (query.isEmpty()) {
            filteredList.addAll(movieList)
        } else {
            filteredList.addAll(movieList.filter { it.title.lowercase().contains(lowerCaseQuery) })
        }

        movieAdapter.notifyDataSetChanged()
    }

    private fun fetchMovies() {
        val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)

        if (!TextUtils.isEmpty(token)) {
            val apiService = RetrofitInstance.api
            apiService.searchMoviesByTitle("Bearer $token", "").enqueue(object :
                Callback<MovieResponse> {
                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { movieResponse ->
                            val validMovies = movieResponse.judul.filter { it.title != "title" }
                            movieList.clear()
                            movieList.addAll(validMovies)

                            filteredList.clear()
                            filteredList.addAll(movieList)

                            movieAdapter.notifyDataSetChanged()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to fetch movies: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "No token found", Toast.LENGTH_SHORT).show()
        }
    }

}