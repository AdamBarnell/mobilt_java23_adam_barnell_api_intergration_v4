package com.example.mobilt_java23_adam_barnell_api_intergration_v4

import android.app.DownloadManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class PopularMoviesFragment : Fragment() {

    private lateinit var moviesContainer: LinearLayout
    private lateinit var toSearchFragmentButton: Button
    private lateinit var requestQueue: RequestQueue
    //This fragment handles the searchrequest from the api
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_popular_movies, container, false)
        moviesContainer = view.findViewById(R.id.moviesContainer)
        toSearchFragmentButton = view.findViewById(R.id.toSearchFragmentButton)
        requestQueue = Volley.newRequestQueue(requireContext())

        toSearchFragmentButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_popularMoviesFragment_to_movieSearchFragment)
        }

        fetchPopularMovies()

        return view
    }

    private fun fetchPopularMovies() {
        val apiKey = "ed5381eb1e1c381406081dd00ed56ac8"
        val url = "https://api.themoviedb.org/3/movie/popular?api_key=$apiKey&language=en-US&page=1"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val moviesArray = response.getJSONArray("results")
                displayMovies(moviesArray)
            },
            {
                Toast.makeText(requireContext(), "Failed to get movies", Toast.LENGTH_SHORT).show()
                Log.e("Adam", "Error getting data", it)
            }
        )

        requestQueue.add(jsonObjectRequest)
    }
    // Fetches the title and posters for the movies
    private fun displayMovies(moviesArray: JSONArray) {
        moviesContainer.removeAllViews()
        for (i in 0 until Math.min(moviesArray.length(), 10)) {
            val movieObject = moviesArray.getJSONObject(i)
            val title = movieObject.getString("title")
            val posterPath = movieObject.optString("poster_path", null)
            val posterUrl = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }

            val titleTextView = TextView(requireContext())
            titleTextView.text = title
            titleTextView.textSize = 24f
            titleTextView.setPadding(0, 8, 0, 8)

            val posterImageView = ImageView(requireContext())
            posterImageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1400
            ).apply {
                setMargins(0, 8, 0, 16)
            }
            posterImageView.scaleType = ImageView.ScaleType.CENTER_CROP

            if (posterUrl != null) {
                loadImage(posterUrl, posterImageView)
            } else {
                posterImageView.setImageResource(android.R.color.transparent)
            }
            moviesContainer.addView(titleTextView)
            moviesContainer.addView(posterImageView)
        }
    }

    private fun loadImage(url: String, imageView: ImageView) {
        val imageRequest = ImageRequest(
            url,
            { bitmap ->
                imageView.setImageBitmap(bitmap)
            },
            0, 0, null, Bitmap.Config.RGB_565,
            { error ->
                Toast.makeText(requireContext(), "Failed to getimage", Toast.LENGTH_SHORT).show()
                Log.e("Adam", "Error loading image", error)
            }
        )

        requestQueue.add(imageRequest)
    }
}
