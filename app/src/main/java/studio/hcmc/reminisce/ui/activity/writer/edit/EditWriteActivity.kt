package studio.hcmc.reminisce.ui.activity.writer.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityEditWriteBinding
import studio.hcmc.reminisce.dto.location.LocationDTO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.ui.activity.map.SearchLocationActivity
import studio.hcmc.reminisce.ui.activity.writer.StopWritingDialog
import studio.hcmc.reminisce.ui.activity.writer.WriteOptionsActivity
import studio.hcmc.reminisce.ui.activity.writer.WriteSelectEmojiDialog
import studio.hcmc.reminisce.ui.activity.writer.WriteSelectVisitedAtDialog
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.vo.location.LocationVO

class EditWriteActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityEditWriteBinding
    private lateinit var location: LocationVO

    private val locationId by lazy { intent.getIntExtra("locationId", -1) }
    private val position by lazy { intent.getIntExtra("position", -1) }
    private val writeOptions = HashMap<String, Any?>()
    private val searchLocationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onSearchLocationResult)
    private val writeOptionsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onWriteOptionsResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityEditWriteBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        prepareLocation()
    }

    private fun initView() {
        viewBinding.editWriteAppbar.appbarTitle.text = location.title
        viewBinding.editWriteTextContainer.string = location.body
        viewBinding.editWriteVisitedAt.text = location.visitedAt
        viewBinding.editWriteLocation.text = location.roadAddress
        if (!location.markerEmoji.isNullOrEmpty()) {
            viewBinding.editWriteMarkerEmoji.text = location.markerEmoji
        }
        prepareOptions()
        viewBinding.editWriteAppbar.appbarBack.setOnClickListener { StopWritingDialog(this, stopDialogDelegate) }
        viewBinding.editWriteAppbar.appbarActionButton1.setOnClickListener { onValidate() }
        viewBinding.editWriteVisitedAt.setOnClickListener { WriteSelectVisitedAtDialog(this, visitedAtDelegate) }
        viewBinding.editWriteLocation.setOnClickListener { launchSearchLocation() }
        viewBinding.editWriteMarkerEmoji.setOnClickListener { WriteSelectEmojiDialog(this, emojiDelegate) }
    }

    private fun prepareOptions() {
        writeOptions["emoji"] = location.markerEmoji
        writeOptions["latitude"] = location.latitude
        writeOptions["longitude"] = location.longitude
        writeOptions["roadAddress"] = location.roadAddress
        writeOptions["place"] = location.title
        writeOptions["body"] = location.body
        writeOptions["visitedAt"] = location.visitedAt
    }

    private fun prepareLocation() = CoroutineScope(Dispatchers.IO).launch {
        val result = runCatching { LocationIO.getById(locationId) }
            .onSuccess { location = it }
            .onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            withContext(Dispatchers.Main) { initView() }
        }
    }

    private fun onValidate() {
        writeOptions["visitedAt"] ?: Toast.makeText(this, getString(R.string.error_visited_at_miss), Toast.LENGTH_SHORT).show()
        writeOptions["place"] ?: Toast.makeText(this, getString(R.string.error_location_miss), Toast.LENGTH_SHORT).show()
        if (writeOptions["place"] != null && writeOptions["visitedAt"] != null) {
            writeOptions["body"] = viewBinding.editWriteTextContainer.string
            preparePutContents()
        }
    }

    private fun preparePutContents() {
        val dto = LocationDTO.Put().apply {
            this.categoryId = location.categoryId
            this.markerEmoji = writeOptions["emoji"] as String?
            this.latitude = writeOptions["latitude"] as Double
            this.longitude = writeOptions["longitude"] as Double
            this.roadAddress = writeOptions["roadAddress"] as String
            this.title = writeOptions["place"] as String
            this.body = writeOptions["body"] as String
            this.visitedAt = writeOptions["visitedAt"] as String
        }
        putContents(dto)
    }

    private fun putContents(dto: LocationDTO.Put) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.put(locationId, dto) }
            .onSuccess {
                location = LocationIO.getById(locationId)
                launchWriteOptions(location.id, position)
            }.onFailure { LocalLogger.e(it) }
    }

    private val visitedAtDelegate = object : WriteSelectVisitedAtDialog.Delegate {
        override fun onSaveClick(date: String) {
            viewBinding.editWriteVisitedAt.text = date
            writeOptions["visitedAt"] = date
        }
    }

    private val emojiDelegate = object : WriteSelectEmojiDialog.Delegate {
        override fun onSaveClick(value: String?) {
            viewBinding.editWriteMarkerEmoji.text = value
            writeOptions["emoji"] = value
        }
    }

    private val stopDialogDelegate = object : StopWritingDialog.Delegate {
        override fun onClick() { finish() }
    }

    private fun onSearchLocationResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val place = activityResult.data?.getStringExtra("place")
            val roadAddress = activityResult.data?.getStringExtra("roadAddress")
            val longitude = activityResult.data?.getDoubleExtra("longitude", -1.0)
            val latitude = activityResult.data?.getDoubleExtra("latitude", -1.0)

            viewBinding.editWriteLocation.text = roadAddress
            viewBinding.editWriteAppbar.appbarTitle.text = place
            if (writeOptions["visitedAt"] != null) {
                viewBinding.editWriteVisitedAt.text = writeOptions["visitedAt"].toString()
            }
            writeOptions["place"] = place!!
            writeOptions["roadAddress"] = roadAddress
            writeOptions["longitude"] = longitude!!
            writeOptions["latitude"] = latitude!!
        }
    }

    private fun launchSearchLocation() {
        val intent = Intent(this, SearchLocationActivity::class.java)
            .putExtra("categoryId", location.categoryId)
        searchLocationLauncher.launch(intent)
    }

    private fun launchWriteOptions(locationId: Int, position: Int) {
        val intent = Intent(this, WriteOptionsActivity::class.java)
            .putExtra("locationId", locationId)
            .putExtra("categoryId", location.categoryId)
            .putExtra("visitedAt", writeOptions["visitedAt"].toString())
            .putExtra("place", writeOptions["place"].toString())
            .putExtra("position", position)
        writeOptionsLauncher.launch(intent)
    }

    private fun onWriteOptionsResult(activityResult: ActivityResult) {
//        if (activityResult.data?.getBooleanExtra("isAdded", false) == true) {
//            val locationId = activityResult.data?.getIntExtra("locationId", -1)
//            launchAddedWriteDetail(locationId!!)
//        }
        if (activityResult.data?.getBooleanExtra("isModified", false) == true) {
            val locationId = activityResult.data?.getIntExtra("locationId", -1)
            val position = activityResult.data?.getIntExtra("position", -1)
            toWriteDetail(locationId!!, position!!)
        }
    }

//    private fun launchAddedWriteDetail(locationId: Int) {
//        Intent()
//            .putExtra("isAdded", true)
//            .putExtra("locationId", locationId)
//            .setActivity(this, Activity.RESULT_OK)
//        finish()
//    }

    private fun toWriteDetail(locationId: Int, position: Int) {
        Intent()
            .putExtra("isModified", true)
            .putExtra("locationId", locationId)
            .putExtra("position", position)
            .setActivity(this, Activity.RESULT_OK)
        finish()
    }
}