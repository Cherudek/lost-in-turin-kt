class MapFragment : Fragment(), SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener, OnMapReadyCallback {
    @BindView(id.map)
    internal var mapView: MapView? = null
    @BindView(id.checkout_button)
    internal var placePicker: FloatingActionButton? = null
    @BindView(id.map_progress_bar)
    internal var progressBar: ProgressBar? = null
    private var mMap: GoogleMap? = null
    private var onMarkerClickListener: OnMarkerClickListener? = null
    private var onInfoWindowClickListener: OnInfoWindowClickListener? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var mCurrentLocation: LatLng? = null
    private var apiKey: String? = null
    private var mQuery: String? = null
    private var permissions: Permissions? = null
    private var context: Context? = null
    private var listener: OnFragmentInteractionListener? = null
    private var queryViewModel: QueryNearbyPlacesViewModel? = null
    private var nearbyPlacesResponseParser: GoogleNearbyPlacesParser? = null
    private var mNearbyPlaces: NearbyPlaces? = null
    private var mSavedInstanceisNull: Boolean = false
    private var rootView: View? = null
    private var mMarkerOptionsRetrieved: List<MarkerOptions>? = null
    private var sharedModel: MapDetailSharedViewHolder? = null
    private var mPlaceIdTag: Int = 0
    private var searchView: SearchView? = null
    private val placeIdInt: Int = 0
    private var eventMarkerMap: TreeMap<Marker, Int>? = null
    private var activity: Activity? = null
    private var googleMapsApi: GoogleMapsApi? = null
    private var locationTask: Task<Location>? = null

    @Nullable
    fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup,
                     @Nullable savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(layout.fragment_map, container, false)
        ButterKnife.bind(this, rootView)
        apiKey = getString(string.google_maps_key)
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState!!.getBundle(MAPVIEW_BUNDLE_KEY)
            latitude = savedInstanceState!!.getDouble(CURRENT_LATITUDE_TAG)
            longitude = savedInstanceState!!.getDouble(CURRENT_LONGITUDE_TAG)
            mCurrentLocation = LatLng(latitude, longitude)
            mQuery = savedInstanceState!!.getString(CURRENT_QUERY_TAG)
            Log.i(LOG_TAG, "onCreateView savedInstanceState mapViewBundle:  " + mapViewBundle!!)
            Log.i(LOG_TAG, "onCreateView savedInstanceState latitude:  " + latitude!!)
            Log.i(LOG_TAG, "onCreateView savedInstanceState longitude:  " + longitude!!)
            Log.i(LOG_TAG, "onCreateView savedInstanceState Current String:  " + mQuery!!)
            Log.i(LOG_TAG, "onCreateView savedInstanceState Current Location:  " + mCurrentLocation!!)
            mSavedInstanceisNull = false
        } else {
            mSavedInstanceisNull = true
            longitude = 7.6862986
            latitude = 7.6862986
            mCurrentLocation = PIAZZA_CASTELLO
            mQuery = ""
        }
        eventMarkerMap = TreeMap<Marker, Int>()
        mapView!!.onCreate(mapViewBundle)
        // mapView.getMapAsync(this);
        // Instantiate the data parsing class
        nearbyPlacesResponseParser = GoogleNearbyPlacesParser()
        // Shared View Model to send Data from this fragment to the Detail one
        sharedModel = ViewModelProviders.of(getActivity()).get(MapDetailSharedViewHolder::class.java)
        return rootView
    }

    fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle) {
        super.onViewCreated(view, savedInstanceState)

        val connectivity = Connectivity()
        if (!connectivity.isOnline(context)) {
            val snackbar = Snackbar
                    .make(rootView, string.no_internet_connection,
                            Snackbar.LENGTH_LONG)
            snackbar.show()
        }

        placePicker!!.setOnClickListener(???({ this.onClick(it) }))

        onMarkerClickListener = OnMarkerClickListener { onMarkerClick(it) }

        onInfoWindowClickListener = OnInfoWindowClickListener { this.onInfoWindowClick(it) }
    }

    private fun onMarkerPressedIntent(marker: Marker) {
        if (listener != null) {
            listener!!.onFragmentInteraction(marker)
        }
    }

    private fun onPlacePickerPressedIntent(place: Place) {
        if (listener != null) {
            listener!!.OnPlacePickerInteraction(place)
        }
    }

    fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context

        } else {
            throw RuntimeException(context.toString() + getString(string.must_implement_on_frag_list))
        }
    }

    fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun onCreate(@Nullable savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        context = getContext()
        activity = getActivity()
        setHasOptionsMenu(true)
        // Check if the Google Play Services are available or not and set Up Map
        googleMapsApi = GoogleMapsApi()
        googleMapsApi!!.CheckGooglePlayServices(context, activity)
        googleMapsApi!!.GoogleApiClient(context)
        // Check if the user has granted permission to use Location Services
        permissions = Permissions()
    }

    // Prompt the user to check out of their location. Called when the "Check Out!" button
    // is clicked.
    fun checkOut() {
        try {
            progressBar!!.setVisibility(View.VISIBLE)
            val intentBuilder = PlacePicker.IntentBuilder()
            val intent = intentBuilder.build(getActivity())
            startActivityForResult(intent, REQUEST_PLACE_PICKER)
        } catch (e: GooglePlayServicesRepairableException) {
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    REQUEST_PLACE_PICKER)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Toast.makeText(context, string.install_google_play_services, Toast.LENGTH_LONG)
                    .show()
        }

    }

    // App bar Search View setUp
    fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.main, menu)
        val menuItem = menu.findItem(id.menu_search)
        searchView = menuItem.getActionView() as SearchView
        searchView!!.setOnQueryTextListener(this)
        searchView!!.setQueryHint(getString(string.search_nearby_places))
        searchView!!.setIconified(true)
    }

    fun onQueryTextSubmit(query: String): Boolean {
        mQuery = query
        progressBar!!.setVisibility(View.VISIBLE)
        Log.i(LOG_TAG, "The Search Query is: $query")
        val DEFAULT_ZOOM = 1500
        if (mNearbyPlaces == null) {
            val factory = NearbyPlacesListViewModelFactory(
                    NearbyPlacesRepository.getInstance(),
                    mQuery, latitude!!.toString(), longitude!!.toString(), DEFAULT_ZOOM, apiKey)
            queryViewModel = ViewModelProviders.of(this, factory).get(QueryNearbyPlacesViewModel::class.java)

        } else {
            queryViewModel!!.nearbyPlacesRepository = NearbyPlacesRepository.getInstance()
            queryViewModel!!.keyword = mQuery
            queryViewModel!!.latitude = latitude!!.toString()
            queryViewModel!!.longitude = longitude!!.toString()
            queryViewModel!!.radius = DEFAULT_ZOOM
            queryViewModel!!.apiKey = apiKey
            queryViewModel!!.markerOptions = mMarkerOptionsRetrieved
            queryViewModel!!.getNewPlaces()
        }

        queryViewModel!!.getData().observe(this, object : Observer<NearbyPlaces>() {
            fun onChanged(@Nullable nearbyPlaces: NearbyPlaces?) {
                if (nearbyPlaces != null) {
                    mNearbyPlaces = nearbyPlaces
                    val status = nearbyPlaces!!.getStatus()
                    if (status.matches("ZERO_RESULTS".toRegex())) {
                        val snackbar = Snackbar.make(rootView, string.no_results, Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                    mMarkerOptionsRetrieved = nearbyPlacesResponseParser!!.drawLocationMap(nearbyPlaces, mMap, mCurrentLocation, eventMarkerMap)
                    queryViewModel!!.markerOptions = mMarkerOptionsRetrieved
                    progressBar!!.setVisibility(View.INVISIBLE)
                    hideKeyboard(activity!!)
                    Log.i(LOG_TAG, "queryViewModel markerOptions on Rotation is" + queryViewModel!!.markerOptions.size())
                    queryViewModel!!.getData().removeObserver(this)
                } else {
                    val snackbar = Snackbar.make(rootView, string.check_internet_connection, Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            }
        })
        return true
    }

    fun onQueryTextChange(newText: String): Boolean {
        return false
    }

    fun onMenuItemActionExpand(item: MenuItem): Boolean {
        return true
    }

    fun onMenuItemActionCollapse(item: MenuItem): Boolean {
        return true
    }

    // Once the user has chosen a place, onActivityResult will be called, so we need to implement that now.
    // This code checks that the intent was successful and uses PlacePicker.getPlace() to obtain the chosen Place.
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(context, data)
                onPlacePickerPressedIntent(place)
                progressBar!!.setVisibility(View.INVISIBLE)
            } else if (resultCode == PlacePicker.RESULT_ERROR) {
                progressBar!!.setVisibility(View.INVISIBLE)
                Toast.makeText(context,
                        string.places_api_failure_msg,
                        Toast.LENGTH_LONG).show()
            } else {
                super.onActivityResult(requestCode, resultCode, data)
                progressBar!!.setVisibility(View.INVISIBLE)
            }
        }
    }

    // Get the last known location of the device
    fun getLastLocation() {
        if (permissions!!.checkLocalPermission(context, getActivity())) {
            locationTask = LocationServices.getFusedLocationProviderClient(context).getLastLocation()
            locationTask!!.addOnSuccessListener({ location ->
                // GPS location can be null if GPS is switched off
                if (location != null) {
                    latitude = location!!.getLatitude()
                    longitude = location!!.getLongitude()
                    mCurrentLocation = LatLng(latitude, longitude)
                    Log.i(LOG_TAG,
                            "The Last location is: Latitude: $latitude Longitude: $longitude")
                } else {
                    latitude = PIAZZA_CASTELLO.latitude
                    longitude = PIAZZA_CASTELLO.longitude
                    mCurrentLocation = PIAZZA_CASTELLO
                    Log.i(LOG_TAG, "Could not fetch the GPS location, we set to the default one: $PIAZZA_CASTELLO")
                    val snackbar = Snackbar.make(rootView,
                            context!!.getResources().getString(string.check_internet_gps_msg),
                            Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            })
            locationTask!!.addOnFailureListener({ e ->
                Log.d(LOG_TAG, "Error trying to get last GPS location")
                e.printStackTrace()
                val snackbar = Snackbar
                        .make(rootView, string.check_internet_gps_msg,
                                Snackbar.LENGTH_LONG)
                snackbar.show()
            })
        }
    }

    fun onResume() {
        super.onResume()
        mapView!!.onResume()
        mapView!!.getMapAsync(this)

    }

    fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    fun onStop() {
        super.onStop()
        mapView!!.onStop()

    }

    fun onPause() {
        mapView!!.onPause()
        super.onPause()
    }

    fun onMapReady(googleMap: GoogleMap) {
        MapsInitializer.initialize(context)
        val style = MapStyleOptions.loadRawResourceStyle(context, raw.mapstyle_retro)
        googleMap.setMapStyle(style)
        //Once the Map is initialised we set Up an observer for changes to the Map
        // Check the Location permission is given before enabling setMyLocation to true.
        if (permissions!!.checkLocalPermission(context, getActivity())) {
            // If the Location Permission id Granted Enable Location on the Map
            googleMap.setMyLocationEnabled(true)
            googleMap.setBuildingsEnabled(true)
            googleMap.setOnMarkerClickListener(onMarkerClickListener)
            googleMap.setOnInfoWindowClickListener(onInfoWindowClickListener)
            if (mSavedInstanceisNull) {
                // Construct a CameraPosition focusing on Piazza Castello, Turin Italy and animate the camera to that position.
                val cameraPosition = CameraPosition.Builder()
                        .target(mCurrentLocation)      // Sets the center of the map to Piazza Castello
                        .zoom(13)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build()
                // Creates a CameraPosition from the builder
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }

            if (queryViewModel != null) {
                for (i in 0 until queryViewModel!!.markerOptions.size()) {
                    Log.i(LOG_TAG, "On Rotation Map Marker size is " + queryViewModel!!.markerOptions.size())
                    val m = queryViewModel!!.markerOptions.get(i)
                    googleMap.addMarker(m)
                }
            }
        }
        getLastLocation()
        mMap = googleMap
    }

    fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
        googleMapsApi!!.DisconnectGoogleApiClient()
        googleMapsApi = null
        LostInTurin.getRefWatcher(context).watch(this)
    }

    fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    fun onSaveInstanceState(@NonNull outState: Bundle) {
        outState.putDouble(CURRENT_LATITUDE_TAG, latitude)
        outState.putDouble(CURRENT_LONGITUDE_TAG, longitude)
        outState.putString(CURRENT_QUERY_TAG, mQuery)
        outState.putInt(MARKERS_TAG_KEY, placeIdInt)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView!!.onSaveInstanceState(mapViewBundle)
    }

    private fun onClick(v: View) {
        this@MapFragment.checkOut()
    }

    private fun onInfoWindowClick(marker: Marker) {
        searchView!!.isIconified()
        searchView!!.onActionViewCollapsed()
        mPlaceIdTag = Integer.valueOf(marker.getSnippet())
        //   detailViewModel.getPlaceDetails().getValue().getResult();
        sharedModel!!.select(queryViewModel!!.getData().getValue().getResults().get(mPlaceIdTag))
        // launch the detail fragment.
        this@MapFragment.onMarkerPressedIntent(marker)
        marker.hideInfoWindow()
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(marker: Marker)
        fun OnPlacePickerInteraction(place: Place)
    }

    companion object {

        private val LOG_TAG = MapFragment::class.java!!.getSimpleName()
        private val CURRENT_LATITUDE_TAG = "CURRENT LATITUDE TAG"
        private val CURRENT_LONGITUDE_TAG = "CURRENT LONGITUDE TAG"
        private val CURRENT_QUERY_TAG = "CURRENT QUERY TAG"
        private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
        private val MARKERS_TAG_KEY = "MarkerTagKey"
        private val REQUEST_PLACE_PICKER = 1
        // A default location (Piazza Castello, Turin, Italy) and default zoom to use when location permission is
        private val PIAZZA_CASTELLO = LatLng(45.0710394, 7.6862986)

        fun hideKeyboard(activity: Activity) {
            val view = activity.findViewById(id.menu_search)
            if (view != null) {
                val imm = activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
            }
        }

        private fun onMarkerClick(marker: Marker): Boolean {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            return false
        }
    }
}


