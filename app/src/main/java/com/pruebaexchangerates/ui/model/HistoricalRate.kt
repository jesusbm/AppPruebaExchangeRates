package com.pruebaexchangerates.ui.model

data class RateModel(

    val baseCurrency: String,

    val otherCurrency: String,

    val rate: Double,

    val date: String
) {
    companion object {

        val dateStringComparator = object : Comparator<RateModel> {
            override fun compare(o1: RateModel, o2: RateModel): Int {
                val parts1 = o1.date.replace("-0", "-").split("-").map { it.toInt() }
                val parts2 = o2.date.replace("-0", "-").split("-").map { it.toInt() }
                if (parts1[0] != parts2[0]) {
                    return parts1[0] - parts2[0]
                }
                if (parts1[1] != parts2[1]) {
                    return parts1[1] - parts2[1]
                }
                return parts1[2] - parts2[2]
            }
        }
    }
}
