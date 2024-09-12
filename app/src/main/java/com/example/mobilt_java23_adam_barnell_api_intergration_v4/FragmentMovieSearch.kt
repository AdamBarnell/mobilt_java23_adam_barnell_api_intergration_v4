package com.example.mobilt_java23_adam_barnell_api_intergration_v4

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MovieSearchFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var movieImageView: ImageView
    private lateinit var descriptionTextView: TextView
    private lateinit var toPopularMoviesFragmentButton: Button
    private lateinit var requestQueue: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie_search, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)
        searchButton = view.findViewById(R.id.searchButton)
        movieImageView = view.findViewById(R.id.movieImageView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        toPopularMoviesFragmentButton = view.findViewById(R.id.toPopularMoviesFragmentButton)

        requestQueue = Volley.newRequestQueue(requireContext())

        toPopularMoviesFragmentButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_movieSearchFragment_to_popularMoviesFragment)
        }

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                searchMovie(query)
            } else {
                Toast.makeText(requireContext(), "Please enter a movie title", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun searchMovie(query: String) {
        val apiKey = "ed5381eb1e1c381406081dd00ed56ac8"
        val url = "https://api.themoviedb.org/3/search/movie?api_key=$apiKey&query=${query.replace(" ", "%20")}"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val moviesArray = response.getJSONArray("results")

                if (moviesArray.length() > 0) {
                    val firstMovie = moviesArray.getJSONObject(0)
                    val description = firstMovie.getString("overview")
                    val posterPath = firstMovie.optString("poster_path", null)
                    val posterUrl = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }

                    descriptionTextView.text = description
                    if (posterUrl != null) {
                        loadImage(posterUrl)
                    } else {
                        movieImageView.setImageBitmap(null)
                    }
                } else {
                    Toast.makeText(requireContext(), "No movies found", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(requireContext(), "Failed to fetch movies", Toast.LENGTH_SHORT).show()
                Log.e("MovieSearchFragment", "Error fetching data", it)
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun loadImage(url: String) {
        val imageRequest = ImageRequest(
            url,
            { bitmap ->
                movieImageView.setImageBitmap(bitmap)
            },
            0, 0, null, Bitmap.Config.RGB_565,
            { error ->
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                Log.e("MovieSearchFragment", "Error loading image", error)
            }
        )
        requestQueue.add(imageRequest)
    }
}
