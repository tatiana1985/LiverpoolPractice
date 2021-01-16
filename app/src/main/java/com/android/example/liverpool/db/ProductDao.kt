package com.android.example.liverpool.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.example.liverpool.testing.OpenForTesting
import com.android.example.liverpool.vo.PlpSearchProductResults
import com.android.example.liverpool.vo.Product

/**
 * Interface for database access on Repo related operations.
 */
@Dao
@OpenForTesting
abstract class ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg repos: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertProducts(repositories: List<Product>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun createProductIfNotExists(repo: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(result: PlpSearchProductResults)

    @Query("SELECT * FROM PlpSearchProductResults WHERE `query` = :query")
    abstract fun search(query: String): LiveData<PlpSearchProductResults?>

    @Query("SELECT * FROM Product WHERE productID in (:productIDs)")
    abstract fun loadById(productIDs: List<Int>): LiveData<List<Product>>

    @Query("SELECT * FROM Product")
    abstract fun load(): LiveData<List<Product>>

    @Query("SELECT * FROM PlpSearchProductResults WHERE `query` = :query")
    abstract fun findSearchResult(query: String): PlpSearchProductResults?

    @Query("DELETE FROM Product")
    abstract fun deleteAll()

    @Query("SELECT * FROM PlpSearchProductResults")
    abstract fun getAllResults(): LiveData<List<PlpSearchProductResults>>

    @Query("DELETE FROM PlpSearchProductResults WHERE `query` = :query ")
    abstract fun deleteItemResult(query: String)

    @Query("DELETE FROM PlpSearchProductResults ")
    abstract fun deleteAllItems()

}