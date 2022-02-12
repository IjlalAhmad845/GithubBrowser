package com.example.githubbrowser.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubbrowser.Activities.CommitsActivity
import com.example.githubbrowser.Activities.DetailActivity
import com.example.githubbrowser.Adapters.BranchesRecyclerAdapter
import com.example.githubbrowser.R
import com.example.githubbrowser.ViewModels.DetailsViewModel
import com.example.githubbrowser.ViewModels.HomeViewModel
import com.example.githubbrowser.databinding.FragmentBranchesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BranchesFragment : Fragment(), BranchesRecyclerAdapter.BranchClickInterface {

    private val TAG = "BranchesFragment"
    private lateinit var binding: FragmentBranchesBinding

    companion object {
        const val BRANCH_CARD_KEY: String = "branch.card.onclick"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_branches, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext() as Activity
        binding.branchRv.layoutManager = LinearLayoutManager(context)
        getBranches(DetailsViewModel.selectedRepo, context)
    }

    private fun getBranches(index: Int, context: Context) =
        CoroutineScope(Dispatchers.Main).launch {

            //API is only called first time
            if (DetailsViewModel.branchesList.size == 0) {
                binding.branchLoadingProgressView.visibility = View.VISIBLE
                binding.branchLoadingTextView.visibility = View.VISIBLE
                //sending api call
                val task = async(Dispatchers.IO) {
                    DetailsViewModel.getBranchesList(
                        HomeViewModel.reposList[index].repoOwner,
                        HomeViewModel.reposList[index].repoName
                    )
                }

                //getting response
                task.await()
                binding.branchLoadingProgressView.visibility = View.GONE
                binding.branchLoadingTextView.visibility = View.GONE
            }

            binding.branchRv.addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayout.VERTICAL
                )
            )

            DetailsViewModel.branchesAdapter =
                BranchesRecyclerAdapter(DetailsViewModel.branchesList, this@BranchesFragment)
            binding.branchRv.adapter = DetailsViewModel.branchesAdapter
        }

    override fun branchOnClick(position: Int) {
        val intent = Intent(requireContext(), CommitsActivity::class.java)
        intent.putExtra(BRANCH_CARD_KEY, position)
        startActivity(intent)
    }
}