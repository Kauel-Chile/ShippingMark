package com.kauel.shippingmark.di

import android.app.Application
import android.content.Context
import android.util.Log
import android.webkit.CookieManager
import androidx.room.Room
import androidx.viewbinding.BuildConfig
import com.kauel.shippingmark.api.ApiService
import com.kauel.shippingmark.data.AppDatabase
import com.kauel.shippingmark.utils.URL_BASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit =
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client().build())
            .build()

    private fun client(): OkHttpClient.Builder {
        var cookieManager: CookieManager
        return OkHttpClient.Builder().apply {
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            connectTimeout(30, TimeUnit.SECONDS)
            cookieJar(object : CookieJar {

                /**
                 * @param url
                 * @param cookies list of cookies get in api response
                 */
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {

                    cookieManager = CookieManager.getInstance()
                    for (cookie in cookies) {
                        cookieManager.setCookie(url.toString(), cookie.toString())
                        Log.e(
                            "ok",
                            "saveFromResponse :  Cookie url : $cookie"
                        )
                    }
                }

                /**
                 * @param url
                 *
                 * adding cookies with request
                 */
                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    cookieManager = CookieManager.getInstance()

                    val cookies: ArrayList<Cookie> = ArrayList()
                    if (cookieManager.getCookie(url.toString()) != null) {
                        val splitCookies =
                            cookieManager.getCookie(url.toString()).split("[,;]".toRegex())
                                .dropLastWhile { it.isEmpty() }.toTypedArray()
                        for (i in splitCookies.indices) {
                            cookies.add(Cookie.parse(url, splitCookies[i].trim { it <= ' ' })!!)
                            Log.e(
                                "ok",
                                "loadForRequest :Cookie.add ::  " + Cookie.parse(
                                    url,
                                    splitCookies[i].trim { it <= ' ' })!!
                            )
                        }
                    }
                    return cookies
                }
            })
        }
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideDataBase(app: Application) : AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "app_data_shippingMark")
            .fallbackToDestructiveMigration()
            .build()

//    @Provides
//    fun provideServerDao(db: AppDatabase) = db.sendDataDao()

}