package me.ilich.vel.model

import me.ilich.vel.R

sealed class Theme(
        val titleResId: Int,
        val themeResId: Int
) {

    class Forest : Theme(R.string.theme_forest_title, R.style.AppTheme_Forest)

    class Eggplant : Theme(R.string.theme_eggplant_title, R.style.AppTheme_Eggplant)

    class Sea : Theme(R.string.theme_sea_title, R.style.AppTheme_Sea)

}