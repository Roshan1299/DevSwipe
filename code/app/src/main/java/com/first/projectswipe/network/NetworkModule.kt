//package com.first.projectswipe.network
//
//import android.content.Context
//import com.first.projectswipe.presentation.ui.auth.AuthManager
//import okhttp3.Interceptor
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.util.concurrent.TimeUnit
//
//object NetworkModule {
//
//    // Replace with your backend URL
//    private const val BASE_URL = "http://10.0.2.2:8080/" // For Android Emulator
//    //or
////    private const val BASE_URL = "http://127.0.0.1:8080/" // For real device
//    // For real device use: "http://YOUR_LOCAL_IP:8080/"
//    // For production use: "https://your-backend-domain.com/"
//
//    fun provideApiService(context: Context): ApiService {
//        return provideRetrofit(context).create(ApiService::class.java)
//    }
//
//    private fun provideRetrofit(context: Context): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(provideOkHttpClient(context))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    private fun provideOkHttpClient(context: Context): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(provideAuthInterceptor(context))
//            .addInterceptor(provideLoggingInterceptor())
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(30, TimeUnit.SECONDS)
//            .build()
//    }
//
//    private fun provideAuthInterceptor(context: Context): Interceptor {
//        return Interceptor { chain ->
//            val authManager = AuthManager.getInstance(context)
//            val token = authManager.getToken()
//
//            val requestBuilder = chain.request().newBuilder()
//
//            // Add Authorization header if token exists
//            token?.let {
//                requestBuilder.addHeader("Authorization", "Bearer $it")
//            }
//
//            // Add Content-Type header for all requests
//            requestBuilder.addHeader("Content-Type", "application/json")
//
//            chain.proceed(requestBuilder.build())
//        }
//    }
//
//    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
//        return HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//    }
//}

// network/NetworkModule.kt
// network/NetworkModule.kt
package com.first.projectswipe.network

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.inject.Inject

/**
 * Token provider interface to break the circular dependency
 */
interface TokenProvider {
    fun getToken(): String?
}

/**
 * Implementation of TokenProvider that reads from SharedPreferences
 */
class SharedPreferencesTokenProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenProvider {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:8080/" // Emulator
    // private const val BASE_URL = "http://127.0.0.1:8080/" // For real device
    // For real device use: "http://YOUR_LOCAL_IP:8080/"
    // For production use: "https://your-backend-domain.com/"

    @Provides
    @Singleton
    fun provideTokenProvider(@ApplicationContext context: Context): TokenProvider {
        return SharedPreferencesTokenProvider(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenProvider: TokenProvider): Interceptor {
        return Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            // Add token if available
            val token = tokenProvider.getToken()
            token?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }

            // Always set Content-Type
            requestBuilder.addHeader("Content-Type", "application/json")

            chain.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}