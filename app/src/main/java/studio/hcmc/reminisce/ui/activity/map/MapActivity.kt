package studio.hcmc.reminisce.ui.activity.map

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityMapBinding
import studio.hcmc.reminisce.databinding.LayoutCustomMarkerBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.ui.activity.map.place.PlaceActivity
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.navigationController

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var viewBinding: ActivityMapBinding
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    private var mapView: MapView? = null
    private val markerInfo = HashMap<String, Place>()
    private val markers = ArrayList<Marker>(markerInfo.size)
    private val customMarkerInfo = HashMap<String, PlaceWithEmoji>()
    private val customMarkers = ArrayList<Marker>(customMarkerInfo.size)

    private data class Place(
        val address: String,
        val latitude: Double,
        val longitude: Double
    )

    private data class PlaceWithEmoji(
        val emoji: String,
        val address: String,
        val latitude: Double,
        val longitude: Double
    )

    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }

        mapView = viewBinding.navermapMapView
        mapView?.getMapAsync(this)
        mapView?.onCreate(savedInstanceState)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        initView()
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.locationSource = locationSource
//        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        preparePlace()
        prepareSetting()
    }

    private fun preparePlace() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@MapActivity)
        val result = runCatching { LocationIO.allByUserId(user.id) }
            .onSuccess { it -> it.forEach {
                if (it.markerEmoji.isNullOrEmpty()) {
                    // customMarker 우선순위가 더 높음
                    if (!markerInfo.containsKey(it.title)) {
                        markerInfo[it.title] = Place(it.roadAddress, it.latitude, it.longitude)
                    }
                } else {
                    if (!customMarkerInfo.containsKey(it.title)) {
                        customMarkerInfo[it.title] = PlaceWithEmoji(it.markerEmoji!!, it.roadAddress, it.latitude, it.longitude)
                    }
                } }
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            withContext(Dispatchers.Main) { buildMarkers() }
        }
    }

    private fun buildMarkers() {
//        markerInfo.forEach { markers.add(prepareMarker(it.key, it.value.address, it.value.latitude, it.value.longitude)) }
//        customMarkerInfo.forEach { customMarkers.add(prepareCustomMarker(it.value.emoji, it.key, it.value.address, it.value.latitude, it.value.longitude)) }
        for (place in markerInfo) {
            markers.add(prepareMarker(
                place.key,
                place.value.address,
                place.value.latitude,
                place.value.longitude
            ))
        }
        for (place in customMarkerInfo) {
            customMarkers.add(prepareCustomMarker(
                place.value.emoji,
                place.key,
                place.value.address,
                place.value.latitude,
                place.value.longitude
            ))
        }
    }

    private fun prepareMarker(place: String, address: String, latitude: Double, longitude: Double): Marker {
        return Marker().apply {
            position = LatLng(latitude, longitude)
            icon = MarkerIcons.BLACK
            iconTintColor = getColor(R.color.md_theme_light_primary)
            isHideCollidedSymbols = true
            isHideCollidedMarkers = true
            setOnClickListener {
                MarkerDetailDialog(this@MapActivity, detailDialogDelegate, place, address)

                false
            }
            map = naverMap
        }
    }

    private fun prepareCustomMarker(emoji: String, place: String, address: String, latitude: Double, longitude: Double): Marker {
        val emojiView = LayoutCustomMarkerBinding.inflate(layoutInflater)
        emojiView.root.text = emoji
        return Marker().apply {
            position = LatLng(latitude, longitude)
            icon = OverlayImage.fromView(emojiView.root)
            isHideCollidedSymbols = true
            isHideCollidedMarkers = true
            zIndex = 100
            setOnClickListener {
                MarkerDetailDialog(this@MapActivity, detailDialogDelegate, place, address)

                false
            }
            map = naverMap
        }
    }

    private val detailDialogDelegate = object : MarkerDetailDialog.Delegate {
        override fun onClick(placeName: String) {
            moveToPlace(placeName)
        }
    }

    private fun moveToPlace(value: String) {
        Intent(this, PlaceActivity::class.java).apply {
            putExtra("location", value)
            startActivity(this)
        }
    }

    private fun prepareSetting() {
        naverMap.isIndoorEnabled = true // 실내지도 활성화
        naverMap.uiSettings.apply {
            isLocationButtonEnabled = true // 위치 추적 모드 표현
            isZoomControlEnabled = true
            isZoomGesturesEnabled = true
            isTiltGesturesEnabled = false
            isRotateGesturesEnabled = false
            isScaleBarEnabled = true
            isCompassEnabled = false // 나침반 활성화
        }
    }

    private fun initView() {
        val menuId = intent.getIntExtra("menuId", -1)
        navigationController(viewBinding.mapNavView, menuId)
        viewBinding.mapHeaderSearchField.setOnClickListener {moveToSearchLocation() }
        // TODO permission check
//        ContextCompat.checkSelfPermission(
//            this, android.Manifest.permission.
//        )
    }

    private fun moveToSearchLocation() {
        Intent(this@MapActivity, SearchLocationActivity::class.java).apply {
            startActivity(this)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }

            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}