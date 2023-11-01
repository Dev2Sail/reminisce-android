package studio.hcmc.reminisce.ui.activity.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import studio.hcmc.reminisce.databinding.ActivitySearchLocationBinding

class SearchLocationActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySearchLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySearchLocationBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        viewBinding.searchLocationBackIcon.setOnClickListener { finish() }
//        viewBinding.searchLocationField.editText!!.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//            }
//        }


    }





}
/*
inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty() && inputField.text.length >= 5
        }
 */

/*
naver는 주소 <-> 좌표
https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode -> 위도, 경도 좌표 return

reverseGc
https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc


 */