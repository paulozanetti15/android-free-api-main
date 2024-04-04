import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.app.freeapi.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {

    private lateinit var messageTextView: TextView
    private lateinit var fetchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageTextView = findViewById(R.id.messageTextView)
        fetchButton = findViewById(R.id.fetchButton)

        fetchButton.setOnClickListener {
            fetchDataFromApi()
        }
    }

    private fun fetchDataFromApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dogapi.dog/") // URL base correta
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = apiService.getMessage()
                if (response.isSuccessful) {
                    val message = response.body()?.data?.getOrNull(0)?.attributes?.body ?: "No message received"
                    messageTextView.text = message
                } else {
                    messageTextView.text = "Failed to fetch data"
                }
            } catch (e: Exception) {
                messageTextView.text = "Error: ${e.message}"
            }
        }
    }

    interface ApiService {
        @GET("api/v2/facts")
        suspend fun getMessage(): retrofit2.Response<ApiResponse>
    }

    data class ApiResponse(
        val data: List<DataItem>
    )

    data class DataItem(
        val id: String,
        val type: String,
        val attributes: Attributes
    )

    data class Attributes(
        val body: String
    )
}
