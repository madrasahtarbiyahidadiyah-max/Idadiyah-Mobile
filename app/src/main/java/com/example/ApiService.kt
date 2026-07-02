package com.example

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface ApiService {
    @GET
    suspend fun sinkronDatabase(
        @Url url: String,
        @Query("action") action: String = "sinkron"
    ): SyncResponse

    @GET
    suspend fun cariSantri(
        @Url url: String,
        @Query("id") id: String,
        @Query("action") action: String = "cariSantri"
    ): StudentResponse

    @GET
    suspend fun cariPembimbing(
        @Url url: String,
        @Query("nama") nama: String,
        @Query("action") action: String = "cariPembimbing"
    ): PembimbingResponse

    @GET
    suspend fun simpanInspeksi(
        @Url url: String,
        @Query("daerah") daerah: String,
        @Query("kelas") kelas: String,
        @Query("telatMenit") telatMenit: Int,
        @Query("guruAktif") guruAktif: Boolean,
        @Query("muridAktif") muridAktif: Boolean,
        @Query("kekondusifan") kekondusifan: String,
        @Query("kerapian") kerapian: String,
        @Query("catatan") catatan: String,
        @Query("action") action: String = "simpan"
    ): SimpanResponse

    companion object {
        private val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        fun create(): ApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://placeholder.com/") // Placeholder because we use dynamic @Url
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(ApiService::class.java)
        }
    }
}

// Retrofit response data classes
data class SyncResponse(
    val error: Boolean = false,
    val pesan: String = ""
)

data class StudentResponse(
    val error: Boolean = false,
    val pesan: String = "",
    val data: StudentData? = null
)

data class StudentData(
    val idpps: String,
    val nama: String,
    val domisili: String,
    val kelas: String,
    val pembimbing: String,
    val ruang_pagi: String = "-",
    val ruang_malam: String = "-"
)

data class PembimbingResponse(
    val error: Boolean = false,
    val pesan: String = "",
    val data: PembimbingData? = null
)

data class PembimbingData(
    val idpps: String = "",
    val nama: String,
    val guru: String = "",
    val alamat: String = "",
    val ruang_pagi: String = "-",
    val ruang_malam: String = "-"
)

data class SimpanResponse(
    val sukses: Boolean = false,
    val pesan: String = ""
)
