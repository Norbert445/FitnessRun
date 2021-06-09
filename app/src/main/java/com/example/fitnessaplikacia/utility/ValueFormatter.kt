package com.example.fitnessaplikacia.utility

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat


class ValueFormatter : ValueFormatter() {
    private val mFormat: DecimalFormat

    init {
        mFormat = DecimalFormat("###,###,##0.00") // use one decimal
    }

    override fun getFormattedValue(value: Float): String {
        return "${String.format("%.2f",value)} km"
    }

}