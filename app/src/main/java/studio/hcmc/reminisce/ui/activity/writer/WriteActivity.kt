package studio.hcmc.reminisce.ui.activity.writer

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
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteBinding
import studio.hcmc.reminisce.dto.location.LocationDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.CategoryIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.ui.activity.map.SearchLocationActivity
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.util.string

class WriteActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteBinding
    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val writeOptions = HashMap<String, Any?>()

    private var defaultCategoryId = -1
    private val searchLocationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onSearchLocationResult)
    private val writeOptionsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onOptionsResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.writeAppbar.appbarTitle.text = ""
        viewBinding.writeAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.writeAppbar.appbarActionButton1.setOnClickListener { onValidatePost() }
        viewBinding.writeVisitedAt.setOnClickListener { WriteSelectVisitedAtDialog(this, visitedAtDelegate) }
        viewBinding.writeMarkerEmoji.setOnClickListener { WriteSelectEmojiDialog(this, emojiDelegate) }
        viewBinding.writeLocation.setOnClickListener {
            searchLocationLauncher.launch(Intent(this, SearchLocationActivity::class.java))
        }
        fromSearchLocation()
    }

    private fun onValidatePost() {
        writeOptions["visitedAt"] ?: Toast.makeText(this, getString(R.string.error_visited_at_miss), Toast.LENGTH_SHORT).show()
        writeOptions["place"] ?: Toast.makeText(this, getString(R.string.error_location_miss), Toast.LENGTH_SHORT).show()
        if (writeOptions["place"] != null && writeOptions["visitedAt"] != null) {
            writeOptions["body"] = viewBinding.writeTextContainer.string
            prepareContents()
        }
    }

    private fun fromSearchLocation() {
        if (categoryId == -1) {
            getDefaultCategoryId()
            val place = intent.getStringExtra("place")
            viewBinding.writeAppbar.appbarTitle.text = place
            viewBinding.writeLocation.text = place
            writeOptions["place"] = place
            writeOptions["roadAddress"] = intent.getStringExtra("roadAddress")
            writeOptions["longitude"] = intent.getDoubleExtra("longitude", -1.0)
            writeOptions["latitude"] = intent.getDoubleExtra("latitude", -1.0)
        }
    }

    private fun getDefaultCategoryId() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteActivity)
        runCatching { CategoryIO.getDefaultCategoryIdByUserId(user.id) }
            .onSuccess { defaultCategoryId = it.id }
            .onFailure { LocalLogger.e(it) }
    }

    private fun prepareContents() {
        val dto = LocationDTO.Post().apply {
            this.markerEmoji = writeOptions["emoji"] as String?
            this.latitude = writeOptions["latitude"] as Double
            this.longitude = writeOptions["longitude"] as Double
            this.roadAddress = writeOptions["roadAddress"] as String
            this.title = writeOptions["place"] as String
            this.body = writeOptions["body"] as String
            this.visitedAt = writeOptions["visitedAt"] as String
        }
        dto.categoryId = if (categoryId == -1) defaultCategoryId else this.categoryId
        postContents(dto)
    }

    private fun postContents(dto: LocationDTO.Post) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationIO.post(dto) }
            .onSuccess { launchWriteOptions(it.id) }
            .onFailure { LocalLogger.e(it) }
    }

    private val visitedAtDelegate = object : WriteSelectVisitedAtDialog.Delegate {
        override fun onSaveClick(date: String) {
            viewBinding.writeVisitedAt.text = date
            writeOptions["visitedAt"] = date
        }
    }

    private val emojiDelegate = object : WriteSelectEmojiDialog.Delegate {
        override fun onSaveClick(value: String?) {
            viewBinding.writeMarkerEmoji.text = value
            writeOptions["emoji"] = value
        }
    }

    private fun launchWriteOptions(locationId: Int) {
        val finalCategoryId = if (categoryId == -1) defaultCategoryId else categoryId
        val intent = Intent(this@WriteActivity, WriteOptionsActivity::class.java)
            .putExtra("locationId", locationId)
            .putExtra("categoryId", finalCategoryId)
            .putExtra("visitedAt", writeOptions["visitedAt"].toString())
            .putExtra("place", writeOptions["place"].toString())
        writeOptionsLauncher.launch(intent)
        finish()
    }

    private fun onSearchLocationResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val place = activityResult.data?.getStringExtra("place")
            val roadAddress = activityResult.data?.getStringExtra("roadAddress")
            val longitude = activityResult.data?.getDoubleExtra("longitude", -1.0)
            val latitude = activityResult.data?.getDoubleExtra("latitude", -1.0)

            viewBinding.writeLocation.text = roadAddress
            viewBinding.writeAppbar.appbarTitle.text = place
            writeOptions["place"] = place!!
            writeOptions["roadAddress"] = roadAddress
            writeOptions["longitude"] = longitude!!
            writeOptions["latitude"] = latitude!!
        }
    }

    private fun onOptionsResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isAdded", false) == true) {
            Intent().putExtra("isAdded", true).setActivity(this, Activity.RESULT_OK)
            finish()
        }
    }
}