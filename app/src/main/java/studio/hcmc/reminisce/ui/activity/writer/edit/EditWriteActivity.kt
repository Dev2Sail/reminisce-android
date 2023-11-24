package studio.hcmc.reminisce.ui.activity.writer.edit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.databinding.ActivityEditWriteBinding
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.vo.location.LocationVO

class EditWriteActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityEditWriteBinding
    private lateinit var location: LocationVO

    private val locationId by lazy { intent.getIntExtra("locationId", -1) }
    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }

    private val writeOptions = HashMap<String, Any?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityEditWriteBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        prepareLocation()
    }

    private fun initView() {
        viewBinding.editWriteAppbar.appbarTitle.text = location.title
        viewBinding.editWriteAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.editWriteAppbar.appbarActionButton1.setOnClickListener {
            // TODO location put
        }
        viewBinding.editWriteVisitedAt.text = location.visitedAt
        viewBinding.editWriteLocation.text = location.roadAddress
        if (!location.markerEmoji.isNullOrEmpty()) {
            viewBinding.editWriteMarkerEmoji.text = location.markerEmoji
        }
//        viewBinding.editWriteTextContainer.placeholderText = location.body // no
        // body 내용 띄우기
        viewBinding.editWriteTextContainer.string = location.body

    }

    private fun prepareLocation() = CoroutineScope(Dispatchers.IO).launch {
        val result = runCatching { LocationIO.getById(locationId) }
            .onSuccess { location = it }
            .onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            withContext(Dispatchers.Main) { initView() }
        }
    }
}