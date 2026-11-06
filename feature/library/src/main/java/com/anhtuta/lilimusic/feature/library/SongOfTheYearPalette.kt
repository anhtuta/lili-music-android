package com.anhtuta.lilimusic.feature.library

/**
 * Web `BaiHatTable.js` Song-of-the-Year badge colours: unique years descending,
 * rainbow background, dark text on the first half of the palette.
 */
object SongOfTheYearPalette {
    val backgrounds =
        intArrayOf(
            0xFFFF3311.toInt(),
            0xFFFF4422.toInt(),
            0xFFFF6644.toInt(),
            0xFFFF9933.toInt(),
            0xFFFEAE2D.toInt(),
            0xFFCCBB33.toInt(),
            0xFFD0C310.toInt(),
            0xFFAACC22.toInt(),
            0xFF69D025.toInt(),
            0xFF22CCAA.toInt(),
            0xFF12BDB9.toInt(),
            0xFF11AABB.toInt(),
            0xFF4444DD.toInt(),
            0xFF3311BB.toInt(),
            0xFF3B0CBD.toInt(),
            0xFF442299.toInt(),
        )

    const val LIGHT_TEXT = 0xFFFFFFFF.toInt()
    const val DARK_TEXT = 0xFF000000.toInt()

    fun uniqueYearsDescending(years: Iterable<Int?>): List<Int> =
        years.filterNotNull().distinct().sortedDescending()

    fun backgroundArgb(
        year: Int?,
        uniqueDescending: List<Int>,
    ): Int {
        if (year == null) return backgrounds.last()
        val index = uniqueDescending.indexOf(year)
        return if (index in backgrounds.indices) backgrounds[index] else backgrounds.last()
    }

    fun contentArgb(
        year: Int?,
        uniqueDescending: List<Int>,
    ): Int {
        if (year == null) return LIGHT_TEXT
        val index = uniqueDescending.indexOf(year)
        return if (index >= 0 && index < backgrounds.size / 2) DARK_TEXT else LIGHT_TEXT
    }
}
