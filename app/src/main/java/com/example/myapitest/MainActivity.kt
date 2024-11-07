package com.example.myapitest

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapitest.database.DatabaseBuilder
import com.example.myapitest.database.adapter.CarAdapter
import com.example.myapitest.database.model.CarLocation
import com.example.myapitest.databinding.ActivityMainBinding
import com.example.myapitest.model.Car
import com.example.myapitest.services.Result
import com.example.myapitest.services.RetrofitClient
import com.example.myapitest.services.safeApiCall
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermitionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //requestLocationPermission()
        setupView()
        //FirebaseAuth.getInstance()

        // 2- Criar Opção de Logout no aplicativo

        // 3- Integrar API REST /car no aplicativo
        //      API será disponibilida no Github
        //      JSON Necessário para salvar e exibir no aplicativo
        //      O Image Url deve ser uma foto armazenada no Firebase Storage
        //      { "id": "001", "imageUrl":"https://image", "year":"2020/2020", "name":"Gaspar", "licence":"ABC-1234", "place": {"lat": 0, "long": 0} }

        // Opcionalmente trabalhar com o Google Maps ara enviar o place
    }

    override fun onResume() {
        super.onResume()
        fetchItems()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                onLogout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onLogout() {
        FirebaseAuth.getInstance().signOut()
        val intent = LoginActivity.newIntent(this)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            fetchItems()
        }
        binding.addCta.setOnClickListener{
            startActivity(NewCarActivity.newIntent(this))
        }
    }

    private fun requestLocationPermission() {
        //inicializar o nosso fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //configura a permissão do usuário
        locationPermitionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
                if(isGranted){
                    getLastLocation()
                }else{
                    Toast.makeText(this, R.string.denied_permission, Toast.LENGTH_LONG).show()
                }
            }
        checkLocationPermissionAndRequest()
    }

    private fun checkLocationPermissionAndRequest() {
        when {
            ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED -> {
                getLastLocation()
            }

            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                locationPermitionLauncher.launch(ACCESS_FINE_LOCATION)
            }
            shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION) -> {
                locationPermitionLauncher.launch(ACCESS_COARSE_LOCATION)
            }

            else -> {
                locationPermitionLauncher.launch((ACCESS_FINE_LOCATION))
            }
        }
    }

    private fun getLastLocation() {
        if(ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            requestLocationPermission()
            return

        }
        fusedLocationClient.lastLocation.addOnCompleteListener{ task->
            if(task.isSuccessful && task.result != null){
                val location = task.result
                val userLocation = CarLocation(latitude = location.latitude, longitude = location.longitude)
                CoroutineScope(Dispatchers.IO).launch {
                    DatabaseBuilder.getInstance()
                        .userLocationDoa()
                        .insert(userLocation)
                }
                Log.d("Hello", "Lat: ${userLocation.latitude} Long: ${userLocation.longitude}" )
                //Toast.makeText(this, "Location ${location.latitude} Long: ${location.longitude}", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, R.string.unknow_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val resultado = safeApiCall { RetrofitClient.apiService.getItems() }
            withContext(Dispatchers.Main){
                binding.swipeRefreshLayout.isRefreshing = false
                when(resultado){
                    is Result.Error -> {}
                    is Result.Success -> {

                        handleOnSuccess(resultado.data)
                    }
                }
            }
        }
    }

    private fun handleOnSuccess(data: List<Car>) {
        val adapter = CarAdapter(data){
            startActivity(CarDetailActivity.newIntent(
                this,
                it.id
            ))
        }
        binding.recyclerView.adapter = adapter
    }

    companion object{
        fun newIntent(context: Context) =
            Intent(context, MainActivity::class.java)
    }


}
