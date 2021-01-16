package com.android.example.liverpool.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.example.liverpool.AppExecutors
import com.android.example.liverpool.R
import com.android.example.liverpool.binding.FragmentDataBindingComponent
import com.android.example.liverpool.databinding.HistoryFragmentBinding
import com.android.example.liverpool.databinding.SearchProductsFragmentBinding
import com.android.example.liverpool.di.Injectable
import com.android.example.liverpool.ui.common.HistoryListAdapter
import com.android.example.liverpool.ui.common.ProductListAdapter
import com.android.example.liverpool.ui.common.RetryCallback
import com.android.example.liverpool.ui.common.TaskListener
import com.android.example.liverpool.ui.search.SearchProductViewModel
import com.android.example.liverpool.util.autoCleared
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HistoryFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<HistoryFragmentBinding>()

    var adapter by autoCleared<HistoryListAdapter>()

    val historyViewModel: HistoryViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.history_fragment,
                container,
                false,
                dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        val rvAdapter = HistoryListAdapter(
                dataBindingComponent = dataBindingComponent,
                appExecutors = appExecutors,
                repoClickCallback = {item -> {
                    Timber.i(item.query)
                }} ,
                object : TaskListener {
                    override fun onTaskClick(task: String) {
                        Timber.d("Delete selected ${task}");
                        GlobalScope.launch(Dispatchers.IO) {
                            historyViewModel.deleteItems.deleteItem(task)
                        }
                    }
                }

        )
        historyViewModel.listHistory.observe(viewLifecycleOwner, Observer { result ->
            adapter.submitList(result)
        })
        binding.historyList.adapter = rvAdapter
        adapter = rvAdapter

        binding.deleteAllBtn.setOnClickListener{

            GlobalScope.launch(Dispatchers.IO) {
                historyViewModel.deleteItems.deleteAllItems();
            }
        }




    }

}