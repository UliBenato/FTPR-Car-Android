package com.example.carApi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carApi.databinding.ActivityCarDetailBinding
import com.example.carApi.model.CarValue
import com.example.carApi.services.Result
import com.example.carApi.services.RetrofitClient
import com.example.carApi.services.safeApiCall
import com.example.carApi.ui.loadUrl
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CarDetailActivity : AppCompatActivity() , OnMapReadyCallback {

    private lateinit var binding: ActivityCarDetailBinding
    private lateinit var item: CarValue
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        loadItem()
        setupGoogleMap()
    }

    override fun onMapReady(googleMap: GoogleMap){
        mMap = googleMap
        binding.googleMapContent.visibility = View.VISIBLE
        if(::item.isInitialized){
            loadItemLocationInGoogleMap()
        }
    }


    private fun loadItem() {
        val itemId = intent.getStringExtra(ARG_ID) ?: ""
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.getItem(itemId) }

            withContext((Dispatchers.Main)) {
                when (result) {
                    is Result.Error -> {}
                    is Result.Success -> {
                        item = result.data
                        handleSuccess()
                    }
                }
            }
        }
    }

    private fun handleSuccess() {
        binding.name.text = item.value.name
        binding.year.text = item.value.year
        binding.license.setText(item.value.licence)
        binding.image.loadUrl(item.value.imageUrl)
        loadItemLocationInGoogleMap()
    }

    private fun loadItemLocationInGoogleMap() {
        item.value.place?.let {
            binding.googleMapContent.visibility = View.VISIBLE
            val latLng = LatLng(it.lat, it.long)
            mMap.addMarker(
                MarkerOptions().position(latLng)
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.deleteCTA.setOnClickListener {
            deleteItem()
        }
        binding.editCTA.setOnClickListener {
            editItem()
        }
    }

    companion object {
        private const val ARG_ID = "ARG_ID"
        fun newIntent(
            context: Context,
            itemId: String
        ) =
            Intent(context, CarDetailActivity::class.java).apply {
                putExtra(ARG_ID, itemId)
            }
    }

    private fun deleteItem() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall {
                RetrofitClient.apiService.deleteItem(item.id)
            }

            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(
                            this@CarDetailActivity,
                            R.string.error_delete,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    is Result.Success -> {
                        Toast.makeText(
                            this@CarDetailActivity,
                            R.string.success_delete,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        finish()
                    }
                }
            }
        }
    }


    private fun setupGoogleMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun editItem() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall {
                RetrofitClient.apiService.updateItem(
                    item.id,
                    item.value.copy(licence = binding.license.text.toString())
                )
            }

            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(
                            this@CarDetailActivity,
                            R.string.error_editar,
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                    is Result.Success -> {
                        Toast.makeText(
                            this@CarDetailActivity,
                            R.string.success_save,
                            Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
                }
            }
        }
    }

