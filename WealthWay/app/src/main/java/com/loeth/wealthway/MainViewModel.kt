package com.loeth.wealthway

import androidx.lifecycle.ViewModel
import com.loeth.wealthway.data.InvestmentResult
import com.loeth.wealthway.logic.InvestmentCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val _results = MutableStateFlow<List<InvestmentResult>>(emptyList())
    val results: StateFlow<List<InvestmentResult>> = _results

    fun calculateEarnings(
        principal: Double,
        dailyRate: Double,
        withdrawalPercentage: Double,
        withdrawalCycle: Int
    ) {
        val calculator = InvestmentCalculator()
        _results.value = calculator.calculateEarnings(
            principal, dailyRate, withdrawalPercentage, withdrawalCycle
        )
    }
}
