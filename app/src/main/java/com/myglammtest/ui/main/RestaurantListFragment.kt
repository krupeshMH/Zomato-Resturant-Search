package com.myglammtest.ui.main


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.myglammtest.R
import com.myglammtest.ui.Resource

import com.myglammtest.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import android.content.Intent
import android.location.LocationManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myglammtest.models.response.Restaurant
import com.myglammtest.ui.main.adapter.RestaurantListAdapter


class RestaurantListFragment : DaggerFragment(), SearchView.OnQueryTextListener,
    View.OnClickListener, RestaurantListAdapter.Interaction {

    private var viewModel: RestaurantSearchViewModel? = null
    lateinit var editsearch: SearchView
    lateinit var txtInfo: TextView
    lateinit var btnPermission: Button
    lateinit var recycler_view: RecyclerView
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private val PERMISSION_LOCATION_REQUEST_CODE = 1
    lateinit var mainRecyclerAdapter: RestaurantListAdapter


    @Inject
    @JvmField
    internal var providerFactory: ViewModelProviderFactory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, providerFactory)
            .get(RestaurantSearchViewModel::class.java!!)

        val lm: LocationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex1: Exception) {
        }

        if (gpsEnabled)
            checkLocationPermission()
        else {
            val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
            activity?.finish()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_resturant_list, container, false)
        editsearch = view.findViewById(R.id.searchView) as SearchView
        editsearch.setOnQueryTextListener(this)
        txtInfo = view.findViewById(R.id.txt_info) as TextView
        btnPermission = view.findViewById(R.id.btn_permission) as Button
        btnPermission.setOnClickListener(this)
        recycler_view = view.findViewById(R.id.recyclerView_main) as RecyclerView

        editsearch.setIconifiedByDefault(true)
        editsearch.setFocusable(true)
        editsearch.setIconified(false)
        editsearch.requestFocusFromTouch()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        initRecyclerView()
    }

    private fun checkLocationPermission() {
        val hasLocationPermission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasLocationPermission) {
            startLocationUpdate()
        } else {
            if (context?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED) {
                requestPermissions( //Method of Fragment
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    PERMISSION_LOCATION_REQUEST_CODE
                )
            } else {

            }

        }
    }

    private fun initRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            mainRecyclerAdapter = RestaurantListAdapter(this@RestaurantListFragment)
            adapter = mainRecyclerAdapter
        }
    }


    private fun subscribeObservers() {

        viewModel!!.listRestaurant.observe(viewLifecycleOwner, Observer { response ->
            when (response.status) {
                Resource.Status.LOADING -> {
                    // show progress
                    txtInfo.text = getString(R.string.loading_text)
                }
                Resource.Status.SUCCESS -> {
                    if (response.data?.resultsFound != null) {
                        val numberRest = response.data?.resultsFound
                        if (numberRest > 0)
                            txtInfo.text = getString(R.string.text_resturants_found)
                        else
                            txtInfo.text = getString(R.string.text_resturants_absent)

                        mainRecyclerAdapter.submitList(response.data.restaurants!!)
                    }
                }
                Resource.Status.ERROR -> {
                    Log.e("ERROR", "onChanged: ERROR..." + response.message)
                    txtInfo.text = getString(R.string.generic_error_msg)
                }
            }
        })
    }


    override fun onQueryTextSubmit(query: String): Boolean {
        if (!TextUtils.isEmpty(query)) {
            viewModel?.callApi(query, latitude, longitude)
        }
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        return false
    }


    private fun startLocationUpdate() {
        viewModel!!.getLocationData().observe(this, Observer { locationModel ->
            println("lat ${locationModel.latitude} long ${locationModel.longitude}")
            latitude = locationModel.latitude
            longitude = locationModel.longitude
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_LOCATION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startLocationUpdate()
                } else {
                    // permission denied
                    btnPermission.visibility = View.VISIBLE
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_permission -> {
                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)
                activity?.finish()
            }
        }
    }

    override fun onItemSelected(position: Int, item: Restaurant) {
    }
}

