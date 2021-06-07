package com.example.fitnessaplikacia.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessaplikacia.Adapters.RunAdapter
import com.example.fitnessaplikacia.R
import com.example.fitnessaplikacia.models.Run
import com.example.fitnessaplikacia.utility.SwipeToDeleteCallback
import com.example.fitnessaplikacia.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_runs.*
import timber.log.Timber

@AndroidEntryPoint
class RunsFragment : Fragment(R.layout.fragment_runs) {

    private val viewModel: MainViewModel by viewModels()

    lateinit var runAdapter: RunAdapter

    private lateinit var runs: MutableList<Run>

    private val onItemClickListener = object : RunAdapter.OnItemClickListener {
        override fun onItemClick(position: Int) {
            Timber.d("Clicked ${position}. item")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set up recyclerView
        runAdapter = RunAdapter(onItemClickListener)
        setUpRecyclerView()

        setObservers()
    }

    private fun setUpRecyclerView() {
        rvRuns.apply {
            val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    viewModel.deleteRun(runs[viewHolder.adapterPosition])
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeHandler).apply {
                attachToRecyclerView(rvRuns)
            }

            adapter = runAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setObservers() {
        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runs = it as MutableList<Run>
            runAdapter.submitList(runs)

            if(runs.isEmpty()) {
               tvNoRun.visibility = View.VISIBLE
               ivRunCircle.visibility = View.VISIBLE
            } else {
                tvNoRun.visibility = View.GONE
                ivRunCircle.visibility = View.GONE
            }
        })
    }

}