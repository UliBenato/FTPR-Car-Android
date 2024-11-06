package com.example.myapitest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapitest.databinding.ActivityCarDetailBinding
import com.example.myapitest.databinding.ActivityLoginBinding
import com.example.myapitest.model.Car
import com.example.myapitest.services.Result
import com.example.myapitest.services.RetrofitClient
import com.example.myapitest.services.safeApiCall
import com.example.myapitest.ui.loadUrl
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarDetailActivity : AppCompatActivity() {

    private lateinit var car: Car
    private lateinit var mMap: GoogleMap

    private lateinit var binding: ActivityCarDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        loadItem()
        //setupGoogleMap()

    }

    private fun loadItem() {
        val itemId = intent.getStringExtra(ARG_ID) ?: ""
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.getItem(itemId) }

            withContext((Dispatchers.Main)){
                when (result){
                    is Result.Error -> {}
                    is Result.Success -> {
                        car = result.data
                        handleSuccess()
                    }
                }
            }
        }
    }
    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.deleteCTA.setOnClickListener {
            //deleteItem()
        }
        binding.editCTA.setOnClickListener {
            //editItem()
        }
    }
    private fun handleSuccess() {
        binding.model.text = "${car.value.name}"
        binding.year.text = getString(R.string.year, car.value.year.toString())
        binding.licence.setText(car.value.licence)
        binding.image.loadUrl(car.value.imageUrl)
        //loadItemLocationInGoogleMap()
    }

    companion object{
        private  const val ARG_ID = "ARG_ID"
        fun newIntent(
            context: Context,
            itemId: String
        ) =
            Intent(context, CarDetailActivity::class.java).apply {
                putExtra(ARG_ID, itemId)
            }
    }
}