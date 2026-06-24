package io.funstop.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.funstop.dao.EventDao
import io.funstop.dao.ProductDao
import io.funstop.database.AppDatabase
import io.funstop.network.ProductApi
import io.funstop.repository.WebRepository
import io.funstop.work_manager.EventUploadWorker
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ProductDiModule {

    @Provides
    @Singleton
    fun providesWorkManager(
        @ApplicationContext context: Context
    )= WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun providesEventDao(appDatabase: AppDatabase): EventDao{
        return appDatabase.eventDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun providesProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    @Singleton
    fun providesRepository(productApi: ProductApi): WebRepository {
        return WebRepository(productApi)
    }

    @Provides
    @Singleton
    fun provideRetrofitInstance(): Retrofit{

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        return Retrofit.Builder()
            .baseUrl("https://dummyjson.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi {
        return retrofit.create(ProductApi::class.java)
    }

}