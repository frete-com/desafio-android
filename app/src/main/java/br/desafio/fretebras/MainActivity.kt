package br.desafio.fretebras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private val url = "https://6628184554afcabd0734dd14.mockapi.io/fretebras/"
    private val gson: Gson by lazy { GsonBuilder().create() }
    private val okHttp: OkHttpClient by lazy { OkHttpClient.Builder().build() }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val service: FretebrasService by lazy {
        retrofit.create(FretebrasService::class.java)
    }

    private val freightList = MutableStateFlow<List<Freight>>(emptyList())
    private val isLoading = MutableStateFlow(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val freightList by freightList.collectAsState()
            val isLoading by isLoading.collectAsState()

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        modifier = Modifier.padding(bottom = 12.dp),
                        text = "Empresas",
                        fontSize = 24.sp,
                    )
                    Content(freightList)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getList()
    }

    private fun getList() {
        isLoading.value = true
        lifecycleScope.launch {
            delay(3000)
            val response = service.getUsers()
            isLoading.value = false
            freightList.value = response.body()!!
        }
    }
}

@Composable
fun Content(freights: List<Freight>) {
    Column {
        freights.forEach {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(
                    text = "Empresa: " + it.name,
                    fontSize = 18.sp
                )
                Text(
                    text = "Endereço: " + it.address.orEmpty(),
                    fontSize = 18.sp
                )
            }
            Divider()
        }
    }
}