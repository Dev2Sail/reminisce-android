package studio.hcmc.reminisce.ui.activity.map

import android.app.Activity
import android.view.LayoutInflater
import studio.hcmc.reminisce.databinding.DialogMarkerDetailBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog

class MarkerDetailDialog(
    activity: Activity,
    delegate: Delegate,
    placeName: String,
    address: String
) {
    interface Delegate {
        fun onClick(placeName: String)
    }

    init {
        val viewBinding = DialogMarkerDetailBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogMarkerDetailPlace.text = placeName
        viewBinding.dialogMarkerDetailAddress.text = address
        viewBinding.dialogMarkerDetailNext.setOnClickListener { delegate.onClick(placeName) }
        viewBinding.dialogMarkerDetailNextIcon.setOnClickListener { delegate.onClick(placeName) }
        dialog.show()
    }
}