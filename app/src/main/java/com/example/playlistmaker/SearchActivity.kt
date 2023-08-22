package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.api.ITunesApi
import com.example.playlistmaker.api.TracksResponse
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.track.Track
import com.example.playlistmaker.track.TrackAdapter
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val EDIT_TEXT = "EDIT_TEXT"
        private const val OK_RESPONSE = 200
        private const val TRACK_HISTORY_PREFERENCES = "track_history_preferences"
        private const val TRACK = "TRACK"
    }

    private lateinit var binding: ActivitySearchBinding
    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var editText: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    private val tracksList = ArrayList<Track>()
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryAdapter: TrackAdapter
    private val itunesService = retrofit.create(ITunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(TRACK_HISTORY_PREFERENCES, Context.MODE_PRIVATE)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.clearIcon.setOnClickListener {
            handleClearButtonClick()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(s)
                editText = s.toString()
                setHistoryVisibility(binding.searchField.hasFocus())
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        fun startPlayerActivity(track: Track) {
            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra(TRACK, Gson().toJson(track))
            startActivity(playerIntent)
        }

        binding.searchField.addTextChangedListener(simpleTextWatcher)

        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksAdapter = TrackAdapter(tracksList)
        tracksAdapter.tracks = tracksList
        binding.searchRecyclerView.adapter = tracksAdapter

        tracksAdapter.setOnItemClickListener(object : TrackAdapter.OnListElementClickListener {
            override fun onListElementClick(position: Int) {
                searchHistory.addToHistory(tracksAdapter.getTrack(position))
                startPlayerActivity(tracksAdapter.getTrack(position))
            }
        })

        binding.clearHistory.setOnClickListener {
            searchHistory.clearHistory()
            binding.searchHistory.visibility = View.GONE
            it.visibility = View.GONE
            binding.historyRecyclerView.visibility = View.GONE
        }

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchAction()
            }
            false
        }

        binding.searchField.setOnFocusChangeListener { view, hasFocus ->
            setHistoryVisibility(hasFocus)
        }

        searchHistory = SearchHistory(sharedPreferences)
        searchHistoryAdapter = TrackAdapter(searchHistory.getHistory())
        binding.historyRecyclerView.adapter = searchHistoryAdapter

        searchHistoryAdapter.setOnItemClickListener(object :
            TrackAdapter.OnListElementClickListener {
            override fun onListElementClick(position: Int) {
                startPlayerActivity(searchHistoryAdapter.getTrack(position))
            }
        })

        binding.refreshButton.setOnClickListener {
            searchAction()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, editText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editText = savedInstanceState.getString(EDIT_TEXT)
        binding.searchField.setText(editText)
    }

    private fun setHistoryVisibility(hasFocus: Boolean) {
        binding.searchHistory.visibility =
            if (hasFocus && binding.searchField.text.isEmpty() && searchHistory.getHistory()
                    .isNotEmpty()
            ) View.VISIBLE else View.GONE
        binding.clearHistory.visibility =
            if (hasFocus && binding.searchField.text.isEmpty() && searchHistory.getHistory()
                    .isNotEmpty()
            ) View.VISIBLE else View.GONE
        binding.historyRecyclerView.visibility =
            if (hasFocus && binding.searchField.text.isEmpty() && searchHistory.getHistory()
                    .isNotEmpty()
            ) View.VISIBLE else View.GONE
    }

    private fun searchAction() {
        itunesService.search(binding.searchField.text.toString())
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.code() == OK_RESPONSE) {
                        tracksList.clear()
                        val results = response.body()?.results
                        if (!results.isNullOrEmpty()) {
                            showNotingFoundMessage("")
                            showSomethingWentWrongMessage("", "")
                            tracksList.addAll(results)
                            tracksAdapter.notifyDataSetChanged()
                        } else {
                            showNotingFoundMessage(getString(R.string.nothing_found))
                        }
                    } else {
                        showSomethingWentWrongMessage(
                            getString(R.string.something_went_wrong),
                            response.code().toString()
                        )
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showSomethingWentWrongMessage(
                        getString(R.string.something_went_wrong),
                        t.message.toString()
                    )
                }
            }
            )
    }

    private fun showNotingFoundMessage(text: String) {
        if (text.isNotEmpty()) {
            tracksList.clear()
            tracksAdapter.notifyDataSetChanged()
            binding.nothingFoundMessage.apply {
                visibility = View.VISIBLE
                this.text = text
            }
            binding.somethingWentWrong.visibility = View.GONE
            binding.refreshButton.visibility = View.GONE
        } else {
            binding.nothingFoundMessage.visibility = View.GONE
        }
    }

    private fun showSomethingWentWrongMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            tracksList.clear()
            tracksAdapter.notifyDataSetChanged()
            binding.somethingWentWrong.apply {
                visibility = View.VISIBLE
                this.text = text
            }
            binding.refreshButton.visibility = View.VISIBLE
            binding.nothingFoundMessage.apply {
                if (visibility == View.VISIBLE) visibility = View.GONE
            }
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            binding.somethingWentWrong.visibility = View.GONE
            binding.refreshButton.visibility = View.GONE
        }
    }

    private fun handleClearButtonClick() {
        binding.searchField.setText("")
        tracksList.clear()
        tracksAdapter.notifyDataSetChanged()
        hideKeyboard()
        searchHistoryAdapter = TrackAdapter(searchHistory.getHistory())
        binding.historyRecyclerView.adapter = searchHistoryAdapter
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val focus = this.currentFocus
        inputMethodManager?.hideSoftInputFromWindow(focus?.windowToken, 0)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}