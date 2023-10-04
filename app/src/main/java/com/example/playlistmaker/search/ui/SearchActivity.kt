package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.search.data.impl.HistoryRepositoryImpl
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.presentation.models.SearchScreenState
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val EDIT_TEXT = "EDIT_TEXT"
        private const val TRACK_HISTORY_PREFERENCES = "track_history_preferences"
    }

    private lateinit var sharedPreferences: SharedPreferences

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory(sharedPreferences)
        )[SearchViewModel::class.java]
    }

    private lateinit var binding: ActivitySearchBinding

    private var editText: String? = null
    private val tracksList = ArrayList<Track>()
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var historyRepositoryImpl: HistoryRepositoryImpl
    private lateinit var searchHistoryAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(TRACK_HISTORY_PREFERENCES, Context.MODE_PRIVATE)

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.getShowPlayerTrigger().observe(this) {
            showPlayerActivity(it)
        }

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
                viewModel.onEditTextChanged(binding.searchField.hasFocus(), s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        binding.searchField.addTextChangedListener(simpleTextWatcher)

        binding.searchRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksAdapter = TrackAdapter(tracksList)
        tracksAdapter.tracks = tracksList
        binding.searchRecyclerView.adapter = tracksAdapter

        tracksAdapter.setOnItemClickListener(object : TrackAdapter.OnListElementClickListener {
            override fun onListElementClick(position: Int) {
                viewModel.addTrackToSearchHistory(tracksAdapter.getTrack(position))
                viewModel.showPlayer(tracksAdapter.getTrack(position))
            }
        })

        binding.clearHistory.setOnClickListener {
            historyRepositoryImpl.clearHistory()
            binding.searchHistory.visibility = View.GONE
            it.visibility = View.GONE
            binding.historyRecyclerView.visibility = View.GONE
        }

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onEditorAction()
            }
            false
        }

        binding.searchField.setOnFocusChangeListener { view, hasFocus ->
            viewModel.onEditFocusChange(hasFocus)
        }

        historyRepositoryImpl = HistoryRepositoryImpl(sharedPreferences)
        searchHistoryAdapter = TrackAdapter(historyRepositoryImpl.getHistory())
        binding.historyRecyclerView.adapter = searchHistoryAdapter

        searchHistoryAdapter.setOnItemClickListener(object :
            TrackAdapter.OnListElementClickListener {
            override fun onListElementClick(position: Int) {
                viewModel.showPlayer(searchHistoryAdapter.getTrack(position))
            }
        })

        binding.refreshButton.setOnClickListener {
            viewModel.onEditorAction()
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

    private fun showPlayerActivity(track: Track) {
        PlayerActivity.show(this, track)
    }

    private fun handleClearButtonClick() {
        binding.searchField.setText("")
        tracksList.clear()
        hideKeyboard()
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

    private fun render(state: SearchScreenState) {
        binding.searchRecyclerView.isVisible = state is SearchScreenState.List && binding.clearIcon.visibility == View.VISIBLE
        binding.nothingFoundMessage.isVisible = state is SearchScreenState.Empty
        binding.somethingWentWrong.isVisible = state is SearchScreenState.Error
        binding.refreshButton.isVisible = state is SearchScreenState.Error
        binding.historyRecyclerView.isVisible = state is SearchScreenState.History
        binding.clearHistory.isVisible = state is SearchScreenState.History && state.tracks.isNotEmpty()
        binding.progressBar.isVisible = state is SearchScreenState.Progress
        when (state) {
            is SearchScreenState.List -> tracksAdapter.addItems(state.tracks)
            is SearchScreenState.Empty -> Unit
            is SearchScreenState.Error -> Unit
            is SearchScreenState.History -> searchHistoryAdapter.addItems(state.tracks)
            is SearchScreenState.Progress -> Unit
        }
    }
}
