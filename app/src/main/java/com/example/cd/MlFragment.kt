package com.example.cd
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MlFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateTextView: TextView
    private lateinit var movieAdapter: MlAdapter // Correct adapter for displaying wishlist movies

    private val wishlistMovies = mutableListOf<Movie>() // Wishlist movies data

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ml, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.mlview)
        emptyStateTextView = view.findViewById(R.id.emptyStateTextView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        movieAdapter = MlAdapter(wishlistMovies) // Initialize MlAdapter
        recyclerView.adapter = movieAdapter

        fetchWishlist() // Fetch the wishlist data
    }

    private fun fetchWishlist() {
        val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)

        if (token != null) {
            val apiService = RetrofitInstance.api
            apiService.getWishlist("Bearer $token").enqueue(object : Callback<MovieResponseML> {
                override fun onResponse(call: Call<MovieResponseML>, response: Response<MovieResponseML>) {
                    if (response.isSuccessful) {
                        response.body()?.let { movieResponse ->
                            val movies = movieResponse.data // Update based on API response format
                            if (movies.isNotEmpty()) {
                                recyclerView.visibility = View.VISIBLE
                                emptyStateTextView.visibility = View.GONE
                                wishlistMovies.clear()
                                wishlistMovies.addAll(movies)
                                movieAdapter.notifyDataSetChanged()
                            } else {
                                recyclerView.visibility = View.GONE
                                emptyStateTextView.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to fetch wishlist: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<MovieResponseML>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "No token found", Toast.LENGTH_SHORT).show()
        }
    }
}
