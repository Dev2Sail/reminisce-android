package studio.hcmc.reminisce.ui.activity.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
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
//        viewBinding.searchLocationField.setEndIconDrawable(R.drawable.round_search_16)
        viewBinding.searchLocationField.editText!!.addTextChangedListener {

        }



    }
}
/*
inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty() && inputField.text.length >= 5
        }
 */