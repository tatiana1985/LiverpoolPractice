package com.android.example.liverpool.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.android.example.liverpool.repository.PlpRepository
import com.android.example.liverpool.vo.PlpSearchProductResults
import javax.inject.Inject

class HistoryViewModel @Inject constructor(plpRepository: PlpRepository) : ViewModel(){

    val listHistory: LiveData<List<PlpSearchProductResults>> = plpRepository.getAllSugges()
    val deleteItems = DeleteItems(plpRepository)

    class DeleteItems(private val repository: PlpRepository) {
        fun deleteItem(value: String) {
            repository.deleteItem(value)
        }
        fun deleteAllItems() {
            repository.deleteAllItems()
        }
    }

}