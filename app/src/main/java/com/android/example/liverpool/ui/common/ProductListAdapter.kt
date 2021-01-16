package com.android.example.liverpool.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.android.example.liverpool.AppExecutors
import com.android.example.liverpool.R
import com.android.example.liverpool.databinding.ProductItemBinding
import com.android.example.liverpool.vo.Product

/**
 * A RecyclerView adapter for [Product] class.
 */
class ProductListAdapter (
        private val dataBindingComponent: DataBindingComponent,
        appExecutors: AppExecutors,
        private val showFullName: Boolean,
        private val repoClickCallback: ((Product) -> Unit)?
) : DataBoundListAdapter<Product, ProductItemBinding>(
        appExecutors = appExecutors,
        diffCallback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.productID == newItem.productID
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.productID == newItem.productID
            }
        }
) {

    override fun createBinding(parent: ViewGroup): ProductItemBinding {
        val binding = DataBindingUtil.inflate<ProductItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.product_item,
                parent,
                false,
                dataBindingComponent
        )

        binding.root.setOnClickListener {
            binding.plpRecod?.let {
                repoClickCallback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: ProductItemBinding, item: Product) {
        binding.plpRecod = item
    }
}