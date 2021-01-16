package com.android.example.liverpool.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.android.example.liverpool.AppExecutors
import com.android.example.liverpool.R
import com.android.example.liverpool.databinding.HistoryItemBinding
import com.android.example.liverpool.databinding.ProductItemBinding
import com.android.example.liverpool.vo.PlpSearchProductResults
import com.android.example.liverpool.vo.Product
import timber.log.Timber

/**
 * A RecyclerView adapter for [Product] class.
 */
class HistoryListAdapter (
        private val dataBindingComponent: DataBindingComponent,
        appExecutors: AppExecutors,
        private val repoClickCallback: ((PlpSearchProductResults) -> Unit)?,
        private val deleteListener: TaskListener?
) : DataBoundListAdapter<PlpSearchProductResults, HistoryItemBinding>(
        appExecutors = appExecutors,
        diffCallback = object : DiffUtil.ItemCallback<PlpSearchProductResults>() {
            override fun areItemsTheSame(oldItem: PlpSearchProductResults, newItem: PlpSearchProductResults): Boolean {
                return oldItem.query == newItem.query
            }

            override fun areContentsTheSame(oldItem: PlpSearchProductResults, newItem: PlpSearchProductResults): Boolean {
                return oldItem.query == newItem.query
            }
        }
) {

    override fun createBinding(parent: ViewGroup): HistoryItemBinding {
        val binding = DataBindingUtil.inflate<HistoryItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.history_item,
                parent,
                false,
                dataBindingComponent
        )

        binding.root.setOnClickListener {
            binding.name?.let {
               // repoClickCallback?.invoke(it)
            }
        }
        binding.deleteButton.setOnClickListener{
            binding.plpSearchResult?.let {
                Timber.d("delete ${it.query}")
                deleteListener?.onTaskClick(it.query)
            }

        }
        return binding
    }

    override fun bind(binding: HistoryItemBinding, item: PlpSearchProductResults) {
        binding.plpSearchResult = item
    }
}

interface TaskListener {
    fun onTaskClick(task: String)
}