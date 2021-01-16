package com.android.example.liverpool.ui.search

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isGone
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.example.liverpool.AppExecutors
import com.android.example.liverpool.R
import com.android.example.liverpool.binding.FragmentDataBindingComponent
import com.android.example.liverpool.databinding.SearchProductsFragmentBinding
import com.android.example.liverpool.di.Injectable
import com.android.example.liverpool.ui.common.ProductListAdapter
import com.android.example.liverpool.ui.common.RetryCallback
import com.android.example.liverpool.util.autoCleared
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import javax.inject.Inject

class SearchProductFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<SearchProductsFragmentBinding>()

    var adapter by autoCleared<ProductListAdapter>()

    val searchViewModel: SearchProductViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.search_products_fragment,
                container,
                false,
                dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        initRecyclerView()
        val rvAdapter = ProductListAdapter(
                dataBindingComponent = dataBindingComponent,
                appExecutors = appExecutors,
                showFullName = true
        ) { repo ->
            /*findNavController().navigate(
                    SearchFragmentDirections.showRepo(repo.owner.login, repo.name)
            */
        }
        binding.query = searchViewModel.query
        binding.resultsProductsList.adapter = rvAdapter
        adapter = rvAdapter

        initSearchInputListener()

        binding.callback = object : RetryCallback {
            override fun retry() {
                searchViewModel.refresh()
            }
        }

    }

    private fun initSearchInputListener() {
        binding.searchInput.setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(view)
                true
            } else {
                false
            }
        }
        binding.searchInput.setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(view)
                true
            } else {
                false
            }
        }
    }

    private fun doSearch(v: View) {
        val query = binding.searchInput.text.toString()
        // Dismiss keyboard
        dismissKeyboard(v.windowToken)
        if (query.isEmpty()) {
            binding.searchInput.isGone  = true
        } else {
            searchViewModel.setQuery(query)
        }

    }

    private fun initRecyclerView() {

        binding.resultsProductsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) {
                    searchViewModel.loadNextPage()
                }
            }
        })
        binding.searchResult = searchViewModel.results
        searchViewModel.results.observe(viewLifecycleOwner, Observer { result ->
            adapter.submitList(result?.data)
        })

        searchViewModel.loadMoreStatus.observe(viewLifecycleOwner, Observer { loadingMore ->
            if (loadingMore == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = loadingMore.isRunning
                val error = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

}