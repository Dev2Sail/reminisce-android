package studio.hcmc.reminisce.ui.activity.map

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.databinding.ActivitySearchLocationBinding
import studio.hcmc.reminisce.io.kakao.KKPlaceInfo
import studio.hcmc.reminisce.io.ktor_client.KakaoIO
import studio.hcmc.reminisce.io.ktor_client.MoisIO
import studio.hcmc.reminisce.ui.activity.writer.WriteActivity
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.util.string

class SearchLocationActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySearchLocationBinding
    private lateinit var places: List<KKPlaceInfo>
    private lateinit var adapter: SearchLocationAdapter

//    private var categoryId = -1
    private val contents = ArrayList<SearchLocationAdapter.Content>()
    private val placeInfo = HashMap<String /* placeId */, Place>()
    private val roadAddress = HashMap<String /* placeId*/, String>()

    private data class Place(
        val placeName: String,
        val longitude: String,
        val latitude: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySearchLocationBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
//        LocalLogger.v("Search Location Activity -- CategoryId:${categoryId}")
        viewBinding.searchLocationBackIcon.setOnClickListener { finish() }
        viewBinding.searchLocationField.editText!!.setOnEditorActionListener { _, actionId, event ->
            val input = viewBinding.searchLocationField.string
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event.keyCode == KeyEvent.KEYCODE_SEARCH && event.action == KeyEvent.ACTION_DOWN) {
                fetchSearchResult(input)

                return@setOnEditorActionListener true
            }

            false
        }
//        getDefaultCategoryId()
    }

    private fun fetchSearchResult(value: String) = CoroutineScope(Dispatchers.IO).launch {
        val result = runCatching { KakaoIO.listByKeyword(value) }
            .onSuccess {
                places = it.documents
                for (document in it.documents) {
                    placeInfo[document.id] = Place(document.place_name, document.x, document.y)
                    roadAddress[document.id] = document.road_address_name.ifEmpty { getRoadAddress(document.address_name) }
                }
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        }
    }

    private suspend fun getRoadAddress(address: String): String = withContext(Dispatchers.IO) {
        runCatching { MoisIO.getRoadAddress(address) }
            .onSuccess {
                for (jusoInfo in it.results.juso) {
                    return@withContext jusoInfo.roadAddr
                }
            }.onFailure { LocalLogger.e(it) }
    }.toString()

    private fun prepareContents() {
        for (place in places) {
            contents.add(SearchLocationAdapter.PlaceContent(
                place.id,
                place.place_name,
                place.category_name.split(">").last().trim(' '),
                roadAddress[place.id]!!
            ))
        }
        //places.mapTo(contents) { SearchLocationAdapter.PlaceContent(it.place_name, it.category_name.split(">").last(), it.road_address_name) }
    }

    private fun onContentsReady() {
        viewBinding.searchLocationItems.layoutManager = LinearLayoutManager(this)
        adapter = SearchLocationAdapter(adapterDelegate, itemDelegate)
        viewBinding.searchLocationItems.adapter = adapter
    }

    private val adapterDelegate = object : SearchLocationAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val itemDelegate = object : SearchLocationItemViewHolder.Delegate {
        override fun onClick(placeId: String) {
            Intent().apply {
                putExtra("place", placeInfo[placeId]!!.placeName)
                putExtra("roadAddress", roadAddress[placeId])
                putExtra("longitude", placeInfo[placeId]!!.longitude.toDouble())
                putExtra("latitude", placeInfo[placeId]!!.latitude.toDouble())
                setActivity(this@SearchLocationActivity, Activity.RESULT_OK)
            }
            launchWrite(placeId)
        }
    }

    // 잘못된 게 아닌가
    private fun launchWrite(placeId: String) {
        Intent(this, WriteActivity::class.java).apply {
//            putExtra("categoryId", categoryId)
            putExtra("place", placeInfo[placeId]!!.placeName)
            putExtra("roadAddress", roadAddress[placeId])
            putExtra("longitude", placeInfo[placeId]!!.longitude.toDouble())
            putExtra("latitude", placeInfo[placeId]!!.latitude.toDouble())
            startActivity(this)
        }
    }
//    private fun getDefaultCategoryId() = CoroutineScope(Dispatchers.IO).launch {
//        val user = UserExtension.getUser(this@SearchLocationActivity)
//        runCatching { CategoryIO.getDefaultCategoryIdByUserId(user.id) }
//            .onSuccess { categoryId = it.id }
//            .onFailure { LocalLogger.e(it) }
//    }
}