package com.example.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.R
import com.example.weatherapp.data.ForecastResponse
import com.example.weatherapp.data.response.WeatherResponse
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.utils.HelperFunction.formatterDegree
import com.example.weatherapp.utils.LOCATION_PERMISSION_REQUEST_CODE
import com.example.weatherapp.utils.iconSizeWeather4x
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var _viewModel: MainViewModel? = null
    private val viewModel get() = _viewModel as MainViewModel

    private var _weatherAdapter: WeatherAdapter? = null
    private val weatherAdapter get() = _weatherAdapter as WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Membuat Full Fullscreen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetsController?.isAppearanceLightStatusBars = true

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _weatherAdapter = WeatherAdapter()

        _viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.getForecastByCity().observe(this) {}

        searchCity()
        viewModel.getWeatherByCity().observe(this) {
            binding.tvCity.text = it.name
            binding.tvDegree.text = formatterDegree(it.main?.temp)

            val icon = it.weather?.get(0)?.icon
            val iconUrl = BuildConfig.ICON_URL + icon + iconSizeWeather4x
            Glide.with(this).load(iconUrl)
                .into(binding.imgIcWeather)
        }

        val weatherAdapter = WeatherAdapter()
        viewModel.getForecastByCity().observe(this) {
            weatherAdapter.setData(it.list)
        }

        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = weatherAdapter
        }

        getWeatherCurrentLocation()
    }

    private fun getWeatherCurrentLocation() {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                try {
                    val lat = it.latitude
                    val lon = it.longitude

                    viewModel.forecastByCurrentLocation(lat, lon)
                    viewModel.weatherByCurrentLocation(lat, lon)
                } catch (e: Throwable) {
                    Log.i("MainActivity", "last location: $it")
                    Log.e("MainActivity", "Couldn't get latitude longitude")
                }

            }
            .addOnFailureListener {
                Log.e("MainActivity", "FusedLocationError: Failed getting current location")
            }
        viewModel.weatherByCurrentLocation(1.9, 9.9)
        viewModel.getWeatherByCurrentLocation().observe(this) {
            setupView(it, null)
        }
        viewModel.getForecastByCurrentLocation().observe(this) {
            weatherAdapter.setData(it.list)
            binding.rvForecast.apply {
                layoutManager =
                    LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = weatherAdapter
            }
        }
    }

    private fun setupView(weather: WeatherResponse?, forecast: ForecastResponse?) {
        weather?.let {
            binding.apply {
                binding.tvCity.text = it.name
                binding.tvDegree.text = formatterDegree(it.main?.temp)

                val icon = it.weather?.get(0)?.icon
                val iconUrl = BuildConfig.ICON_URL + icon + iconSizeWeather4x
                Glide.with(applicationContext).load(iconUrl)
                    .into(binding.imgIcWeather)

                setupBackgroundWeather(weather.weather?.get(0)?.id, icon)
            }
            binding.rvForecast.apply {
                layoutManager =
                    LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = weatherAdapter
            }
        }
    }

    private fun setupBackgroundWeather(idWeather: Int?, icon: String?) {
        idWeather?.let {
            when (idWeather) {
                in resources.getIntArray(R.array.thunderstorm_id_list) -> setImageBackground(R.drawable.thunderstorm)
                in resources.getIntArray(R.array.drizzle_id_list) -> setImageBackground(R.drawable.drizzle)
                in resources.getIntArray(R.array.rain_id_list) -> setImageBackground(R.drawable.rain)
                in resources.getIntArray(R.array.freezing_rain_id_list) -> setImageBackground(R.drawable.freezing_rain)
                in resources.getIntArray(R.array.snow_id_list) -> setImageBackground(R.drawable.snow)
                in resources.getIntArray(R.array.sleet_id_list) -> setImageBackground(R.drawable.sleet)
                in resources.getIntArray(R.array.clear_id_list) -> when (icon) {
                    "01d" -> setImageBackground(R.drawable.clear)
                    "01n" -> setImageBackground(R.drawable.clear_night)
                }
                in resources.getIntArray(R.array.clouds_id_list) -> setImageBackground(R.drawable.lightcloud)
                in resources.getIntArray(R.array.heavy_clouds_id_list) -> setImageBackground(R.drawable.heavycloud)
                in resources.getIntArray(R.array.fog_id_list) -> setImageBackground(R.drawable.fog)
                in resources.getIntArray(R.array.sand_id_list) -> setImageBackground(R.drawable.sand)
                in resources.getIntArray(R.array.dust_id_list) -> setImageBackground(R.drawable.dust)
                in resources.getIntArray(R.array.volcanic_ash_id_list) -> setImageBackground(R.drawable.volcanic)
                in resources.getIntArray(R.array.squalls_id_list) -> setImageBackground(R.drawable.squalls)
                in resources.getIntArray(R.array.tornado_id_list) -> setImageBackground(R.drawable.tornado)
            }
        }
    }

    private fun setImageBackground(image: Int) {
        Glide.with(applicationContext).load(image).into(binding.imgBgWeather)
    }

    private fun searchCity() {
        binding.edtSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        viewModel.weatherByCity(it)
                        viewModel.forecastByCty(it)
                    }
                    try {
                        val inputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
                    } catch (e: Throwable) {
                        Log.e("MainActivity", "hideSoftWindow: $e")
                    }
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return false
                }
            }
        )
    }
}