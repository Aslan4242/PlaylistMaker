package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.presentation.models.SearchScreenState
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    companion object {
        private const val EDIT_TEXT = "EDIT_TEXT"
    }

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModel()
    private var editText: String? = null
    private val tracksList = ArrayList<Track>()
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.getShowPlayerTrigger().observe(viewLifecycleOwner) {
            showPlayerActivity(it)
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
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
            viewModel.onClearSearchHistoryButtonClick()
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

        searchHistoryAdapter = TrackAdapter(ArrayList())
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

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        editText = savedInstanceState?.getString(EDIT_TEXT)
        binding.searchField.setText(editText)
    }

    private fun showPlayerActivity(track: Track) {
        findNavController().navigate(
            R.id.action_searchFragment_to_playerActivity,
            PlayerActivity.createArgs(track)
        )
    }

    private fun handleClearButtonClick() {
        binding.searchField.setText("")
        tracksList.clear()
        hideKeyboard(binding.searchField)
    }

    private fun hideKeyboard(field: View) {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(field.windowToken, 0)
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
