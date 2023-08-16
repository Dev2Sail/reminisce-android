package studio.hcmc.reminisce.ui.view

import android.content.Context
import android.view.View
import com.google.android.material.chip.Chip
import studio.hcmc.reminisce.R
import com.google.android.material.R as MaterialR

class ChipConfig private constructor() {
    companion object {
        operator fun invoke(context: Context, configure: Chip.() -> Unit) = Chip(
            context,
            null,
            MaterialR.style.Widget_Material3_Chip_Suggestion_Elevated
        ).apply {
            shapeAppearanceModel = shapeAppearanceModel.withCornerSize(context.resources.getDimension(MaterialR.dimen.m3_chip_corner_size))
            layoutDirection = View.LAYOUT_DIRECTION_LOCALE
            setChipBackgroundColorResource(R.color.md_theme_light_secondaryContainer)
        }.apply(configure)
    }
}
