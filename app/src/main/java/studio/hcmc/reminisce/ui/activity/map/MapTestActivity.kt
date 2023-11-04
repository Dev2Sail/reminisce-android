package studio.hcmc.reminisce.ui.activity.map

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MapConstants
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityMapTestBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.util.LocalLogger

class MapTestActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var viewBinding: ActivityMapTestBinding
    private lateinit var naverMap: NaverMap
    private var mapView: MapView? = null

//    private val placeInfo = ArrayList<MarkerInfo>()

    private val coords = ArrayList<SavedCoords>()
    private val markers = ArrayList<Marker>()

    private val bounds = LatLngBounds.Builder()

    data class SavedCoords(
        val longitude: Double,
        val latitude: Double
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMapTestBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        preparePlace()

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
        mapView = viewBinding.testNavermap
        mapView?.getMapAsync(this) // onMapReady 호출
        mapView?.onCreate(savedInstanceState)
        prepareMarkers()

    }

    private fun preparePlace() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@MapTestActivity)
        runCatching { LocationIO.listByUserId(user.id) }
            .onSuccess {
                for (location in it) {
                    coords.add(SavedCoords(location.longitude, location.latitude))
                }
            }.onFailure { LocalLogger.e(it) }
    }

    private fun prepareMarkers() {
        for (i in 0 until coords.size) {
            markers[i] = Marker().apply {
                map = naverMap
                icon = MarkerIcons.BLACK
                iconTintColor = getColor(R.color.md_theme_light_primary)
                isHideCollidedSymbols = true
                position = LatLng(coords[i].longitude, coords[i].latitude)
            }
//            val marker = Marker().apply {
//                map = naverMap
//                icon = MarkerIcons.BLACK
//                iconTintColor = getColor(R.color.md_theme_light_primary)
//                isHideCollidedSymbols = true
//                position = LatLng(coords[i].longitude, coords[i].latitude)
//            }
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = MapConstants.MIN_ZOOM_KOREA
        uiSetting()

    }

    private fun initView() {
        // display multiple marker Kakao CTA


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

    private fun buildBounds() {

        bounds
            .include(LatLng(37.4462920026041, 126.372737043106))
            .include(LatLng(37.49592797039814, 126.56541222674704))
            .include(LatLng(37.47186060562196, 126.66066670198887))
            .include(LatLng(37.46989474547404, 126.66071402353639))
            .build()
    }

    private fun marker() {
        val defaultMarker = Marker()
        defaultMarker.map = naverMap
        defaultMarker.icon = MarkerIcons.BLACK
        defaultMarker.iconTintColor = getColor(R.color.md_theme_light_primary)
        defaultMarker.isHideCollidedSymbols = true



    }

    private fun uiSetting() {
        naverMap.isIndoorEnabled = true // 실내지도 활성화

        val mapUiSetting = naverMap.uiSettings
        mapUiSetting.isLocationButtonEnabled = true // 위치 추적 모드 표현
        mapUiSetting.isZoomControlEnabled = true
        mapUiSetting.isTiltGesturesEnabled = false
        mapUiSetting.isRotateGesturesEnabled = false
        mapUiSetting.isScaleBarEnabled = true
        mapUiSetting.isCompassEnabled = false // 나침반 활성화
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
//        val bounds = LatLngBounds.Builder() // 마커 여러 개
//            // user의 location 조회 후 latitude, longitude insert
//            .include(LatLng(37.5640984, 126.9712268))
//            .build()

//        val infoWindow = InfoWindow() // display marker information
//        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
//            override fun getText(p0: InfoWindow): CharSequence {
//                return "장소"
//
//            }
//        }
//
//        val markerListener = Overlay.OnClickListener {
//            val marker = it as Marker
//
//            if (marker.infoWindow == null) {
//                infoWindow.open(marker)
//            } else {
//                infoWindow.close()
//            }
//
//            true
//        }
//defaultMarker.onClickListener = markerListener
//        marker().onClickListener = markerListener

/*
defaultMarker.setOnClickListener {
            Toast.makeText(this, "Marker Click", Toast.LENGTH_SHORT).show()
            // OnMapClick x
            true
        }
 */