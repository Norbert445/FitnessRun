package com.example.fitnessaplikacia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnessaplikacia.Adapters.RunAdapter
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_runs.*

@AndroidEntryPoint
class RunsFragment : Fragment(R.layout.fragment_runs) {

    private val viewModel: MainViewModel by viewModels()

    lateinit var runAdapter: RunAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set up recyclerView
        runAdapter = RunAdapter()
        setUpRecyclerView()

        setObservers()
    }

    private fun setUpRecyclerView() {
        rvRuns.apply {
            adapter = runAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setObservers() {
        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })
    }
}