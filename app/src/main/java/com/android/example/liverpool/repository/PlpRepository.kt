package com.android.example.liverpool.repository

import androidx.lifecycle.LiveData
import com.android.example.liverpool.AppExecutors
import com.android.example.liverpool.api.ApiSuccessResponse
import com.android.example.liverpool.api.LiverpoolService
import com.android.example.liverpool.api.PlpSearchProductResponse
import com.android.example.liverpool.db.LiverpoolDb
import com.android.example.liverpool.db.ProductDao
import com.android.example.liverpool.testing.OpenForTesting
import com.android.example.liverpool.util.RateLimiter
import com.android.example.liverpool.vo.PlpSearchProductResults
import com.android.example.liverpool.vo.Product
import com.android.example.liverpool.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles Repo instances.
 *
 */
@Singleton
@OpenForTesting
class PlpRepository @Inject constructor(
        private val appExecutors: AppExecutors,
        private val db: LiverpoolDb,
        private val productDao: ProductDao,
        private val liverpoolService: LiverpoolService
) {

    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun getAllSugges() : LiveData<List<PlpSearchProductResults>> {
        val allSuggest =  db.productDao().getAllResults()
        return allSuggest
    }

    fun deleteItem(value: String) {
        db.productDao().deleteItemResult(value)
    }

    fun deleteAllItems() {
        db.productDao().deleteAllItems()
    }


    fun searchNextPage(search: String): LiveData<Resource<Boolean>> {
        val fetchNextSearchPageTask = FetchNextSearchPageTask(
                force = "true",
                search = search,
                itemsPerPage = 10,
                liverpoolService = liverpoolService,
                db = db
        )
        appExecutors.networkIO().execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }

    fun search(search: String): LiveData<Resource<List<Product>>> {

        return object : NetworkBoundResource<List<Product>, PlpSearchProductResponse>(appExecutors) {

            override fun saveCallResult(item: PlpSearchProductResponse) {
                val repoIds = item.plpResults.records.map { it.productID }
                val plpSearchResult = PlpSearchProductResults(
                        query = search,
                        totalCount = item.plpResults.plpState.totalNumRecs,
                        next = item.plpResults.plpState.firstRecNum
                )
                db.runInTransaction {
                    productDao.insertProducts(item.plpResults.records)
                    productDao.insert(plpSearchResult)
                }
            }

            override fun shouldFetch(data: List<Product>?) = true

            override fun loadFromDb(): LiveData<List<Product>> {
                return productDao.load()
            }

            //override fun cleanFromDb() {
              //  runBlocking(Dispatchers.Default) {
              //      productDao.deleteAll()
              //  }
            //}


            override fun createCall() = liverpoolService.searchPlp("true",search,1,10 )

            override fun processResponse(response: ApiSuccessResponse<PlpSearchProductResponse>)
                    : PlpSearchProductResponse {
                val body = response.body
                body.nextPage = response.nextPage
                return body
            }
        }.asLiveData()
    }
}