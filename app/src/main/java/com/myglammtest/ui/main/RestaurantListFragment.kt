package com.myglammtest.ui.main


import android.Manifest
import android.content.*
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
import android.location.LocationManager
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myglammtest.BuildConfig
import com.myglammtest.models.response.Restaurant
import com.myglammtest.ui.main.adapter.RestaurantListAdapter


class RestaurantListFragment : DaggerFragment(), SearchView.OnQueryTextListener,
    RestaurantListAdapter.Interaction {

    private var viewModel: RestaurantSearchViewModel? = null
    lateinit var editsearch: SearchView
    lateinit var txtInfo: TextView
    lateinit var btnPermission: Button
    private lateinit var recyclerView: RecyclerView
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
            .get(RestaurantSearchViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        checkGPSEnabled()
    }

    private fun checkGPSEnabled() {
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
            showGPSDialog()
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
        recyclerView = view.findViewById(R.id.recyclerView_main) as RecyclerView

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
            }
        }
    }


    private fun initRecyclerView() {
        recyclerView.apply {
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
                        val numberRest = response.data.resultsFound
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
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        // user rejected the permission
                        val showRationale =
                            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                        if (!showRationale) {
                            // user also CHECKED "never ask again"
                            Toast.makeText(context, "Grant location permission", Toast.LENGTH_LONG)
                                .show()
                            val intent = Intent(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                            )
                            startActivity(intent)
                        } else {
                            // user did NOT check "never ask again"
                            activity?.finish()
                        }
                    }

                    // permission denied
                    activity?.finish()
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }


    override fun onItemSelected(position: Int, item: Restaurant) {
    }

    private fun showGPSDialog() {
        val dialogBuilder = AlertDialog.Builder(context!!)

        // set message of alert dialog
        dialogBuilder.setMessage("GPS needed to retrieve nearby location. Click Proceed")
            // if the dialog is cancelable
            .setCancelable(false)
            .setPositiveButton("Proceed") { dialog, id ->
                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)
                //activity?.finish()
            }
            // negative button text and action
            .setNegativeButton("Cancel") { dialog, id ->
                activity?.finish()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("GPS Required")
        // show alert dialog
        alert.show()
    }


}
