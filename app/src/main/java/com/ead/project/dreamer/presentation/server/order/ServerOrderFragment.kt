package com.ead.project.dreamer.presentation.server.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ead.commons.lib.views.justifyInterWord
import com.ead.commons.lib.views.setVisibility
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.utils.DragAndDropCallBack
import com.ead.project.dreamer.databinding.FragmentServerOrderBinding
import com.ead.project.dreamer.presentation.server.order.adapter.ServerOrderRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ServerOrderFragment : Fragment() {

    var description : String = "description"
    var optionOrder : Int = 0

    private var isInfoExpanded = false

    private val buttonApply : Button by lazy { requireActivity().findViewById(R.id.buttonApply) }

    private val viewModel : ServerOrderViewModel by viewModels()
    private lateinit var adapter : ServerOrderRecyclerViewAdapter

    private val itemTouchHelper by lazy { ItemTouchHelper(DragAndDropCallBack) }

    private var _binding : FragmentServerOrderBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServerOrderBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayouts()
        setupServers()
    }

    override fun onResume() {
        super.onResume()
        setupInfo()
        setupApply()
    }

    private fun setupLayouts() {
        binding.apply {
            txvDescription.text = description
            txvDescription.justifyInterWord()

            recyclerView.apply {
                this@ServerOrderFragment.adapter = ServerOrderRecyclerViewAdapter()
                this.adapter = this@ServerOrderFragment.adapter
                layoutManager = LinearLayoutManager(requireActivity())
                itemTouchHelper.attachToRecyclerView(this)
            }

            txvInfo.setOnClickListener {
                isInfoExpanded = !isInfoExpanded
                (requireActivity() as ServerOrderActivity).isInfoExpanded = isInfoExpanded
                binding.txvDescription.setVisibility(isInfoExpanded)
                if (isInfoExpanded) {
                    binding.txvInfo.text = getString(R.string.what_is_this_up)
                }
                else {
                    binding.txvInfo.text = getString(R.string.what_is_this_down)
                }
            }
        }
    }

    private fun setupServers() {
        viewModel.getServers(optionOrder).observe(viewLifecycleOwner) {
            DragAndDropCallBack.itemList = it.toMutableList()
            adapter.submitList(it)
        }
    }

    private fun setupApply() {
        buttonApply.setOnClickListener {
            viewModel.setServers(optionOrder,DragAndDropCallBack.itemList)
            requireActivity().finish()
        }
    }

    private fun setupInfo() {
        isInfoExpanded = (requireActivity() as ServerOrderActivity).isInfoExpanded
        binding.txvDescription.setVisibility(isInfoExpanded)
    }

    companion object {
        const val INTERNAL_SERVERS = -1
        const val EXTERNAL_SERVERS = 0
        const val DOWNLOAD_SERVERS = 1
    }
}