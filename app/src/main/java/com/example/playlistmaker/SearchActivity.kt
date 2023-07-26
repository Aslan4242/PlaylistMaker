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
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.api.ITunesApi
import com.example.playlistmaker.api.TracksResponse
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

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var editText: String? = null
    private lateinit var inputEditText: EditText
    private lateinit var songsList: RecyclerView
    private lateinit var nothingFoundMessage: TextView
    private lateinit var somethingWentWrong: TextView
    private lateinit var refreshButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private val tracksList = ArrayList<Track>()
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var clearButton: ImageView

    private lateinit var searchHistoryList: RecyclerView
    private lateinit var searchHistoryTitle: TextView
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var clearHistoryButton: Button
    private val itunesService = retrofit.create(ITunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        sharedPreferences = getSharedPreferences(TRACK_HISTORY_PREFERENCES, Context.MODE_PRIVATE)

        val backButton = findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener {
            finish()
        }

        nothingFoundMessage = findViewById(R.id.nothingFoundMessage)
        somethingWentWrong = findViewById(R.id.somethingWentWrong)
        inputEditText = findViewById(R.id.search_field)
        songsList = findViewById(R.id.searchRecyclerView)
        refreshButton = findViewById(R.id.refresh_button)
        clearButton = findViewById(R.id.clearIcon)
        searchHistoryTitle = findViewById(R.id.searchHistory)
        searchHistoryList = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistory)

        clearButton.setOnClickListener {
            handleClearButtonClick()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                editText = s.toString()
                setHistoryVisibility(inputEditText.hasFocus())
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

        inputEditText.addTextChangedListener(simpleTextWatcher)

        songsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksAdapter = TrackAdapter(tracksList)
        tracksAdapter.tracks = tracksList
        songsList.adapter = tracksAdapter

        tracksAdapter.setOnItemClickListener(object : TrackAdapter.OnListElementClickListener {
            override fun onListElementClick(position: Int) {
                searchHistory.addToHistory(tracksAdapter.getTrack(position))
                startPlayerActivity(tracksAdapter.getTrack(position))
            }
        })

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            searchHistoryTitle.visibility = View.GONE
            it.visibility = View.GONE
            searchHistoryList.visibility = View.GONE
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchAction()
            }
            false
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            setHistoryVisibility(hasFocus)
        }

        searchHistory = SearchHistory(sharedPreferences)
        searchHistoryAdapter = TrackAdapter(searchHistory.getHistory())
        searchHistoryList.adapter = searchHistoryAdapter

        searchHistoryAdapter.setOnItemClickListener(object :
            TrackAdapter.OnListElementClickListener {
            override fun onListElementClick(position: Int) {
                startPlayerActivity(searchHistoryAdapter.getTrack(position))
            }
        })

        refreshButton.setOnClickListener {
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
        inputEditText.setText(editText)
    }

    private fun setHistoryVisibility(hasFocus: Boolean) {
        searchHistoryTitle.visibility =
            if (hasFocus && inputEditText.text.isEmpty() && searchHistory.getHistory()
                    .isNotEmpty()
            ) View.VISIBLE else View.GONE
        clearHistoryButton.visibility =
            if (hasFocus && inputEditText.text.isEmpty() && searchHistory.getHistory()
                    .isNotEmpty()
            ) View.VISIBLE else View.GONE
        searchHistoryList.visibility =
            if (hasFocus && inputEditText.text.isEmpty() && searchHistory.getHistory()
                    .isNotEmpty()
            ) View.VISIBLE else View.GONE
    }

    private fun searchAction() {
        itunesService.search(inputEditText.text.toString())
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
            nothingFoundMessage.apply {
                visibility = View.VISIBLE
                this.text = text
            }
            somethingWentWrong.visibility = View.GONE
            refreshButton.visibility = View.GONE
        } else {
            nothingFoundMessage.visibility = View.GONE
        }
    }

    private fun showSomethingWentWrongMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            tracksList.clear()
            tracksAdapter.notifyDataSetChanged()
            somethingWentWrong.apply {
                visibility = View.VISIBLE
                this.text = text
            }
            refreshButton.visibility = View.VISIBLE
            nothingFoundMessage.apply {
                if (visibility == View.VISIBLE) visibility = View.GONE
            }
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            somethingWentWrong.visibility = View.GONE
            refreshButton.visibility = View.GONE
        }
    }

    private fun handleClearButtonClick() {
        inputEditText.setText("")
        tracksList.clear()
        tracksAdapter.notifyDataSetChanged()
        hideKeyboard()
        searchHistoryAdapter = TrackAdapter(searchHistory.getHistory())
        searchHistoryList.adapter = searchHistoryAdapter
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