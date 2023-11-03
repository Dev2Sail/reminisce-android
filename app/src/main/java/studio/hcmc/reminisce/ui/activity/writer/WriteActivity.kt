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
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.databinding.ActivityWriteBinding
import studio.hcmc.reminisce.dto.location.LocationDTO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.ui.activity.map.SearchLocationActivity
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.util.string

class WriteActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteBinding
    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val writeOptions = HashMap<String, Any?>()

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
        viewBinding.writeAppbar.appbarActionButton1.setOnClickListener {
            if (writeOptions["visitedAt"] == null) {
                Toast.makeText(this, "방문 날짜를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
            if (writeOptions["place"] == null) {
                Toast.makeText(this, "방문 장소를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
            if (writeOptions["place"] != null && writeOptions["visitedAt"] != null) {
                writeOptions["body"] = viewBinding.writeTextContainer.string
                postContents()
            }
        }
        viewBinding.writeVisitedAt.setOnClickListener { WriteSelectVisitedAtDialog(this@WriteActivity, visitedAtDelegate) }
        viewBinding.writeLocation.setOnClickListener {
            val intent = Intent(this, SearchLocationActivity::class.java)
            searchLocationLauncher.launch(intent)
        }
    }

    private fun postContents() = CoroutineScope(Dispatchers.IO).launch {
        val dto = LocationDTO.Post().apply {
            this.categoryId = this@WriteActivity.categoryId
            markerEmoji = null
            this.title = writeOptions["place"] as String
            body = writeOptions["body"] as String
            latitude = writeOptions["latitude"] as Double
            longitude = writeOptions["longitude"] as Double
            visitedAt = writeOptions["visitedAt"] as String
        }

        runCatching { LocationIO.post(dto) }
            .onSuccess {
                withContext(Dispatchers.Main) {
                    val intent = Intent(this@WriteActivity, WriteOptionsActivity::class.java)
                        .putExtra("locationId", it.id)
                        .putExtra("categoryId", categoryId)
                        .putExtra("visitedAt", writeOptions["visitedAt"].toString())
                        .putExtra("place", writeOptions["place"].toString())
                    writeOptionsLauncher.launch(intent)
                    finish()
                }
            }.onFailure { LocalLogger.e(it) }
    }

    private val visitedAtDelegate = object : WriteSelectVisitedAtDialog.Delegate {
        override fun onSaveClick(date: String) {
            viewBinding.writeVisitedAt.text = date
            writeOptions["visitedAt"] = date
        }
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