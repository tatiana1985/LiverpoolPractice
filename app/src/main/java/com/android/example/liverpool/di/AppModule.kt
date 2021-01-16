/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.liverpool.di

import android.app.Application
import androidx.room.Room
import com.android.example.liverpool.api.LiverpoolService
import com.android.example.liverpool.db.LiverpoolDb
import com.android.example.liverpool.db.ProductDao
import com.android.example.liverpool.util.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module(includes = [ViewModelModule::class])
class AppModule {
    private lateinit var httpClient: OkHttpClient



    @Singleton
    @Provides
    fun provideLiverpoolService(): LiverpoolService {
        var builder: Retrofit.Builder
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient = OkHttpClient().newBuilder()
                .addInterceptor(interceptor).build()

        return  Retrofit.Builder()
                .baseUrl("https://shoppapp.liverpool.com.mx/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(LiverpoolService::class.java)

    }

    @Singleton
    @Provides
    fun provideDb(app: Application): LiverpoolDb {
        return Room
                .databaseBuilder(app, LiverpoolDb::class.java, "liverpool.db")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: LiverpoolDb): ProductDao {
        return db.productDao()
    }
}
