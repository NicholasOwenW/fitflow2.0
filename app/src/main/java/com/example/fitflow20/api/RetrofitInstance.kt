import com.example.fitflow20.api.WorkoutListAPI
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://exercises-by-api-ninjas.p.rapidapi.com/"

    val api: WorkoutListAPI by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-RapidAPI-Key", "e58f7be579msh761583fe2fe4a3ap13fc1bjsn20885e1270a8")
                    .addHeader("X-RapidAPI-Host", "exercises-by-api-ninjas.p.rapidapi.com")
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(WorkoutListAPI::class.java)
    }
}
