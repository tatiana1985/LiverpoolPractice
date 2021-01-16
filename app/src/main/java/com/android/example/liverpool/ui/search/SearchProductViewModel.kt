package com.android.example.liverpool.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.android.example.liverpool.repository.PlpRepository
import com.android.example.liverpool.testing.OpenForTesting
import com.android.example.liverpool.util.AbsentLiveData
import com.android.example.liverpool.vo.Product
import com.android.example.liverpool.vo.Resource
import com.android.example.liverpool.vo.Status
import java.util.*
import javax.inject.Inject

@OpenForTesting
class SearchProductViewModel @Inject constructor(plpRepository: PlpRepository) : ViewModel(){

    private val _query = MutableLiveData<String>()
    private val nextPageHandler = NextPageHandler(plpRepository)
    val query : LiveData<String> = _query

    val results: LiveData<Resource<List<Product>>> = _query.switchMap { search ->
        if (search.isBlank()) {
            AbsentLiveData.create()
        } else {
            plpRepository.search(search)
        }
    }

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase(Locale.getDefault()).trim()
        if (input == _query.value) {
            //return
        }
        nextPageHandler.reset()
        _query.value = input
    }

    fun loadNextPage() {
        _query.value?.let {
            if (it.isNotBlank()) {
                nextPageHandler.queryNextPage(it)
            }
        }
    }

    fun refresh() {
        _query.value?.let {
            _query.value = it
        }
    }

    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
        private var handledError = false

        val errorMessageIfNotHandled: String?
            get() {
                if (handledError) {
                    return null
                }
                handledError = true
                return errorMessage
            }
    }

    class NextPageHandler(private val repository: PlpRepository) : Observer<Resource<Any>> {
        private var nextPageLiveData: LiveData<Resource<Boolean>>? = null
        val loadMoreState = MutableLiveData<LoadMoreState>()
        private var query: String? = null
        private var _hasMore: Boolean = false
        val hasMore
            get() = _hasMore

        init {
            reset()
        }

        fun queryNextPage(query: String) {
            if (this.query == query) {
                //return
            }
            unregister()
            this.query = query
            nextPageLiveData = repository.searchNextPage(query)
            loadMoreState.value = LoadMoreState(
                    isRunning = true,
                    errorMessage = null
            )
            nextPageLiveData?.observeForever(this)
        }

        override fun onChanged(result: Resource<Any>?) {
            if (result == null) {
                reset()
            } else {
                when (result.status) {
                    Status.SUCCESS -> {
                        _hasMore = result.data == true
                        unregister()
                        loadMoreState.setValue(
                                LoadMoreState(
                                        isRunning = false,
                                        errorMessage = null
                                )
                        )
                    }
                    Status.ERROR -> {
                        _hasMore = true
                        unregister()
                        loadMoreState.setValue(
                                LoadMoreState(
                                        isRunning = false,
                                        errorMessage = result.message
                                )
                        )
                    }
                    Status.LOADING -> {
                        // ignore
                    }
                }
            }
        }

        private fun unregister() {
            nextPageLiveData?.removeObserver(this)
            nextPageLiveData = null
            if (_hasMore) {
                query = null
            }
        }

        fun reset() {
            unregister()
            _hasMore = true
            loadMoreState.value = LoadMoreState(
                    isRunning = false,
                    errorMessage = null
            )
        }
    }

}