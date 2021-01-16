package com.android.example.liverpool.api

import com.android.example.liverpool.vo.Product
import com.google.gson.annotations.SerializedName

/**
 * Simple object to hold repo search responses. This is different from the Entity in the database
 * because we are keeping a search result in 1 row and denormalizing list of results into a single
 * column.
 */

data class PlpSearchProductResponse( val status: Status,
            val pageType: String,
            val plpResults: PlpResults
            ) {
    var nextPage: Int? = null
}

data class PlpResults (
        val label: String,
        val plpState: PlpState,
        val sortOptions: List<Any?>,
        val refinementGroups: List<Any?>,
        val records: List<Product>,
        val navigation: Navigation
)

data class Navigation (
        val ancester: List<Any?>,
        val current: List<Current>,
        val childs: List<Any?>
)

data class Current (
        val label: String,
        @SerializedName("categoryId")
        val categoryID: String
)

data class PlpState (
        @SerializedName("categoryId")
        val categoryID: String,

        val currentSortOption: String,
        val currentFilters: String,
        val firstRecNum: Int,
        val lastRecNum: Int,
        val recsPerPage: Int,
        val totalNumRecs: Int,
        val originalSearchTerm: String
)

data class Status (
        val status: String,
        val statusCode: Long
)