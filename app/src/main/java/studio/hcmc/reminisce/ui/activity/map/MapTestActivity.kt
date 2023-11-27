package studio.hcmc.reminisce.ui.activity.map

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityMapTestBinding
import studio.hcmc.reminisce.databinding.LayoutCustomMarkerBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.util.LocalLogger

class MapTestActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var viewBinding: ActivityMapTestBinding
    private lateinit var naverMap: NaverMap

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMapTestBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }

        mapView = viewBinding.testNavermap
        mapView?.getMapAsync(this) // onMapReady 호출
        mapView?.onCreate(savedInstanceState)
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        prepareSetting()
        preparePlace()
    }

    private fun preparePlace() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@MapTestActivity)
        val result = runCatching { LocationIO.listByUserId(user.id, Int.MAX_VALUE) }
            .onSuccess { it -> it.forEach {
                if (it.markerEmoji.isNullOrEmpty()) {
                    if (!markerInfo.containsKey(it.title)) {
                        markerInfo[it.title] = Place(it.roadAddress, it.latitude, it.longitude)
                    }
                } else {
                    if (!customMarkerInfo.containsKey(it.title)) {
                        customMarkerInfo[it.title] = PlaceWithEmoji(
                            it.markerEmoji!!,
                            it.roadAddress,
                            it.latitude,
                            it.longitude
                        )
                    }
                } }
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) { withContext(Dispatchers.Main) { buildMarkers() } }
    }

    private fun buildMarkers() {
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
            setOnClickListener {
                MarkerDetailDialog(this@MapTestActivity, detailDialogDelegate, place, address)

                false
            }
            map = naverMap
        }
    }

    private fun prepareCustomMarker(emoji: String, place: String, address: String, latitude: Double, longitude: Double): Marker {
        val emojiView = LayoutCustomMarkerBinding.inflate(layoutInflater)
        emojiView.root.text = emoji
        val customMarker = Marker().apply {
            position = LatLng(latitude, longitude)
            icon = OverlayImage.fromView(emojiView.root)
            isHideCollidedSymbols = true
            setOnClickListener {
                MarkerDetailDialog(this@MapTestActivity, detailDialogDelegate, place, address)

                false
            }
            map = naverMap
        }

        return customMarker
    }

    private val detailDialogDelegate = object : MarkerDetailDialog.Delegate {
        override fun onClick(placeName: String) {
            // TODO getByTitle recyclerview
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

    private fun initOnClick() {


//        naverMap.setOnMapClickListener { pointF, latLng ->
////            infoWindow.close()
//            InfoWindow().close()
//            Toast.makeText(this, "${latLng.latitude}, ${latLng.longitude}", Toast.LENGTH_SHORT).show()
//        }
//        naverMap.setOnSymbolClickListener {
//            if (it.caption == "서울특별시청") {
//                Toast.makeText(this, "서울시청 클릭", Toast.LENGTH_SHORT).show()
//                // consume event, OnMapClick x
//                true
//            } else {
//                // OnMapClick o
//
//                false
//            }
//        }
//        naverMap.addOnLocationChangeListener { // 사용자 위치 변경 시
//            Toast.makeText(this, "${it.latitude}, ${it.longitude} // ${it.accuracy}", Toast.LENGTH_SHORT).show()
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        if (item.itemId == R.id.nav_main_home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
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
}

private fun buildBounds() {

//        bounds
//            .include(LatLng(37.4462920026041, 126.372737043106))
//            .include(LatLng(37.49592797039814, 126.56541222674704))
//            .include(LatLng(37.47186060562196, 126.66066670198887))
//            .include(LatLng(37.46989474547404, 126.66071402353639))
//            .build()
}


// https://github.com/navermaps/android-map-sdk/tree/master/app/src/main/java/com/naver/maps/map/demo/kotlin/overlay
// https://navermaps.github.io/maps.js.ncp/docs/tutorial-digest.example.html
// https://api.ncloud-docs.com/docs/ai-naver-mapsgeocoding-geocode
// https://velog.io/@soyoung-dev/AndroidKotlin-%EB%84%A4%EC%9D%B4%EB%B2%84-%EC%A7%80%EB%8F%84-API-%EB%A7%88%EC%BB%A4-%ED%81%B4%EB%9F%AC%EC%8A%A4%ED%84%B0%EB%A7%81